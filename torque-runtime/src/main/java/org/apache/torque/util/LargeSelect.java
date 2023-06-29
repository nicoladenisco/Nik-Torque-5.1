package org.apache.torque.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.sql.SqlBuilder;

/**
 * This class can be used to retrieve a large result set from a database query.
 * The query is started and then rows are returned a page at a time.  The <code>
 * LargeSelect</code> is meant to be placed into the Session or User.Temp, so
 * that it can be used in response to several related requests.  Note that in
 * order to use <code>LargeSelect</code> you need to be willing to accept the
 * fact that the result set may become inconsistent with the database if updates
 * are processed subsequent to the queries being executed.  Specifying a memory
 * page limit of 1 will give you a consistent view of the records but the totals
 * may not be accurate and the performance will be terrible.  In most cases
 * the potential for inconsistencies data should not cause any serious problems
 * and performance should be pretty good (but read on for further warnings).
 *
 * <p>The idea here is that the full query result would consume too much memory
 * and if displayed to a user the page would be too long to be useful.  Rather
 * than loading the full result set into memory, a window of data (the memory
 * limit) is loaded and retrieved a page at a time.  If a request occurs for
 * data that falls outside the currently loaded window of data then a new query
 * is executed to fetch the required data.  Performance is optimized by
 * starting a thread to execute the database query and fetch the results.  This
 * will perform best when paging forwards through the data, but a minor
 * optimization where the window is moved backwards by two rather than one page
 * is included for when a user pages past the beginning of the window.
 *
 * <p>As the query is performed in in steps, it is often the case that the total
 * number of records and pages of data is unknown.  <code>LargeSelect</code>
 * provides various methods for indicating how many records and pages it is
 * currently aware of and for presenting this information to users.
 *
 * <p><code>LargeSelect</code> utilizes the <code>Criteria</code> methods
 * <code>setOffset()</code> and <code>setLimit()</code> to limit the amount of
 * data retrieved from the database - these values are either passed through to
 * the DBMS when supported (efficient with the caveat below) or handled by
 * the Torque API when it is not (not so efficient).
 *
 * <p>As <code>LargeSelect</code> must re-execute the query each time the user
 * pages out of the window of loaded data, you should consider the impact of
 * non-index sort orderings and other criteria that will require the DBMS to
 * execute the entire query before filtering down to the offset and limit either
 * internally or via Torque.
 *
 * <p>The memory limit defaults to 5 times the page size you specify, but
 * alternative constructors and the class method <code>setMemoryPageLimit()
 * </code> allow you to override this for a specific instance of
 * <code>LargeSelect</code> or future instances respectively.
 *
 * <p>Typically you will create a <code>LargeSelect</code> using your <code>
 * Criteria</code> (perhaps created from the results of a search parameter
 * page), page size, memory page limit, return class name (for which you may
 * have defined a business object class before hand) and peer class
 * and place this in user.Temp thus:
 *
 * <pre>
 *     data.getUser().setTemp("someName", largeSelect);
 * </pre>
 *
 * <p>In your template you will then use something along the lines of:
 *
 * <pre>
 *    #set($largeSelect = $data.User.getTemp("someName"))
 *    #set($searchop = $data.Parameters.getString("searchop"))
 *    #if($searchop.equals("prev"))
 *      #set($recs = $largeSelect.PreviousResults)
 *    #else
 *      #if($searchop.equals("goto"))
 *        #set($recs = $largeSelect.getPage($data.Parameters.getInt("page", 1)))
 *      #else
 *        #set($recs = $largeSelect.NextResults)
 *      #end
 *    #end
 * </pre>
 *
 * <p>...to move through the records.  <code>LargeSelect</code> implements a
 * number of convenience methods that make it easy to add all of the necessary
 * bells and whistles to your template.
 *
 * @param <T> the type of the objects which are returned on a query.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id: LargeSelect.java 1879929 2020-07-16 07:42:57Z gk $
 */
public class LargeSelect<T> implements Runnable, Serializable
{
    /** Serial version */
    private static final long serialVersionUID = -1166842932571491942L;

    /** The number of records that a page consists of.  */
    private int pageSize;
    /** The maximum number of records to maintain in memory. */
    private int memoryLimit;

