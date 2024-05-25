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
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * An element in the source graph.
 */
public class SourceElement implements Serializable
{
  /**
   * Serial Version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The name of the source element.
   */
  private final String name;

  /**
   * The parents of this element; may be empty but not null.
   */
  private final List<SourceElement> parents = new ParentList(this);

  /**
   * All children elements.
   */
  private final List<SourceElement> children = new ChildList(this);

  /**
   * the source element's attributes.
   */
  private final Map<String, Object> attributes
     = new LinkedHashMap<>();

  /**
   * Constructor.
   *
   * @param name the name of the element, not null.
   *
   * @throws NullPointerException if name is null.
   */
  public SourceElement(final String name)
  {
    if(name == null)
    {
      throw new NullPointerException("name must not be null");
    }
    this.name = name;
  }

  /**
   * Constructor.
   *
   * @param sourceElementName sourceElementName name of the element, not null.
   *
   * @throws NullPointerException if sourceElementName is null.
   */
  public SourceElement(final SourceElementName sourceElementName)
  {
    this(sourceElementName.getName());
  }

  /**
   * Returns the name of this source element.
   *
   * @return the name of this source element, never null.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the primary parent of this SourceElement.
   *
   * @return the primary parent of this SourceElement,
   * or null if this is a root element of the source graph.
   */
  public SourceElement getParent()
  {
    if(parents.size() == 0)
    {
      return null;
    }
    return parents.get(0);
  }

  /**
   * Returns the list of parents of this SourceElement.
   * Parents can be added and removed via the methods exposed by
   * the returned list.
   *
   * @return the list of parents of this source element, never null.
   */
  public List<SourceElement> getParents()
  {
    return parents;
  }

  /**
   * Returns all children of this SourceElement.
   * Children can be added and removed via the methods exposed by
   * the returned list.
   *
   * @return the list of children of this source element, never null.
   */
  public List<SourceElement> getChildren()
  {
    return children;
  }

  /**
   * Returns all children of this SourceElement which have the given name.
   * Modifications on the returned list have no effect
   * on the list of children of this SourceElement.
   *
   * @param name the name of the children to select, not null.
   *
   * @return the list of children of this source element with the given name,
   * never null.
   *
   * @throws NullPointerException if name is null.
   */
  public List<SourceElement> getChildren(final String name)
  {
    if(name == null)
    {
      throw new NullPointerException("name must not be null");
    }
    List<SourceElement> result = new ArrayList<>();
    for(SourceElement sourceElement : children)
    {
      if(name.equals(sourceElement.getName()))
      {
        result.add(sourceElement);
      }
    }
    return result;
  }

  /**
   * Returns all children of this SourceElement which have the given name.
   * Modifications on the returned list have no effect
   * on the list of children of this SourceElement.
   *
   * @param sourceElementName contains the name of the child to select,
   * not null.
   *
   * @return the list of children of this source element with the given name,
   * never null.
   *
   * @throws NullPointerException if sourceElementName is null.
   */
  public List<SourceElement> getChildren(final SourceElementName sourceElementName)
  {
    return getChildren(sourceElementName.getName());
  }

  /**
   * Returns the first child of this SourceElement which has
   * the given name.
   *
   * @param name the name of the child to select, not null.
   *
   * @return the first child with the given name, or null if no child with
   * the given name exits.
   *
   * @throws NullPointerException if name is null.
   */
  public SourceElement getChild(final String name)
  {
    for(SourceElement sourceElement : children)
    {
      if(name.equals(sourceElement.getName()))
      {
        return sourceElement;
      }
    }
    return null;
  }

  public SourceElement getChildByAttribute(final String nameAttr, final String valAttr)
  {
    for(SourceElement sourceElement : children)
    {
      Object attribute = sourceElement.getAttribute(nameAttr);
      if(attribute != null && attribute.equals(valAttr))
        return sourceElement;
    }
    return null;
  }

  public SourceElement getChildByAttributeAndName(final String name, final String nameAttr, final String valAttr)
  {
    for(SourceElement sourceElement : children)
    {
      if(name.equals(sourceElement.getName()))
      {
        Object attribute = sourceElement.getAttribute(nameAttr);
        if(attribute != null && attribute.equals(valAttr))
          return sourceElement;
      }
    }
    return null;
  }

