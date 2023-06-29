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

import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.peer.AuthorPeer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Test code for LargeSelect.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id: LargeSelectTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class LargeSelectTest extends BaseDatabaseTestCase
{
    private static final int TEST_PAGE_SIZE = 9;
    private static final int TEST_PAGES = 9;
    private static final int TEST_ROWS = TEST_PAGE_SIZE * TEST_PAGES;
    private static final String LARGE_SELECT_AUTHOR = "LargeSelectAuthor";
    private int firstAuthorId = -1;

    private Criteria criteria;

    @BeforeEach
    public void setUp() throws Exception
    {
        // Clean up any previous failures
        tearDown();

        // Create some test data
        for (int i = 0; i < TEST_ROWS; i++)
        {
            Author author = new Author();
            author.setName(LARGE_SELECT_AUTHOR);
            author.save();
            if (-1 == firstAuthorId)
            {
                firstAuthorId = author.getAuthorId();
            }
        }
        // Set up the standard criteria for the test.
        criteria = new Criteria();
        criteria.where(AuthorPeer.NAME, LARGE_SELECT_AUTHOR);
    }


    @AfterEach
    public void tearDown() throws Exception
    {
        // Delete the test data
        org.apache.torque.criteria.Criteria criteria
            = new org.apache.torque.criteria.Criteria();
        criteria.where(AuthorPeer.NAME, LARGE_SELECT_AUTHOR);
        AuthorPeer.doDelete(criteria);
        criteria = null;
    }

    /**
     * Test the criteria provides the correct number of rows.
     */
    public void testCriteria() throws TorqueException
    {
        criteria.addSelectColumn(AuthorPeer.AUTHOR_ID);
        List<Integer> result = AuthorPeer.doSelect(
                criteria,
                new IntegerMapper());
        assertEquals("Selected rows", TEST_ROWS, result.size());
    }

    /**
     * Test an invalid criteria - includes a limit.
     */
    public void testBadCriteria11()
    {
        criteria.setLimit(1);
        try
        {
            new LargeSelect<>(criteria, TEST_PAGE_SIZE,
                    AuthorPeer.getAuthorPeerImpl());
        }
        catch (IllegalArgumentException success)
        {
            // Do nothing
        }
    }

    /**
     * Test an invalid criteria - includes an offset.
     */
    public void testBadCriteria12()
    {
        criteria.setOffset(1);
        try
        {
            new LargeSelect<>(criteria, TEST_PAGE_SIZE,
                    AuthorPeer.getAuthorPeerImpl());
        }
        catch (IllegalArgumentException success)
        {
            // Do nothing
        }
    }

    /**
     * Test an invalid page size.
     */
    public void testBadPageSize()
    {
        try
        {
            new LargeSelect<>(criteria, 0,
                    AuthorPeer.getAuthorPeerImpl());
        }
        catch (IllegalArgumentException success)
        {
            // Do nothing
        }
    }

    /**
     * Test an invalid memory limit.
     */
    public void testBadMemoryLimit()
    {
        try
        {
            new LargeSelect<>(criteria, TEST_PAGE_SIZE, 0,
                    AuthorPeer.getAuthorPeerImpl());
        }
        catch (IllegalArgumentException success)
        {
            // Do nothing
        }
    }

    /**
     * Test a couple of setter/getter methods.
     */
    public void testSetterMethods()
    {
        LargeSelect<Author> ls = new LargeSelect<>(
                criteria,
                TEST_PAGE_SIZE,
                AuthorPeer.getAuthorPeerImpl());

        assertEquals("Memory page limit", 5, ls.getMemoryPageLimit());
        ls.setMemoryPageLimit(10);
        assertEquals("Memory page limit", 10, ls.getMemoryPageLimit());
        ls.setMemoryPageLimit(LargeSelect.DEFAULT_MEMORY_LIMIT_PAGES);
        assertEquals("Memory page limit", 5, ls.getMemoryPageLimit());

        assertEquals("Page progress text pattern", "{0} of {1,choice,0#&gt; |1#}{2}", ls.getPageProgressTextPattern());
        String newPageProgressTextPattern = "Seite {0} von {1,choice,0#mehr als |1#}{2}";
        ls.setPageProgressTextPattern(newPageProgressTextPattern);
        assertEquals("Page progress text pattern", newPageProgressTextPattern, ls.getPageProgressTextPattern());
        ls.setPageProgressTextPattern(LargeSelect.DEFAULT_PAGE_PROGRESS_TEXT_PATTERN);
        assertEquals("Page progress text pattern", "{0} of {1,choice,0#&gt; |1#}{2}", ls.getPageProgressTextPattern());

        assertEquals("Record progress text pattern", "{0} - {1} of {2,choice,0#&gt; |1#}{3}", ls.getRecordProgressTextPattern());
        String newRecordProgressTextPattern = "Datensätze {0} bis {1} von {2,choice,0#mehr als |1#}{3}";
        ls.setRecordProgressTextPattern(newRecordProgressTextPattern);
        assertEquals("Record progress text pattern", newRecordProgressTextPattern, ls.getRecordProgressTextPattern());
        ls.setRecordProgressTextPattern(LargeSelect.DEFAULT_RECORD_PROGRESS_TEXT_PATTERN);
        assertEquals("Record progress text pattern", "{0} - {1} of {2,choice,0#&gt; |1#}{3}", ls.getRecordProgressTextPattern());
    }

    /**
     * Test a bunch of different methods when everything is set up correctly.
     */
    public void testLargeSelect() throws TorqueException
    {
        LargeSelect<Author> ls = new LargeSelect<>(
                criteria,
                TEST_PAGE_SIZE,
                AuthorPeer.getAuthorPeerImpl());

        assertEquals("Page size", TEST_PAGE_SIZE, ls.getPageSize());
        assertTrue("Paginated", ls.getPaginated());

        long startTime = System.currentTimeMillis();
        List<Author> results = ls.getNextResults();
        long timeElapsed = System.currentTimeMillis() - startTime;
        System.out.println("Elapsed time for first page " + timeElapsed);

        // Page 1
        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
        assertEquals("Current page number", 1, ls.getCurrentPageNumber());
        assertFalse("Previous results available", ls.getPreviousResultsAvailable());
        assertTrue("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
        assertEquals("First record for page", 1, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 9, ls.getLastRecordNoForPage());
        assertFalse("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", 5, ls.getTotalPages());
        assertEquals("Total records", 45, ls.getTotalRecords());
        assertEquals("Page progress text", "1 of &gt; 5", ls.getPageProgressText());
        assertEquals("Record progress text", "1 - 9 of &gt; 45", ls.getRecordProgressText());

        results = ls.getPage(5);
        // Page 5
        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
        assertEquals("Current page number", 5, ls.getCurrentPageNumber());
        assertTrue("Previous results available", ls.getPreviousResultsAvailable());
        assertTrue("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
        assertEquals("First record for page", 37, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 45, ls.getLastRecordNoForPage());
        assertFalse("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", 5, ls.getTotalPages());
        assertEquals("Total records", 45, ls.getTotalRecords());
        assertEquals("Page progress text", "5 of &gt; 5", ls.getPageProgressText());
        assertEquals("Record progress text", "37 - 45 of &gt; 45", ls.getRecordProgressText());

        results = ls.getNextResults();
        // Page 6
        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
        assertEquals("Current page number", 6, ls.getCurrentPageNumber());
        assertTrue("Previous results available", ls.getPreviousResultsAvailable());
        assertTrue("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
        assertEquals("First record for page", 46, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 54, ls.getLastRecordNoForPage());
        assertTrue("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", TEST_PAGES, ls.getTotalPages());
        assertEquals("Total records", TEST_ROWS, ls.getTotalRecords());
        assertEquals("Page progress text", "6 of 9", ls.getPageProgressText());
        assertEquals("Record progress text", "46 - 54 of 81", ls.getRecordProgressText());

        results = ls.getNextResults();
        // Page 7
        results = ls.getNextResults();
        // Page 8
        results = ls.getNextResults();
        // Page 9
        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
        assertEquals("Current page number", 9, ls.getCurrentPageNumber());
        assertTrue("Previous results available", ls.getPreviousResultsAvailable());
        assertFalse("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
        assertEquals("First record for page", 73, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 81, ls.getLastRecordNoForPage());
        assertTrue("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", TEST_PAGES, ls.getTotalPages());
        assertEquals("Total records", TEST_ROWS, ls.getTotalRecords());
        assertEquals("Page progress text", "9 of 9", ls.getPageProgressText());
        assertEquals("Record progress text", "73 - 81 of 81", ls.getRecordProgressText());

        results = ls.getPage(2);
        // Page 2
        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
        assertEquals("Current page number", 2, ls.getCurrentPageNumber());
        assertTrue("Previous results available", ls.getPreviousResultsAvailable());
        assertTrue("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
        assertEquals("First record for page", 10, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 18, ls.getLastRecordNoForPage());
        assertTrue("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", 9, ls.getTotalPages());
        assertEquals("Total records", 81, ls.getTotalRecords());
        assertEquals("Page progress text", "2 of 9", ls.getPageProgressText());
        assertEquals("Record progress text", "10 - 18 of 81", ls.getRecordProgressText());

        List<Author> sameResults = ls.getCurrentPageResults();
        // Page 2
        assertSame("Same results", results, sameResults);

        results = ls.getPreviousResults();
        // Page 1
        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
        assertEquals("Current page number", 1, ls.getCurrentPageNumber());
        assertFalse("Previous results available", ls.getPreviousResultsAvailable());
        assertTrue("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
        assertEquals("First record for page", 1, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 9, ls.getLastRecordNoForPage());
        assertTrue("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", 9, ls.getTotalPages());
        assertEquals("Total records", 81, ls.getTotalRecords());
        assertEquals("Page progress text", "1 of 9", ls.getPageProgressText());
        assertEquals("Record progress text", "1 - 9 of 81", ls.getRecordProgressText());
    }

    /**
     * Test what happens when only one row is returned.
     */
    public void testLargeSelectOneRow() throws Exception
    {
        // Alter criteria to retrieve only one row
        criteria.where(AuthorPeer.AUTHOR_ID, firstAuthorId);
        LargeSelect<Author> ls = new LargeSelect<>(
                criteria,
                TEST_PAGE_SIZE,
                AuthorPeer.getAuthorPeerImpl());

        // Page 1
        List<Author> results = ls.getNextResults();
        assertTrue("Totals finalised", ls.getTotalsFinalized());
        assertFalse("Paginated", ls.getPaginated());
        assertEquals("results.size()", 1, results.size());
        assertEquals("Current page number", 1, ls.getCurrentPageNumber());
        assertFalse("Previous results available", ls.getPreviousResultsAvailable());
        assertFalse("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", 1, ls.getCurrentPageSize());
        assertEquals("First record for page", 1, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 1, ls.getLastRecordNoForPage());
        assertEquals("Total pages", 1, ls.getTotalPages());
        assertEquals("Total records", 1, ls.getTotalRecords());
        assertEquals("Page progress text", "1 of 1", ls.getPageProgressText());
        assertEquals("Record progress text", "1 - 1 of 1", ls.getRecordProgressText());
        assertTrue("Results available", ls.hasResultsAvailable());
    }

    /**
     * Test invalidateResult()
     */
    public void testInvalidateResult() throws Exception
    {
        LargeSelect<Author> ls = new LargeSelect<>(
                criteria,
                TEST_PAGE_SIZE,
                AuthorPeer.getAuthorPeerImpl());

        assertEquals("Page size", TEST_PAGE_SIZE, ls.getPageSize());
        assertTrue("Paginated", ls.getPaginated());

        List<Author> results = ls.getNextResults();
        // Page 1
        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
        assertEquals("Current page number", 1, ls.getCurrentPageNumber());
        assertFalse("Previous results available", ls.getPreviousResultsAvailable());
        assertTrue("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
        assertEquals("First record for page", 1, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 9, ls.getLastRecordNoForPage());
        assertFalse("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", 5, ls.getTotalPages());
        assertEquals("Total records", 45, ls.getTotalRecords());
        assertEquals("Page progress text", "1 of &gt; 5", ls.getPageProgressText());
        assertEquals("Record progress text", "1 - 9 of &gt; 45", ls.getRecordProgressText());

        ls.invalidateResult();

        assertEquals("Page size", TEST_PAGE_SIZE, ls.getPageSize());
        assertTrue("Paginated", ls.getPaginated());

        // Page 0
        assertEquals("Current page number", 0, ls.getCurrentPageNumber());
        assertFalse("Previous results available", ls.getPreviousResultsAvailable());
        assertTrue("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", 0, ls.getCurrentPageSize());
        assertEquals("First record for page", 0, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 0, ls.getLastRecordNoForPage());
        assertFalse("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", 0, ls.getTotalPages());
        assertEquals("Total records", 0, ls.getTotalRecords());
        assertEquals("Page progress text", "0 of &gt; 0", ls.getPageProgressText());
        assertEquals("Record progress text", "0 - 0 of &gt; 0", ls.getRecordProgressText());

        results = ls.getNextResults();
        // Page 1
        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
        assertEquals("Current page number", 1, ls.getCurrentPageNumber());
        assertFalse("Previous results available", ls.getPreviousResultsAvailable());
        assertTrue("Next results available", ls.getNextResultsAvailable());
        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
        assertEquals("First record for page", 1, ls.getFirstRecordNoForPage());
        assertEquals("Last record for page", 9, ls.getLastRecordNoForPage());
        assertFalse("Totals finalised", ls.getTotalsFinalized());
        assertEquals("Total pages", 5, ls.getTotalPages());
        assertEquals("Total records", 45, ls.getTotalRecords());
        assertEquals("Page progress text", "1 of &gt; 5", ls.getPageProgressText());
        assertEquals("Record progress text", "1 - 9 of &gt; 45", ls.getRecordProgressText());
    }

    //    /**
    //     * A basic serialization test.  Cannot continue implementation until
    //     * serialization of Criteria has been implemented correctly.
    //     *
    //     * @throws TorqueException
    //     */
    //    public void testSerialization() throws TorqueException
    //    {
    //        // TODO  Serialization needs to be reviewed for LargeSelect
    //        // userDataSet should be marked transient and all access should be wrapped for detect and handle null _or_ readObject should rerun the query.
    //
    //        System.out.println("criteria (before LS created) = " + criteria);
    //        LargeSelect ls = new LargeSelect(criteria, TEST_PAGE_SIZE,
    //                "org.apache.torque.test.AuthorPeer");
    //
    //        assertEquals("Page size", TEST_PAGE_SIZE, ls.getPageSize());
    //        assertTrue("Paginated", ls.getPaginated());
    //
    //        List results = ls.getNextResults();
    //        // Page 1
    //        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
    //        assertEquals("Current page number", 1, ls.getCurrentPageNumber());
    //        assertFalse("Previous results available", ls.getPreviousResultsAvailable());
    //        assertTrue("Next results available", ls.getNextResultsAvailable());
    //        assertEquals("Current page size", TEST_PAGE_SIZE, ls.getCurrentPageSize());
    //        assertEquals("First record for page", 1, ls.getFirstRecordNoForPage());
    //        assertEquals("Last record for page", 9, ls.getLastRecordNoForPage());
    //        assertFalse("Totals finalised", ls.getTotalsFinalized());
    //        assertEquals("Total pages", 5, ls.getTotalPages());
    //        assertEquals("Total records", 45, ls.getTotalRecords());
    //        assertEquals("Page progress text", "1 of &gt; 5", ls.getPageProgressText());
    //        assertEquals("Record progress text", "1 - 9 of &gt; 45", ls.getRecordProgressText());
    //
    //        LargeSelect lsCopy = (LargeSelect) SerializationUtils.clone(ls);
    //
    //        assertEquals("Page size", TEST_PAGE_SIZE, lsCopy.getPageSize());
    //        assertTrue("Paginated", lsCopy.getPaginated());
    //
    //        // Page 1
    //        results = lsCopy.getCurrentPageResults();
    //        assertEquals("results.size()", TEST_PAGE_SIZE, results.size());
    //        assertEquals("Current page number", 1, lsCopy.getCurrentPageNumber());
    //        assertFalse("Previous results available", lsCopy.getPreviousResultsAvailable());
    //        assertTrue("Next results available", lsCopy.getNextResultsAvailable());
    //        assertEquals("Current page size", TEST_PAGE_SIZE, lsCopy.getCurrentPageSize());
    //        assertEquals("First record for page", 1, lsCopy.getFirstRecordNoForPage());
    //        assertEquals("Last record for page", 9, lsCopy.getLastRecordNoForPage());
    //        assertFalse("Totals finalised", lsCopy.getTotalsFinalized());
    //        assertEquals("Total pages", 5, lsCopy.getTotalPages());
    //        assertEquals("Total records", 45, lsCopy.getTotalRecords());
    //        assertEquals("Page progress text", "1 of &gt; 5", lsCopy.getPageProgressText());
    //        assertEquals("Record progress text", "1 - 9 of &gt; 45", lsCopy.getRecordProgressText());
    //    }

    // todo Add a test for getPaginated() - was previously returning false when 6 results and pageSize 5

    // todo Add test for parameter storage
}