    /** The record number of the first record in memory. */
    private transient volatile int blockBegin = 0;
    /** The record number of the last record in memory. */
    private transient volatile int blockEnd;
    /** How much of the memory block is currently occupied with result data. */
    private volatile int currentlyFilledTo = -1;

    /** The memory store of results. */
    private transient List<T> results = null;

    /** The thread that executes the query. */
    private transient Thread thread = null;
    /**
     * A flag used to kill the thread when the currently executing query is no
     * longer required.
     */
    private transient volatile boolean killThread = false;
    /** A flag that indicates whether or not the query thread is running. */
    private transient volatile boolean threadRunning = false;
    /**
     * An indication of whether or not the current query has completed
     * processing.
     */
    private transient volatile boolean queryCompleted = false;
    /**
     * An indication of whether or not the totals (records and pages) are at
     * their final values.
     */
    private transient boolean totalsFinalized = false;

    /** The cursor position in the result set. */
    private int position;
    /** The total number of pages known to exist. */
    private int totalPages = -1;
    /** The total number of records known to exist. */
    private int totalRecords = 0;

    /** The criteria used for the query. */
    private Criteria criteria = null;

    /** The last page of results that were returned. */
    private transient List<T> lastResults;

    /**
     * The BasePeerImpl object that handles database selects.
     */
    private BasePeerImpl<T> peer = null;

    /** default MessageFormat pattern for the page progress text */ 
    public static final String DEFAULT_PAGE_PROGRESS_TEXT_PATTERN = "{0} of {1,choice,0#&gt; |1#}{2}";

    /**
     * The MessageFormat pattern to format a page progress.
     * The default <pre>{0} of {1,choice,0#&gt; |1#}{2}</pre> formats as <pre>2 of 3</pre>.
     * if the total number of records or pages is unknown, the pattern formats as <pre>2 of &gt; 3</pre>.
     * You can use <code>setPageProgressTextPattern()</code>
     * to change this to whatever value you like.
     */
    private String pageProgressTextPattern = DEFAULT_PAGE_PROGRESS_TEXT_PATTERN;

    /** default MessageFormat pattern for the record progress text */ 
    public static final String DEFAULT_RECORD_PROGRESS_TEXT_PATTERN = "{0} - {1} of {2,choice,0#&gt; |1#}{3}";
    
    /**
     * The MessageFormat pattern to format a record progress.
     * The default <pre>{0} - {1} of {2,choice,0#&gt; |1#}{3}</pre> formats as <pre>1 - 25 of 100</pre>.
     * if the total number of records or pages is unknown, the pattern formats as <pre>1 - 25 of &gt; 100</pre>.
     * You can use <code>setRecordProgressTextPattern()</code>
     * to change this to whatever value you like.
     */
    private String recordProgressTextPattern = DEFAULT_RECORD_PROGRESS_TEXT_PATTERN;

    /**
     * The default value for the maximum number of pages of data to be retained
     * in memory.
     */
    public static final int DEFAULT_MEMORY_LIMIT_PAGES = 5;

    /**
     * The maximum number of pages of data to be retained in memory.  Use
     * <code>setMemoryPageLimit()</code> to provide your own value.
     */
    private int memoryPageLimit = DEFAULT_MEMORY_LIMIT_PAGES;

    /**
     * The number of milliseconds to sleep when the result of a query
     * is not yet available.
     */
    private static final int QUERY_NOT_COMPLETED_SLEEP_TIME = 500;

    /**
     * The number of milliseconds to sleep before retrying to stop a query.
     */
    private static final int QUERY_STOP_SLEEP_TIME = 1000;

    /** A place to store search parameters that relate to this query. */
    private Map<String, String> params = null;

    /** Logging */
    private static final Logger log = LogManager.getLogger(LargeSelect.class);

