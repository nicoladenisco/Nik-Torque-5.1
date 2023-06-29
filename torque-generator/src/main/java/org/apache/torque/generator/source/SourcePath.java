package org.apache.torque.generator.source;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.torque.generator.GeneratorException;

/**
 * Methods for traversing a source tree.
 */
public final class SourcePath
{
    /**
     * The separator between different levels in the path.
     */
    private static final String PATH_LEVEL_SEPARATOR = "/";

    /**
     * The token denoting the current element.
     */
    private static final String THIS_TOKEN = ".";

    /**
     * The token denoting the parent element.
     */
    private static final String PARENT_TOKEN = "..";

    /**
     * The token denoting the parent element.
     */
    private static final String ANY_ELEMENT_TOKEN = "*";

    static
    {
        JXPathContextReferenceImpl.addNodePointerFactory(
                new SourceElementNodePointerFactory());
        JXPathContextReferenceImpl.addNodePointerFactory(
                new ModelNodeFactory());
    }

    /**
     * Private constructor for utility class.
     */
    private SourcePath()
    {
    }

    /**
     * Returns whether children with the given name exist.
     *
     * @param sourceElement the start element, not null.
     * @param name the name of the child element, not null.
     *
     * @return true if children with the given name exist, false otherwise.
     *
     * @throws NullPointerException if name is null.
     */
    public static boolean hasChild(final SourceElement sourceElement, final String name)
    {
        if (name == null)
        {
            throw new NullPointerException("name must not be null");
        }
        if (sourceElement == null)
        {
            throw new NullPointerException("sourceElement must not be null");
        }
        for (final SourceElement child : sourceElement.getChildren())
        {
            if (name.equals(child.getName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether a following element exists as a child of the parent of
     * this element.
     *
     * @param sourceElement the start element, not null.
     *
     * @return true if a following element exists, false if not.
     */
    public static boolean hasFollowing(final SourceElement sourceElement)
    {
        return !getFollowing(sourceElement, null).isEmpty();
    }

    /**
     * Returns whether an preceding exists as a child of the parent of
     * this element.
     *
     * @param sourceElement the start element, not null.
     *
     * @return true if a preceding element exists, false if not.
     */
    public static boolean hasPreceding(final SourceElement sourceElement)
    {
        return !getPreceding(sourceElement, sourceElement.getName()).isEmpty();
    }

    /**
     * Returns whether a following element exists as a child of the parent of
     * this element, which has the same name as this source element.
     *
     * @param sourceElement the start element, not null.
     *
     * @return true if a following sibling exists, false if not.
     */
    public static boolean hasFollowingSibling(final SourceElement sourceElement)
    {
        return !getFollowing(sourceElement, sourceElement.getName()).isEmpty();
    }

    /**
     * Returns whether an preceding exists as a child of the parent of
     * this element, which has the same name as this source element.
     *
     * @param sourceElement the start element, not null.
     *
     * @return true if a preceding sibling exists, false if not.
     */
    public static boolean hasPrecedingSibling(final SourceElement sourceElement)
    {
        return !getPreceding(sourceElement, sourceElement.getName()).isEmpty();
    }

    /**
     * Returns all the preceding elements in the child list
     * of the default parent which appear before this element
     * with the given name.
     * If name is null, all preceding elements are returned.
     * If this element has no parent, an empty list is returned.
     *
     * @param sourceElement the start element, not null.
     * @param name the name of the preceding elements to select,
     *        or null to select all preceding elements.
     *
     * @return a list containing the preceding elements with the given name,
     *         never null.
     *
     * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
     */
    public static List<SourceElement> getPreceding(
            final SourceElement sourceElement,
            final String name)
    {
        if (sourceElement == null)
        {
            throw new NullPointerException("sourceElement must not be null");
        }

        final List<SourceElement> result = new ArrayList<>();
        final ListIterator<SourceElement> sameLevelIt
        = getSiblingIteratorPositionedOnSelf(
                sourceElement,
                sourceElement.getParent());
        if (sameLevelIt == null)
        {
            return result;
        }
        boolean first = true;
        while (sameLevelIt.hasPrevious())
        {
            final SourceElement sameLevelElement = sameLevelIt.previous();
            // skip first iterated element because it is input element,
            // but we want to begin before the input element.
            if (first)
            {
                first = false;
                continue;
            }
            if (name == null || name.equals(sameLevelElement.getName()))
            {
                result.add(sameLevelElement);
            }
        }
        return result;

    }

    /**
     * Returns all the following elements in the child list
     * of the default parent which appear after this element
     * with the given name.
     * If name is null, all following elements are returned.
     * If this element has no parent, an empty list is returned.
     *
     * @param sourceElement the start element, not null.
     * @param name the name of the following elements to select,
     *        or null to select all following elements.
     *
     * @return a list containing the following elements with the given name,
     *         never null.
     *
     * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
     */
    public static List<SourceElement> getFollowing(
            final SourceElement sourceElement,
            final String name)
    {
        if (sourceElement == null)
        {
            throw new NullPointerException("sourceElement must not be null");
        }
        final List<SourceElement> result = new ArrayList<>();

        final ListIterator<SourceElement> sameLevelIt
        = getSiblingIteratorPositionedOnSelf(
                sourceElement,
                sourceElement.getParent());
        if (sameLevelIt == null)
        {
            return result;
        }
        while (sameLevelIt.hasNext())
        {
            final SourceElement sameLevelElement = sameLevelIt.next();
            if (name == null || name.equals(sameLevelElement.getName()))
            {
                result.add(sameLevelElement);
            }
        }
        return result;
    }

    /**
     * Returns a ListIterator of the siblings of the input source element.
     * The iterator is positioned such that the next method returns
     * the element after the input element, and the previous method returns
     * the input element.
     *
     * @param sourceElement the source element for which the sibling iterator
     *        should be created, not null.
     * @param parent the parent for the source element.
     *
     * @return the sibling iterator, or null if the input source element has
     *         no parent.
     *
     * @throws IllegalArgumentException if the element cannot be found in the
     *         list of children of its parent.
     */
    protected static ListIterator<SourceElement> getSiblingIteratorPositionedOnSelf(
            final SourceElement sourceElement,
            final SourceElement parent)
    {
        if (parent == null)
        {
            return null;
        }
        final ListIterator<SourceElement> sameLevelIt
        = parent.getChildren().listIterator();

        boolean found = false;
        while (sameLevelIt.hasNext())
        {
            final SourceElement sameLevelElement = sameLevelIt.next();
            if (sameLevelElement == sourceElement)
            {
                found = true;
                break;
            }
        }
        if (!found)
        {
            throw new IllegalArgumentException("Inconsistent source tree: "
                    + "Source element " + sourceElement.getName()
                    + " not found in the list of the children of its parent");
        }
        return sameLevelIt;
    }

    /**
     * Gets the elements which can be reached from the start element by a given
     * path.
     * If the model root is null, only objects which are children of base
     * can be retrieved.
     *
     * @param root the model root, or null if no model root exists
     *        (in the latter case, no elements only reachable via base's parent
     *        can be accessed)
     * @param pathToBase the path from root to base, must be not null
     *        if root is not null, is disregarded if root is null.
     * @param base the base object, not null.
     * @param path the path to use, or null (which refers base).
     *
     * @return the list of matching source elements, not null, may be empty.
     *
     * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
     */
    public static Iterator<SourcePathPointer> iteratePointer(
            final Object root,
            String pathToBase,
            final Object base,
            String path)
    {
        if (base == null)
        {
            throw new NullPointerException("base must not be null");
        }
        if (path == null)
        {
            path = ".";
        }

        final JXPathContext context;
        if (root != null)
        {
            final JXPathContext rootContext = JXPathContext.newContext(root);
            // In source element, the root node precedes the path.
            // For a correct relative path, the root node
            // of the relative path must be removed.
            if (root instanceof SourceElement && pathToBase.startsWith("/"))
            {
                int slashIndex = pathToBase.indexOf('/', 1);
                if (slashIndex != -1)
                {
                    pathToBase = pathToBase.substring(slashIndex + 1);
                }
                else
                {
                    pathToBase = "/";
                }
            }

            try
            {
                context = rootContext.getRelativeContext(
                        rootContext.getPointer(pathToBase));
            }
            catch (JXPathException e)
            {
                throw new IllegalArgumentException(
                        "The pathToBase " + pathToBase
                        + " is not found in root " + root,
                        e);
            }

            Object pathValue = context.getValue(".");
            if (pathValue != base)
            {
                throw new IllegalArgumentException(
                        "The pathToBase " + pathToBase
                        + " does not evaluate to base " + base
                        + " from root " + root);
            }
            // seems to be a bug in realtiveContext . evaluation in JXPath
            if (".".equals(path))
            {
                return new SourcePathPointerIterator(
                        JXPathContext.newContext(root)
                        .iteratePointers(pathToBase));
            }
        }
        else
        {
            context = JXPathContext.newContext(base);
        }
        context.setLenient(true);


        final Iterator<?> jxpathPointerIterator = context.iteratePointers(path);
        return new SourcePathPointerIterator(jxpathPointerIterator);
    }

    /**
     * Gets the elements which can be reached from the start element by a given
     * path.
     *
     * @param sourceElement the start element, not null.
     * @param path the path to use, not null.
     *
     * @return the list of matching source elements, not null, may be empty.
     *
     * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
     */
    public static List<SourceElement> getElements(
            final SourceElement sourceElement,
            final String path)
    {
        if (sourceElement == null)
        {
            throw new NullPointerException("sourceElement must not be null");
        }

        if (path.equals(THIS_TOKEN))
        {
            final List<SourceElement> result = new ArrayList<>(1);
            result.add(sourceElement);
            return result;
        }
        final StringTokenizer selectionPathTokenizer
            = new StringTokenizer(path, PATH_LEVEL_SEPARATOR);
        List<SourceElement> currentSelection = new ArrayList<>();
        currentSelection.add(sourceElement);
        while (selectionPathTokenizer.hasMoreTokens())
        {
            final String childName = selectionPathTokenizer.nextToken();
            final List<SourceElement> nextSelection = new ArrayList<>();
            for (final SourceElement currentElement : currentSelection)
            {
                if (childName.equals(PARENT_TOKEN))
                {
                    final SourceElement parent = currentElement.getParent();
                    if (parent != null && !nextSelection.contains(parent))
                    {
                        nextSelection.add(parent);
                    }
                }
                else if (ANY_ELEMENT_TOKEN.equals(childName))
                {
                    for (final SourceElement child
                            : currentElement.getChildren())
                    {
                        nextSelection.add(child);
                    }
                }
                {
                    for (final SourceElement child
                            : currentElement.getChildren(childName))
                    {
                        nextSelection.add(child);
                    }
                }
            }
            currentSelection = nextSelection;
        }
        return currentSelection;
    }

    /**
     * Gets the elements which can be reached from the root element by a given
     * path. The name of the root element must appear first in the path,
     * otherwise nothing is selected.
     *
     * @param rootElement the root element of the source tree, not null.
     * @param path the path to use, null selects the root element.
     *
     * @return the list of matching source elements, not null, may be empty.
     *
     * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
     */
    public static List<SourceElement> getElementsFromRoot(
            final SourceElement rootElement,
            String path)
    {
        if (rootElement == null)
        {
            throw new NullPointerException("rootElement must not be null");
        }

        if (path == null
                || "".equals(path.trim())
                || PATH_LEVEL_SEPARATOR.equals(path.trim()))
        {
            // use root element
            final List<SourceElement> result = new ArrayList<>(1);
            result.add(rootElement);
            return result;
        }

        path = path.trim();
        // remove leading slash
        if (path.startsWith(PATH_LEVEL_SEPARATOR))
        {
            path = path.substring(1);
        }
        final int firstSeparatorPos = path.indexOf(PATH_LEVEL_SEPARATOR);
        String firstElementName;
        if (firstSeparatorPos == -1)
        {
            firstElementName = path;
            path = THIS_TOKEN;
        }
        else
        {
            firstElementName = path.substring(0, firstSeparatorPos);
            path = path.substring(firstSeparatorPos + 1);
        }
        if (!ANY_ELEMENT_TOKEN.equals(firstElementName)
                && !rootElement.getName().equals(firstElementName))
        {
            return new ArrayList<>();
        }
        return SourcePath.getElements(rootElement, path);
    }

    /**
     * Gets a single source element which can be reached from the start element
     * by a given path.
     *
     * @param root the model root, or null if no model root exists
     *        (in the latter case, no elements only reachable via base's parent
     *        can be accessed)
     * @param pathToBase the path from root to base, must be not null
     *        if root is not null, is disregarded if root is null.
     * @param sourceElement the start element, not null.
     * @param path the path to use, not null.
     * @param acceptEmpty whether no match is an error(acceptEmpty=false)
     *        or not (acceptEmpty=true)
     *
     * @return the single matching source elements, may be null only if
     *         acceptEmpty=true.
     *
     * @throws GeneratorException if more than one source element matches,
     *       or if no source element matches and acceptEmpty=false
     * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
     */
    public static Object getObject(
            final SourceElement root,
            final String pathToBase,
            final SourceElement sourceElement,
            final String path,
            final boolean acceptEmpty)
                    throws GeneratorException
    {
        final Iterator<SourcePathPointer> sourceElementIt
        = iteratePointer(root, pathToBase, sourceElement, path);
        if (!sourceElementIt.hasNext())
        {
            if (acceptEmpty)
            {
                return null;
            }
            else
            {
                throw new GeneratorException(
                        "Source element path "
                                + path
                                + " selects no element on source element "
                                + sourceElement.getName());
            }
        }
        final SourcePathPointer sourcePathPointer = sourceElementIt.next();
        if (sourceElementIt.hasNext())
        {
            throw new GeneratorException(
                    "Source element path "
                            + path
                            + " selects more than a single element on source element "
                            + sourceElement.getName());
        }
        return sourcePathPointer.getValue();
    }

    /**
     * Gets a single source element which can be reached from the start element
     * by a given path.
     *
     * @param sourceElement the start element, not null.
     * @param path the path to use, not null.
     * @param acceptEmpty whether no match is an error(acceptEmpty=false)
     *        or not (acceptEmpty=true)
     *
     * @return the single matching source elements, may be null only if
     *         acceptEmpty=true.
     *
     * @throws GeneratorException if more than one source element matches,
     *       or if no source element matches and acceptEmpty=false
     * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
     */
    public static SourceElement getElement(
            final SourceElement sourceElement,
            final String path,
            final boolean acceptEmpty)
                    throws GeneratorException
    {
        final List<SourceElement> sourceElements
        = SourcePath.getElements(sourceElement, path);
        if (sourceElements.isEmpty())
        {
            if (acceptEmpty)
            {
                return null;
            }
            else
            {
                throw new GeneratorException(
                        "Source element path "
                                + path
                                + " selects no element on source element "
                                + sourceElement.getName());
            }
        }
        if (sourceElements.size() > 1)
        {
            throw new GeneratorException(
                    "Source element path "
                            + path
                            + " selects more than a single element on source element "
                            + sourceElement.getName());
        }
        return sourceElements.get(0);
    }

    /**
     * Returns the path from the root element to the source element.
     * The element names are separated by slashes.
     * Example: root/firstLevelElement/secondLevelElement/currentNode
     *
     * @param sourceElement the element to output, not null.
     *
     * @return the path from root, not null.
     *
     * @throws GeneratorException if the parent chain contains a closed loop.
     */
    public static String getPathAsString(final SourceElement sourceElement)
            throws GeneratorException
    {
        final StringBuilder result = new StringBuilder();
        getParentPath(sourceElement, new HashSet<SourceElement>(), result);
        result.append(sourceElement.getName());
        return result.toString();
    }

    /**
     * Gets the path to the parent of a source element.
     * @param toProcess the source element for which parent the path should be
     *        calculated.
     * @param alreadyProcessed the elements which are already processed
     *        by this method.
     * @param result the path to the parent, ends with a slash if any parents
     *        are present.
     *
     * @throws GeneratorException if the parent is contained in alreadyProcessed
     *         or if the parent chain contains a closed loop.
     */
    private static void getParentPath(
            final SourceElement toProcess,
            final Set<SourceElement> alreadyProcessed,
            final StringBuilder result)
                    throws GeneratorException
    {
        final SourceElement parent = toProcess.getParent();
        if (alreadyProcessed.contains(parent))
        {
            throw new GeneratorException(
                    "getParentPath(): invoked on a closed loop");
        }
        if (parent == null)
        {
            return;
        }
        result.insert(0, parent.getName() + "/");
        alreadyProcessed.add(parent);
        getParentPath(parent, alreadyProcessed, result);
    }

    /**
     * An iterator of SourcePathPointers wrapping a Iterator of jxpath pointers.
     */
    private static final class SourcePathPointerIterator
    implements Iterator<SourcePathPointer>
    {
        /** The wrapped pointer, not null. */
        private final Iterator<?> jxpathPointerIterator;

        /**
         * Constructor.
         *
         * @param jxpathPointerIterator the wrapped iterator, must iterate
         *        over org.apache.commons.jxpath.Pointer elements.
         */
        public SourcePathPointerIterator(
                final Iterator<?> jxpathPointerIterator)
        {
            if (jxpathPointerIterator == null)
            {
                throw new NullPointerException(
                        "jxpathPointerIterator must not be null");
            }
            this.jxpathPointerIterator = jxpathPointerIterator;
        }

        @Override
        public boolean hasNext()
        {
            return jxpathPointerIterator.hasNext();
        }

        @Override
        public SourcePathPointer next()
        {
            Pointer pointer = (Pointer) jxpathPointerIterator.next();
            return new SourcePathPointer(pointer.getNode(), pointer.asPath());
        }

        @Override
        public void remove()
        {
            jxpathPointerIterator.remove();
        }

    }
}