  /**
   * Returns the first child of this SourceElement which has
   * the given name.
   *
   * @param sourceElementName contains the name of the child to select,
   * not null.
   *
   * @return the first child with the given name, or null if no child with
   * the given name exits.
   *
   * @throws NullPointerException if sourceElementName is null.
   */
  public SourceElement getChild(final SourceElementName sourceElementName)
  {
    return getChild(sourceElementName.getName());
  }

  /**
   * Returns whether children with the given name exist.
   *
   * @param name the name of the child element, not null.
   *
   * @return true if children with the given name exist, false otherwise.
   *
   * @throws NullPointerException if name is null.
   */
  public boolean hasChild(final String name)
  {
    return SourcePath.hasChild(this, name);
  }

  /**
   * Returns the first child of this source element.
   *
   * @return the first child, or null if this source element has no children.
   */
  public SourceElement getFirstChild()
  {
    if(children.isEmpty())
    {
      return null;
    }
    return children.get(0);
  }

  /**
   * Returns the last child of this source element.
   *
   * @return the last child, or null if this source element has no children.
   */
  public SourceElement getLastChild()
  {
    if(children.isEmpty())
    {
      return null;
    }
    return children.get(children.size() - 1);
  }

  /**
   * Returns all the following elements after this element
   * with the given name.
   * If name is null, all following elements are returned.
   * If this element has no parent, an empty list is returned.
   *
   * @param name the name of the following elements to select,
   * or null to select all following elements.
   * @return a list containing the following elements with the given name,
   * never null.
   *
   * @see <a href="http://www.w3.org/TR/xpath#axes"></a>
   */
  public List<SourceElement> getFollowing(final String name)
  {
    return SourcePath.getFollowing(this, name);
  }

  /**
   * Returns the following element after this element
   * If this element has no parent, null is returned.
   *
   * @param parent the parent of this source Element in which child
   * list the following element should be looked for.
   *
   * @return the following source element,
   * or null if no following source element exists.
   *
   * @throws IllegalArgumentException if parent is not a parent
   * of this SourceElement.
   */
  public SourceElement getFollowingSourceElement(final SourceElement parent)
  {
    if(!parents.contains(parent))
    {
      throw new IllegalArgumentException(
         "parent is not a parent of this SourceElement");
    }
    ListIterator<SourceElement> sameLevelIt
       = SourcePath.getSiblingIteratorPositionedOnSelf(this, parent);
    if(!sameLevelIt.hasNext())
    {
      return null;
    }
    return sameLevelIt.next();
  }

  /**
   * Returns whether a following element exists as a child of the parent of
   * this element.
   *
   * @return true if a following element exists, false if not.
   */
  public boolean hasFollowing()
  {
    return SourcePath.hasFollowing(this);
  }

  /**
   * Returns whether an preceding exists as a child of the parent of
   * this element.
   *
   * @return true if a preceding element exists, false if not.
   */
  public boolean hasPreceding()
  {
    return SourcePath.hasPreceding(this);
  }

  /**
   * Returns whether a following element exists as a child of the parent of
   * this element, which has the same name as this source element.
   *
   * @return true if a following sibling exists, false if not.
   */
  public boolean hasFollowingSibling()
  {
    return SourcePath.hasFollowingSibling(this);
  }

  /**
   * Returns whether an preceding exists as a child of the parent of
   * this element, which has the same name as this source element.
   *
   * @return true if a preceding sibling exists, false if not.
   */
  public boolean hasPrecedingSibling()
  {
    return SourcePath.hasPrecedingSibling(this);
  }

  /**
   * Returns all the preceding elements before this element
   * with the given name.
   * If name is null, all preceding elements are returned.
   * If this element has no parent, an empty list is returned.
   *
   * @param name the name of the preceding elements to select,
   * or null to select all preceding elements.
   * @return a list containing the following elements with the given name,
   * never null.
   *
   * @see <a href="http://www.w3.org/TR/xpath#axes"></a>
   */
  public List<SourceElement> getPreceding(final String name)
  {
    return SourcePath.getPreceding(this, name);
  }