    /**
     * Creates a LargeSelect whose results are returned as a <code>List</code>
     * containing a maximum of <code>pageSize</code> objects of the type
     * defined within the class named <code>returnBuilderClassName</code> at a
     * time, maintaining a maximum of <code>LargeSelect.memoryPageLimit</code>
     * pages of results in memory.
     *
     * @param criteria object used by BasePeer to build the query.  In order to
     * allow this class to utilize database server implemented offsets and
     * limits (when available), the provided criteria must not have any limit or
     * offset defined.
     *
     * @param pageSize number of rows to return in one block.
     * @param peerImpl the peer that will be used to do the select operations
     *
     * @throws IllegalArgumentException if <code>criteria</code> uses one or
     * both of offset and limit, if <code>pageSize</code> is less than 1 or
     * the Criteria object does not contain SELECT columns.
     */
    public LargeSelect(
            final Criteria criteria,
            final int pageSize,
            final BasePeerImpl<T> peerImpl)
    {
        this(
                criteria,
                pageSize,
                LargeSelect.DEFAULT_MEMORY_LIMIT_PAGES,
                peerImpl);
    }

    /**
     * Creates a LargeSelect whose results are returned as a <code>List</code>
     * containing a maximum of <code>pageSize</code> objects of the type T at a
     * time, maintaining a maximum of <code>memoryPageLimit</code> pages of
     * results in memory.
     *
     * @param criteria object used by BasePeerImpl to build the query.  In order to
     * allow this class to utilize database server implemented offsets and
     * limits (when available), the provided criteria must not have any limit or
     * offset defined.
     *
     * @param pageSize number of rows to return in one block.
     * @param memoryPageLimit maximum number of pages worth of rows to be held
     * in memory at one time.
     * @param peerImpl the peer that will be used to do the select operations
     *
     * @throws IllegalArgumentException if <code>criteria</code> uses one or
     * both of offset and limit, if <code>pageSize</code> or <code>
     * memoryLimitPages</code> are less than 1 or the Criteria object does not
     * contain SELECT columns..
     */
    public LargeSelect(
            final Criteria criteria,
            final int pageSize,
            final int memoryPageLimit,
            final BasePeerImpl<T> peerImpl)
    {
        this.peer = peerImpl;

        if (criteria.getOffset() != 0 || criteria.getLimit() != -1)
        {
            throw new IllegalArgumentException(
                    "criteria must not use Offset and/or Limit.");
        }

        if (pageSize < 1)
        {
            throw new IllegalArgumentException(
                    "pageSize must be greater than zero.");
        }

        if (memoryPageLimit < 1)
        {
            throw new IllegalArgumentException(
                    "memoryPageLimit must be greater than zero.");
        }

        this.pageSize = pageSize;
        this.memoryLimit = pageSize * memoryPageLimit;
        this.criteria = criteria;
        blockEnd = blockBegin + memoryLimit - 1;
        startQuery(pageSize);
    }

    /**
     * Retrieve a specific page, if it exists.
     *
     * @param pageNumber the number of the page to be retrieved - must be
     * greater than zero.  An empty <code>List</code> will be returned if
     * <code>pageNumber</code> exceeds the total number of pages that exist.
     * @return a <code>List</code> of query results containing a maximum of
     * <code>pageSize</code> results.
     * @throws IllegalArgumentException when <code>pageNo</code> is not
     * greater than zero.
     * @throws TorqueException if a sleep is unexpectedly interrupted.
     */
    public List<T> getPage(final int pageNumber) throws TorqueException
    {
        if (pageNumber < 1)
        {
            throw new IllegalArgumentException(
                    "pageNumber must be greater than zero.");
        }
        return getResults((pageNumber - 1) * pageSize);
    }

    /**
     * Gets the next page of rows.
     *
     * @return a <code>List</code> of query results containing a maximum of
     * <code>pageSize</code> results.
     * @throws TorqueException if a sleep is unexpectedly interrupted.
     */
    public List<T> getNextResults() throws TorqueException
    {
        if (!getNextResultsAvailable())
        {
            return getCurrentPageResults();
        }
        return getResults(position);
    }

    /**
     * Provide access to the results from the current page.
     *
     * @return a <code>List</code> of query results containing a maximum of
     * <code>pageSize</code> results.
     * @throws TorqueException if a sleep is unexpectedly interrupted.
     */
    public synchronized List<T> getCurrentPageResults() throws TorqueException
    {
        return null == lastResults && position > 0
                ? getResults(position) : lastResults;
    }