  /**
   * Returns the preceding element after this element.
   * If this element has no parent, null is returned.
   *
   * @param parent the parent of this source Element in which child
   * list the following element should be looked for.
   *
   * @return the preceding source element,
   * or null if no preceding source element exists.
   *
   * @throws IllegalArgumentException if parent is not a parent
   * of this SourceElement.
   */
  public SourceElement getPrecedingSourceElement(final SourceElement parent)
  {
    if(!parents.contains(parent))
    {
      throw new IllegalArgumentException(
         "parent is not a parent of this SourceElement");
    }
    ListIterator<SourceElement> sameLevelIt
       = SourcePath.getSiblingIteratorPositionedOnSelf(this, parent);
    sameLevelIt.previous();
    if(!sameLevelIt.hasPrevious())
    {
      return null;
    }
    return sameLevelIt.previous();
  }

  /**
   * Returns the object stored in the attribute with key null.
   *
   * @return the stored object, or null if no object is stored
   * under the key null.
   */
  public Object getTextAttribute()
  {
    return attributes.get(null);
  }

  /**
   * Returns the object stored in a given attribute.
   *
   * @param name the name of the attribute, can be null.
   *
   * @return the stored object, or null if no object is stored under that key.
   */
  public Object getAttribute(final String name)
  {
    return attributes.get(name);
  }

  /**
   * Returns the object stored in a given attribute.
   *
   * @param sourceAttributeName contains the name of the attribute, not null.
   *
   * @return the stored object, or null if no object is stored under that key.
   *
   * @throws NullPointerException if sourceAttributeName is null.
   */
  public Object getAttribute(final SourceAttributeName sourceAttributeName)
  {
    return getAttribute(sourceAttributeName.getName());
  }

  /**
   * Sets the attribute of a Source element.
   *
   * @param name the name of the attribute.
   * @param value the value of the attribute,
   * or null to remove the attribute.
   *
   * @return the previous value of this attribute.
   */
  public Object setAttribute(final String name, final Object value)
  {
    if(value == null)
    {
      return attributes.remove(name);
    }
    return attributes.put(name, value);
  }

  /**
   * Sets the attribute of a Source element.
   *
   * @param sourceAttributeName contains the name of the attribute, not null.
   * @param value the value of the attribute,
   * or null to remove the attribute.
   *
   * @return the previous value of this attribute.
   */
  public Object setAttribute(
     final SourceAttributeName sourceAttributeName,
     final Object value)
  {
    return setAttribute(sourceAttributeName.getName(), value);
  }

  /**
   * Returns the name of all set attributes. Note : null may be contained
   * in the set.
   *
   * @return the name of all set values.
   */
  public Set<String> getAttributeNames()
  {
    return attributes.keySet();
  }

  /**
   * Creates a deep copy of this RichSourceelementImpl object.
   * All the elements in the source graph of this Element are copied as well
   * (i.e the copy contains the children, the children's children, ....,
   * the parents, the parent's parents...)
   *
   * @return the copy, not null.
   */
  public SourceElement copy()
  {
    Map<SourceElement, SourceElement> copied
       = new HashMap<>();
    return copy(this, copied);
  }

  /**
   * Deep copies the content of one RichSourceElementImpl object into another.
   *
   * @param toCopy the source element, not null.
   * @param copiedElements Map containing all source elements which are
   * already copied.
   *
   * @return the copy of the source, not null.
   */
  private SourceElement copy(final SourceElement toCopy,
     final Map<SourceElement, SourceElement> copiedElements)
  {
    SourceElement copied = copiedElements.get(toCopy);
    if(copied != null)
    {
      return copied;
    }
    copied = new SourceElement(toCopy.getName());
    copiedElements.put(toCopy, copied);

    for(String attributeName : toCopy.getAttributeNames())
    {
      copied.setAttribute(attributeName, toCopy.getAttribute(attributeName));
    }

    {
      List<SourceElement> childrenOfCopied = copied.getChildren();
      for(SourceElement child : toCopy.getChildren())
      {
        SourceElement copiedChild = copy(child, copiedElements);
        if(!childrenOfCopied.contains(copiedChild))
        {
          childrenOfCopied.add(copiedChild);
        }
      }
    }

    {
      List<SourceElement> parentsOfCopied = copied.getParents();
      for(SourceElement parent : toCopy.getParents())
      {
        SourceElement copiedParent
           = copy(parent, copiedElements);
        if(!parentsOfCopied.contains(copiedParent))
        {
          parentsOfCopied.add(copiedParent);
        }
      }
    }

    return copied;
  }