    /**
     * Gets the previous page of rows.
     *
     * @return a <code>List</code> of query results containing a maximum of
     * <code>pageSize</code> results.
     * @throws TorqueException if a sleep is unexpectedly interrupted.
     */
    public List<T> getPreviousResults() throws TorqueException
    {
        if (!getPreviousResultsAvailable())
        {
            return getCurrentPageResults();
        }

        int start;
        if (position - 2 * pageSize < 0)
        {
            start = 0;
        }
        else
        {
            start = position - 2 * pageSize;
        }
        return getResults(start);
    }

    /**
     * Gets a page of rows starting at a specified row.
     *
     * @param start the starting row.
     * @return a <code>List</code> of query results containing a maximum of
     * <code>pageSize</code> results.
     * @throws TorqueException if a sleep is unexpectedly interrupted.
     */
    private List<T> getResults(final int start) throws TorqueException
    {
        return getResults(start, pageSize);
    }

    /**
     * Gets a block of rows starting at a specified row and containing a
     * specified number of rows.
     *
     * @param start the starting row.
     * @param size the number of rows.
     * @return a <code>List</code> of query results containing a maximum of
     * <code>pageSize</code> results.
     * @throws IllegalArgumentException if <code>size &gt; memoryLimit</code> or
     * <code>start</code> and <code>size</code> result in a situation that is
     * not catered for.
     * @throws TorqueException if a sleep is unexpectedly interrupted.
     */
    private synchronized List<T> getResults(final int start, final int size)
            throws TorqueException
    {
        log.debug("getResults(start: {}, size: {}) invoked.", start, size);

        if (size > memoryLimit)
        {
            throw new IllegalArgumentException("size (" + size
                    + ") exceeds memory limit (" + memoryLimit + ").");
        }

        // Request was for a block of rows which should be in progress.
        // If the rows have not yet been returned, wait for them to be
        // retrieved.
        if (start >= blockBegin && (start + size - 1) <= blockEnd)
        {
            log.debug("getResults(): Sleeping until "
                        + "start+size-1 ({}) > currentlyFilledTo ({}) && !queryCompleted (!{})", 
                    () ->  { return start + size - 1; } , () -> currentlyFilledTo, () -> queryCompleted);
            while (((start + size - 1) > currentlyFilledTo) && !queryCompleted)
            {
                try
                {
                    wait(QUERY_NOT_COMPLETED_SLEEP_TIME);
                }
                catch (InterruptedException e)
                {
                    throw new TorqueException("Unexpected interruption", e);
                }
            }
        }

        // Going in reverse direction, trying to limit db hits so assume user
        // might want at least 2 sets of data.
        else if (start < blockBegin && start >= 0)
        {
            log.debug("getResults(): Paging backwards as start "
                    + "({}) < blockBegin ({}) && start >= 0",
                    () -> start, () -> blockBegin);
            stopQuery();
            if (memoryLimit >= 2 * size)
            {
                blockBegin = start - size;
                if (blockBegin < 0)
                {
                    blockBegin = 0;
                }
            }
            else
            {
                blockBegin = start;
            }
            blockEnd = blockBegin + memoryLimit - 1;
            startQuery(size);
            // Re-invoke getResults() to provide the wait processing.
            return getResults(start, size);
        }

        // Assume we are moving on, do not retrieve any records prior to start.
        else if ((start + size - 1) > blockEnd)
        {
            log.debug("getResults(): Paging past end of loaded data as start+size-1 "
                    + "({}) > blockEnd ({})",
                    start + size - 1, blockEnd);
            stopQuery();
            blockBegin = start;
            blockEnd = blockBegin + memoryLimit - 1;
            startQuery(size);
            // Re-invoke getResults() to provide the wait processing.
            return getResults(start, size);
        }
        
        else
        {
            throw new IllegalArgumentException("Parameter configuration not "
                    + "accounted for.");
        }

        int fromIndex = start - blockBegin;
        int toIndex = fromIndex + Math.min(size, results.size() - fromIndex);

        log.debug("getResults(): Retrieving records from results elements "
                + "start-blockBegin ({}) through fromIndex + "
                + "Math.min(size, results.size() - fromIndex) ({})",
                () -> fromIndex, () -> toIndex);

        List<T> returnResults;

        returnResults = new ArrayList<>(results.subList(fromIndex, toIndex));

        position = start + size;
        lastResults = returnResults;
        return returnResults;
    }

    /**
     * A background thread that retrieves the rows.
     */
    @Override
    public void run()
    {
        /* The connection to the database. */
        try (TorqueConnection conn = Transaction.begin(criteria.getDbName()))
        {
            results = new CopyOnWriteArrayList<>();

            criteria.setOffset(blockBegin);
            // Add 1 to memory limit to check if the query ends on a
            // page break.
            criteria.setLimit(memoryLimit + 1);

            /*
             * Fix criterions relating to booleanint or booleanchar columns
             * The defaultTableMap parameter in this call is null because we have
             * no default peer class inside LargeSelect. This means that all
             * columns not fully qualified will not be modified.
             */
            peer.correctBooleans(
                    criteria);
            peer.setDbName(criteria);

            // Execute the query.
            if (log.isDebugEnabled())
            {
                log.debug("run(): query = {}", SqlBuilder.buildQuery(criteria).toString());
                log.debug("run(): memoryLimit = {}", memoryLimit);
                log.debug("run(): blockBegin = {}", blockBegin);
                log.debug("run(): blockEnd = {}", blockEnd);
            }

            // Continue getting rows one page at a time until the memory limit
            // is reached, all results have been retrieved, or the rest
            // of the results have been determined to be irrelevant.
            boolean allRecordsRetrieved = false;
            while (!killThread
                    && !allRecordsRetrieved
                    && currentlyFilledTo + pageSize <= blockEnd)
            {
                log.debug("run(): Invoking BasePeerImpl.doSelect()");

                List<T> tempResults = peer.doSelect(
                        criteria,
                        conn);

                if (tempResults.size() < criteria.getLimit())
                {
                    allRecordsRetrieved = true;
                }

                boolean perhapsLastPage = true;
                int resultSetSize = tempResults.size();

                // If the extra record was indeed found then we know we are not
                // on the last page but we must now get rid of it.
                if (tempResults.size() == memoryLimit + 1)
                {
                    results.addAll(tempResults.subList(0, memoryLimit));
                    resultSetSize--;
                    perhapsLastPage = false;
                }
                else
                {
                    results.addAll(tempResults);
                }

                synchronized (this)
                {
                    currentlyFilledTo += resultSetSize;

                    if (results.size() > 0
                            && blockBegin + currentlyFilledTo >= totalRecords)
                    {
                        // Add 1 because index starts at 0
                        totalRecords = blockBegin + currentlyFilledTo + 1;
                    }

                    // if the db has limited the datasets, we must retrieve all
                    // datasets.
                    if (allRecordsRetrieved)
                    {
                        queryCompleted = true;
                        // The following ugly condition ensures that the totals are
                        // not finalized when a user does something like requesting
                        // a page greater than what exists in the database.
                        if (perhapsLastPage
                                && getCurrentPageNumber() <= getTotalPages())
                        {
                            totalsFinalized = true;
                        }
                    }

                    notifyAll();
                }
            }

            Transaction.commit(conn);

            if (log.isDebugEnabled())
            {
                log.debug("run(): While loop terminated because either:");
                log.debug("run(): 1. qds.allRecordsRetrieved(): {}", allRecordsRetrieved);
                log.debug("run(): 2. killThread: {}", killThread);
                log.debug("run(): 3. !(currentlyFilledTo + size <= blockEnd): !{}",
                        currentlyFilledTo + pageSize <= blockEnd);
                log.debug("run(): - currentlyFilledTo: {}", currentlyFilledTo);
                log.debug("run(): - size: {}", pageSize);
                log.debug("run(): - blockEnd: {}", blockEnd);
                log.debug("run(): - results.size(): {}", results.size());
            }
        }
        catch (TorqueException e)
        {
            log.error(e);
        }
        finally
        {
            threadRunning = false;

            // Make sure getResults() finally returns if we die.
            queryCompleted = true;

            log.debug("Exiting query thread");
        }
    }

    /**
     * Starts a new thread to retrieve the result set.
     *
     * @param initialSize the initial size for each block.
     */
    private synchronized void startQuery(final int initialSize)
    {
        log.debug("Starting query thread");
        if (!threadRunning)
        {
            pageSize = initialSize;
            currentlyFilledTo = -1;
            queryCompleted = false;
            thread = new Thread(this);
            thread.setName("LargeSelect query Thread");
            thread.start();
            threadRunning = true;
            log.debug("query thread started");
        }
    }