  /**
   * Checks whether the source element graph of this sourceElement,
   * and its position therein, equals the source element graph
   * and the position of the provided SourceElement.
   * This is an expensive operation if the graphs are large.
   *
   * @param toCompare the source element to compare, may be null.
   *
   * @return true if all source elements in the toCompare tree have the equal
   * content as the source elements in this tree.
   */
  public boolean graphEquals(final SourceElement toCompare)
  {
    Set<SourceElement> alreadyCompared = new HashSet<>();
    return graphEquals(this, toCompare, alreadyCompared);
  }

  /**
   * Checks whether the source element graph of one sourceElement,
   * and its position therein, equals the source element graph
   * and the position of another SourceElement.
   * This is an expensive operation if the graphs are large.
   *
   * @param reference the reference element, may be null.
   * @param toCompare the element which is to the referenced element,
   * may be null.
   * @param compared a set of elements which are already compared
   * and which attributes and relative positions to the other
   * compared elements were equal so far.
   * @return true if the elements are equal or if equality is currently
   * checked in another recursive iteration.
   */
  private boolean graphEquals(final SourceElement reference,
     final SourceElement toCompare,
     final Set<SourceElement> compared)
  {
    if(reference == null && toCompare != null
       || reference != null && toCompare == null)
    {
      return false;
    }
    if(reference == null && toCompare == null)
    {
      return true;
    }

    if(compared.contains(reference))
    {
      // although it is not certain that reference is equal to toCompare
      // if it is contained in compared, it does mean that equality
      // was or is being checked and that place will return false
      // if equality is not given; so we can return true here.
      return true;
    }

    compared.add(reference);

    if(!reference.getName().equals(toCompare.getName()))
    {
      return false;
    }

    if(reference.getAttributeNames().size()
       != toCompare.getAttributeNames().size())
    {
      return false;
    }

    for(String attributeName : reference.getAttributeNames())
    {
      Object referenceAttributeContent
         = reference.getAttribute(attributeName);
      Object toCompareAttributeContent
         = toCompare.getAttribute(attributeName);
      if(referenceAttributeContent == null)
      {
        if(toCompareAttributeContent != null)
        {
          return false;
        }
      }
      else
      {
        if(!referenceAttributeContent.equals(
           toCompareAttributeContent))
        {
          return false;
        }
      }
    }

    if(!graphEquals(reference.getParent(), toCompare.getParent(), compared))
    {
      return false;
    }

    if(reference.getChildren().size()
       != toCompare.getChildren().size())
    {
      return false;
    }

    Iterator<SourceElement> referenceChildIt
       = reference.getChildren().iterator();
    Iterator<SourceElement> toCompareChildIt
       = toCompare.getChildren().iterator();
    while(referenceChildIt.hasNext())
    {
      SourceElement referenceChild = referenceChildIt.next();
      SourceElement toCompareChild = toCompareChildIt.next();
      if(!graphEquals(referenceChild, toCompareChild, compared))
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString()
  {
    Set<SourceElement> alreadyProcessed = new HashSet<>();
    StringBuilder result = new StringBuilder();
    toString(alreadyProcessed, result);
    return result.toString();
  }

  /**
   * Creates a String representation of the element for debugging purposes.
   * @param alreadyProcessed the elements which are already processed
   * (for avoiding loops). The current element is added to this set.
   * @param result the String builder to which the string representation
   * should be appended.
   */
  private void toString(
     final Set<SourceElement> alreadyProcessed,
     final StringBuilder result)
  {
    alreadyProcessed.add(this);
    result.append("(name=").append(name).append(",attributes=(");
    Iterator<Map.Entry<String, Object>> entryIt = attributes.entrySet().iterator();
    while(entryIt.hasNext())
    {
      Map.Entry<String, Object> entry = entryIt.next();
      result.append(entry.getKey()).append("=").append(entry.getValue());
      if(entryIt.hasNext())
      {
        result.append(",");
      }
    }

    result.append("),children=(");
    Iterator<SourceElement> childIt = children.iterator();
    while(childIt.hasNext())
    {
      SourceElement child = childIt.next();
      if(alreadyProcessed.contains(child))
      {
        result.append("<<loop detected (").append(child.name).append(")>>");
      }
      else
      {
        child.toString(alreadyProcessed, result);
      }
      if(childIt.hasNext())
      {
        result.append(",");
      }
    }
    result.append("))");
  }

  public String prettyDump()
  {
    Set<SourceElement> alreadyProcessed = new HashSet<>();
    StringBuilder result = new StringBuilder();
    prettyDump("", alreadyProcessed, result);
    return result.toString();
  }

  /**
   * Creates a String representation of the element for debugging purposes.
   * @param alreadyProcessed the elements which are already processed
   * (for avoiding loops). The current element is added to this set.
   * @param result the String builder to which the string representation
   * should be appended.
   */
  private void prettyDump(
     final String rientro,
     final Set<SourceElement> alreadyProcessed,
     final StringBuilder result)
  {
    alreadyProcessed.add(this);
    result.append("(name=").append(name).append(",\n").append(rientro).append("  attributes=(");
    Iterator<Map.Entry<String, Object>> entryIt = attributes.entrySet().iterator();
    while(entryIt.hasNext())
    {
      Map.Entry<String, Object> entry = entryIt.next();
      result.append(entry.getKey()).append("=").append(entry.getValue());
      if(entryIt.hasNext())
      {
        result.append(",");
      }
    }

    result.append("),\n").append(rientro).append("  children=(");
    Iterator<SourceElement> childIt = children.iterator();
    while(childIt.hasNext())
    {
      SourceElement child = childIt.next();
      if(alreadyProcessed.contains(child))
      {
        result.append("<<loop detected (").append(child.name).append(")>>");
      }
      else
      {
        child.prettyDump(rientro + "    ", alreadyProcessed, result);
      }
      if(childIt.hasNext())
      {
        result.append(",\n").append(rientro);
      }
    }
    result.append("))");
  }

  public void xmlDump(Writer wr)
     throws IOException
  {
    Set<SourceElement> alreadyProcessed = new HashSet<>();
    xmlDump("", alreadyProcessed, wr);
  }

  /**
   * Creates a String representation of the element for debugging purposes.
   * @param alreadyProcessed the elements which are already processed
   * (for avoiding loops). The current element is added to this set.
   * @param result the String builder to which the string representation
   * should be appended.
   */
  private void xmlDump(
     final String rientro,
     final Set<SourceElement> alreadyProcessed,
     final Writer result)
     throws IOException
  {
    alreadyProcessed.add(this);
    result.append("<").append(name).append(">\n");

    if(!attributes.isEmpty())
    {
      result.append("  <attributes>\n");
      for(Map.Entry<String, Object> entry : attributes.entrySet())
      {
        String key = entry.getKey();
        Object val = entry.getValue();

        result.append("    <").append(key).append(">")
           .append(val.toString()).append("</").append(key).append(">\n");
      }
      result.append("  </attributes>\n");
    }

    result.append("  <children>\n");
    Iterator<SourceElement> childIt = children.iterator();
    while(childIt.hasNext())
    {
      SourceElement child = childIt.next();
      if(alreadyProcessed.contains(child))
      {
        result.append(rientro).append("  <loop-detected name=\"").append(child.name).append("\"/>\n");
      }
      else
      {
        child.xmlDump(rientro + "  ", alreadyProcessed, result);
      }
      if(childIt.hasNext())
      {
        result.append("\n").append(rientro);
      }
    }
    result.append("  </children>\n");
    result.append("</").append(name).append(">\n");
  }

  /**
   * A list of children which overrides the add and remove methods
   * such that the parents of the source element are updated as well.
   */
  private static class ChildList extends AbstractList<SourceElement>
  {
    /** The source element to which this child list belongs, not null. */
    private final SourceElement sourceElement;

    /** The children list, not null. */
    private final List<SourceElement> children
       = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param sourceElement The source element to which
     * this child list belongs, not null.
     *
     * @throws NullPointerException if <code>sourceElement</code> is null.
     */
    public ChildList(final SourceElement sourceElement)
    {
      if(sourceElement == null)
      {
        throw new NullPointerException(
           "sourceElement must not be null");
      }
      this.sourceElement = sourceElement;
    }

    @Override
    public SourceElement get(final int index)
    {
      return children.get(index);
    }

    @Override
    public int size()
    {
      return children.size();
    }

    @Override
    public void add(final int position, final SourceElement child)
    {
      if(children.contains(child))
      {
        throw new IllegalArgumentException(
           "Element " + child + " is already a child of "
           + sourceElement);
      }
      children.add(position, child);
      List<SourceElement> parents = child.getParents();
      if(!parents.contains(sourceElement))
      {
        parents.add(sourceElement);
      }
    }

    @Override
    public SourceElement remove(final int index)
    {
      SourceElement result = children.remove(index);
      result.getParents().remove(sourceElement);
      return result;
    }

    @Override
    public SourceElement set(final int index, final SourceElement child)
    {
      // allow setting an already contained child at the same position,
      // but throw an error if the child is set at other position.
      if(children.contains(child) && !children.get(index).equals(child))
      {
        throw new IllegalArgumentException(
           "Element " + child + " is already a child of "
           + sourceElement);
      }
      SourceElement previousChild = children.set(index, child);
      previousChild.getParents().remove(sourceElement);
      List<SourceElement> parents = child.getParents();
      if(!parents.contains(sourceElement))
      {
        parents.add(sourceElement);
      }
      return previousChild;
    }
  }

  /**
   * Overrides the add and remove methods such that the children of the
   * source element are updated as well.
   */
  private static class ParentList extends AbstractList<SourceElement>
  {
    /** The source element to which this parent list belongs, not null. */
    private final SourceElement sourceElement;

    /** The parent list, not null. */
    private final List<SourceElement> parents
       = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param sourceElement The source element to which
     * this parent list belongs, not null.
     *
     * @throws NullPointerException if <code>sourceElement</code> is null.
     */
    public ParentList(final SourceElement sourceElement)
    {
      if(sourceElement == null)
      {
        throw new NullPointerException(
           "sourceElement must not be null");
      }
      this.sourceElement = sourceElement;
    }

    @Override
    public SourceElement get(final int index)
    {
      return parents.get(index);
    }

    @Override
    public int size()
    {
      return parents.size();
    }

    @Override
    public void add(final int position, final SourceElement parent)
    {
      if(parents.contains(parent))
      {
        throw new IllegalArgumentException(
           "Element " + parent + " is already a parent of "
           + sourceElement);
      }
      parents.add(position, parent);
      List<SourceElement> children = parent.getChildren();
      if(!children.contains(sourceElement))
      {
        children.add(sourceElement);
      }
    }

    @Override
    public SourceElement remove(final int index)
    {
      SourceElement result = parents.remove(index);
      result.getChildren().remove(sourceElement);
      return result;
    }

    @Override
    public SourceElement set(final int index, final SourceElement parent)
    {
      // allow setting an already contained parent at the same position,
      // but throw an error if the parent is set at other position.
      if(parents.contains(parent) && !parents.get(index).equals(parent))
      {
        throw new IllegalArgumentException(
           "Element " + parent + " is already a parent of "
           + sourceElement);
      }
      SourceElement previousParent = parents.set(index, parent);
      previousParent.getChildren().remove(sourceElement);
      List<SourceElement> children = parent.getChildren();
      if(!children.contains(sourceElement))
      {
        children.add(sourceElement);
      }
      return previousParent;
    }
  }

  /**
   * Gets the elements which can be reached from this element by a given path.
   *
   * @param path the path to use, not null.
   * @return the list of matching source elements, not null, may be empty.
   * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
   */
  public List<SourceElement> getElements(final String path)
  {
    return SourcePath.getElements(this, path);
  }

  /**
   * Gets a single source element which can be reached from this element by a given path.
   *
   * @param path the path to use, not null.
   * @return the single matching source elements, may be null
   * @see <a href="http://www.w3.org/TR/xpath#axes">xpath axes</a>
   */
  public SourceElement getElement(final String path)
  {
    List<SourceElement> ls = getElements(path);
    return ls.isEmpty() ? null : ls.get(0);
  }

  /**
   * Crea accumulatore per semplificare fusione di stringhe in velocity.
   * @return
   */
  public Accumulator createAccumulator()
  {
    return new Accumulator();
  }
}