    /**
     * Used to stop filling the memory with the current block of results, if it
     * has been determined that they are no longer relevant.
     *
     * @throws TorqueException if a sleep is interrupted.
     */
    private synchronized void stopQuery() throws TorqueException
    {
        log.debug("stopQuery(): Stopping query thread");
        if (threadRunning)
        {
            killThread = true;

            try
            {
                thread.join(QUERY_STOP_SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                throw new TorqueException("Unexpected interruption", e);
            }

            killThread = false;
            log.debug("stopQuery(): query thread stopped.");
        }
    }

    /**
     * Retrieve the number of the current page.
     *
     * @return the current page number.
     */
    public int getCurrentPageNumber()
    {
        return position / pageSize;
    }

    /**
     * Retrieve the total number of search result records that are known to
     * exist (this will be the actual value when the query has completed (see
     * <code>getTotalsFinalized()</code>).  The convenience method
     * <code>getRecordProgressText()</code> may be more useful for presenting to
     * users.
     *
     * @return the number of result records known to exist (not accurate until
     * <code>getTotalsFinalized()</code> returns <code>true</code>).
     */
    public int getTotalRecords()
    {
        return totalRecords;
    }

    /**
     * Provide an indication of whether or not paging of results will be
     * required.
     *
     * @return <code>true</code> when multiple pages of results exist.
     */
    public boolean getPaginated()
    {
        // Handle a page memory limit of 1 page.
        if (!getTotalsFinalized())
        {
            return true;
        }
        return blockBegin + currentlyFilledTo + 1 > pageSize;
    }

    /**
     * Retrieve the total number of pages of search results that are known to
     * exist (this will be the actual value when the query has completeted (see
     * <code>getQyeryCompleted()</code>).  The convenience method
     * <code>getPageProgressText()</code> may be more useful for presenting to
     * users.
     *
     * @return the number of pages of results known to exist (not accurate until
     * <code>getTotalsFinalized()</code> returns <code>true</code>).
     */
    public int getTotalPages()
    {
        if (totalPages > -1)
        {
            return totalPages;
        }

        int tempPageCount =  getTotalRecords() / pageSize
                + (getTotalRecords() % pageSize > 0 ? 1 : 0);

        if (getTotalsFinalized())
        {
            totalPages = tempPageCount;
        }

        return tempPageCount;
    }

    /**
     * Retrieve the page size.
     *
     * @return the number of records returned on each invocation of
     * <code>getNextResults()</code>/<code>getPreviousResults()</code>.
     */
    public int getPageSize()
    {
        return pageSize;
    }

    /**
     * Provide access to indicator that the total values for the number of
     * records and pages are now accurate as opposed to known upper limits.
     *
     * @return <code>true</code> when the totals are known to have been fully
     * computed.
     */
    public boolean getTotalsFinalized()
    {
        return totalsFinalized;
    }

    /**
     * Retrieve the MessageFormat pattern for the page progress
     * The default is <pre>{0} of {1,choice,0#&gt; |1#}{2}</pre>
     * 
     * @return the pattern as a string
     */
    public String getPageProgressTextPattern()
    {
        return pageProgressTextPattern;
    }

    /**
     * Set the MessageFormat pattern for the page progress.
     * The default is <pre>{0} of {1,choice,0#&gt; |1#}{2}</pre>
     * <p>
     * The pattern contains three placeholders
     * <ul>
     * <li>{0} - the current page</li>
     * <li>{1} - 0 if the total number of pages is not yet known, 1 otherwise</li>
     * <li>{2} - the total number of pages</li>
     * </ul>
     * <p>
     * Localized example in German:<br>
     * <pre>Seite {0} von {1,choice,0#mehr als |1#}{2}</pre>
     * 
     * @param pageProgressTextPattern
     */
    public void setPageProgressTextPattern(String pageProgressTextPattern)
    {
        this.pageProgressTextPattern = pageProgressTextPattern;
    }

    /**
     * Retrieve the MessageFormat pattern for the record progress
     * The default is <pre>{0} - {1} of {2,choice,0#&gt; |1#}{3}</pre>
     * 
     * @return the pattern as a string
     */
    public String getRecordProgressTextPattern()
    {
        return recordProgressTextPattern;
    }
    
    /**
     * Set the MessageFormat pattern for the record progress.
     * The default is <pre>{0} - {1} of {2,choice,0#&gt; |1#}{3}</pre>
     * <p>
     * The pattern contains four placeholders
     * <ul>
     * <li>{0} - number of the first record on the page</li>
     * <li>{1} - number of the last record on the page</li>
     * <li>{2} - 0 if the total number of records is not yet known, 1 otherwise</li>
     * <li>{3} - the total number of records</li>
     * </ul>
     * <p>
     * Localized example in German:<br>
     * <pre>Datensätze {0} bis {1} von {2,choice,0#mehr als |1#}{3}</pre>
     * 
     * @param recordProgressTextPattern
     */
    public void setRecordProgressTextPattern(String recordProgressTextPattern)
    {
        this.recordProgressTextPattern = recordProgressTextPattern;
    }

    /**
     * Sets the multiplier that will be used to compute the memory limit when a
     * constructor with no memory page limit is used - the memory limit will be
     * this number multiplied by the page size.
     *
     * @param memoryPageLimit the maximum number of pages to be in memory
     * at one time.
     */
    public void setMemoryPageLimit(final int memoryPageLimit)
    {
        this.memoryPageLimit = memoryPageLimit;
    }

    /**
     * Retrieves the multiplier that will be used to compute the memory limit
     * when a constructor with no memory page limit is used - the memory limit
     * will be this number multiplied by the page size.
     * 
     * @return memoryPageLimit the maximum number of pages to be in memory
     * at one time.
     */
    public int getMemoryPageLimit()
    {
        return this.memoryPageLimit;
    }

    /**
     * A convenience method that provides text showing progress through the
     * selected rows on a page basis.
     *
     * @return progress text in the form of "1 of &gt; 5" where "&gt;" can be
     * configured using <code>setMoreIndicator()</code>.
     */
    public String getPageProgressText()
    {
        return MessageFormat.format(getPageProgressTextPattern(), 
                getCurrentPageNumber(), 
                totalsFinalized ? 1 : 0, 
                getTotalPages());
    }

    /**
     * Provides a count of the number of rows to be displayed on the current
     * page - for the last page this may be less than the configured page size.
     *
     * @return the number of records that are included on the current page of
     * results.
     * 
     * @throws TorqueException if invoking the <code>populateObjects()</code>
     * method runs into problems or a sleep is unexpectedly interrupted.
     */
    public int getCurrentPageSize() throws TorqueException
    {
        if (null == getCurrentPageResults())
        {
            return 0;
        }
        return getCurrentPageResults().size();
    }

    /**
     * Provide the record number of the first row included on the current page.
     *
     * @return The record number of the first row of the current page.
     */
    public int getFirstRecordNoForPage()
    {
        if (getCurrentPageNumber() < 1)
        {
            return 0;
        }
        return (getCurrentPageNumber() - 1) * getPageSize() + 1;
    }

    /**
     * Provide the record number of the last row included on the current page.
     *
     * @return the record number of the last row of the current page.
     * 
     * @throws TorqueException if invoking the <code>populateObjects()</code>
     * method runs into problems or a sleep is unexpectedly interrupted.
     */
    public int getLastRecordNoForPage() throws TorqueException
    {
        if (0 == getCurrentPageNumber())
        {
            return 0;
        }
        return (getCurrentPageNumber() - 1) * getPageSize()
                + getCurrentPageSize();
    }

    /**
     * A convenience method that provides text showing progress through the
     * selected rows on a record basis.
     *
     * @return progress text in the form of "26 - 50 of &gt; 250" where "&gt;"
     * can be configured using <code>setMoreIndicator()</code>.
     * 
     * @throws TorqueException if invoking the <code>populateObjects()</code>
     * method runs into problems or a sleep is unexpectedly interrupted.
     */
    public String getRecordProgressText() throws TorqueException
    {
        return MessageFormat.format(getRecordProgressTextPattern(), 
                getFirstRecordNoForPage(),
                getLastRecordNoForPage(),
                totalsFinalized ? 1 : 0, 
                getTotalRecords());
    }

    /**
     * Indicates if further result pages are available.
     *
     * @return <code>true</code> when further results are available.
     */
    public boolean getNextResultsAvailable()
    {
        if (!totalsFinalized || getCurrentPageNumber() < getTotalPages())
        {
            return true;
        }
        return false;
    }

    /**
     * Indicates if previous results pages are available.
     *
     * @return <code>true</code> when previous results are available.
     */
    public boolean getPreviousResultsAvailable()
    {
        if (getCurrentPageNumber() <= 1)
        {
            return false;
        }
        return true;
    }

    /**
     * Indicates if any results are available.
     *
     * @return <code>true</code> of any results are available.
     */
    public boolean hasResultsAvailable()
    {
        return getTotalRecords() > 0;
    }

    /**
     * Clear the query result so that the query is re-executed when the next page
     * is retrieved.  You may want to invoke this method if you are returning to
     * a page after performing an operation on an item in the result set.
     *
     * @throws TorqueException if a sleep is interrupted.
     */
    public synchronized void invalidateResult() throws TorqueException
    {
        stopQuery();
        blockBegin = 0;
        blockEnd = 0;
        currentlyFilledTo = -1;
        results = null;
        // TODO Perhaps store the oldPosition and immediately restart the
        // query.
        // oldPosition = position;
        position = 0;
        totalPages = -1;
        totalRecords = 0;
        queryCompleted = false;
        totalsFinalized = false;
        lastResults = null;
    }

    /**
     * Retrieve a search parameter.  This acts as a convenient place to store
     * parameters that relate to the LargeSelect to make it easy to get at them
     * in order to re-populate search parameters on a form when the next page of
     * results is retrieved - they in no way effect the operation of
     * LargeSelect.
     *
     * @param name the search parameter key to retrieve.
     * @return the value of the search parameter.
     */
    public String getSearchParam(final String name)
    {
        return getSearchParam(name, null);
    }

    /**
     * Retrieve a search parameter.  This acts as a convenient place to store
     * parameters that relate to the LargeSelect to make it easy to get at them
     * in order to re-populate search parameters on a form when the next page of
     * results is retrieved - they in no way effect the operation of
     * LargeSelect.
     *
     * @param name the search parameter key to retrieve.
     * @param defaultValue the default value to return if the key is not found.
     * @return the value of the search parameter.
     */
    public String getSearchParam(final String name, final String defaultValue)
    {
        if (null == params)
        {
            return defaultValue;
        }
        String value = params.get(name);
        return null == value ? defaultValue : value;
    }

    /**
     * Set a search parameter.  If the value is <code>null</code> then the
     * key will be removed from the parameters.
     *
     * @param name the search parameter key to set.
     * @param value the value of the search parameter to store.
     */
    public void setSearchParam(final String name, final String value)
    {
        if (null == value)
        {
            removeSearchParam(name);
        }
        else
        {
            if (null != name)
            {
                if (null == params)
                {
                    params = new Hashtable<>();
                }
                params.put(name, value);
            }
        }
    }

    /**
     * Remove a value from the search parameters.
     *
     * @param name the search parameter key to remove.
     */
    public void removeSearchParam(final String name)
    {
        if (null != params)
        {
            params.remove(name);
        }
    }

    /**
     * De-serialize this LargeSelect instance.
     *
     * @param inputStream The serialization input stream.
     * @throws IOException if input stream cannot be read
     * @throws ClassNotFoundException if class not found
     */
    private void readObject(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException
    {
        inputStream.defaultReadObject();

        // avoid NPE because of Tomcat de-serialization of sessions
        if (Torque.isInit())
        {
            startQuery(pageSize);
        }
    }

    /**
     * Provide something useful for debugging purposes.
     *
     * @return some basic information about this instance of LargeSelect.
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("LargeSelect - TotalRecords: ");
        result.append(getTotalRecords());
        result.append(" TotalsFinalised: ");
        result.append(getTotalsFinalized());
        result.append("\nParameters:");
        if (null == params || params.size() == 0)
        {
            result.append(" No parameters have been set.");
        }
        else
        {
            params.forEach((key, val) -> result.append("\n ").append(key).append(": ").append(val));
        }
        return result.toString();
    }

}
