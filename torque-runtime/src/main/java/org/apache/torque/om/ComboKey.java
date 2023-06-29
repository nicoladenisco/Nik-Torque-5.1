package org.apache.torque.om;

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

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class can be used as an ObjectKey to uniquely identify an
 * object within an application where the key consists of multiple
 * entities (such a String[] representing a multi-column primary key).
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:drfish@cox.net">J. Russell Smyth</a>
 * @version $Id: ComboKey.java 1879896 2020-07-15 15:03:46Z gk $
 */
public class ComboKey extends ObjectKey<SimpleKey<?>[]>
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -264927663211141894L;

    // might want to shift these to TR.props

    /** The single character used to separate key values in a string. */
    public static final char SEPARATOR = ':';

    /** The single character used to separate key values in a string. */
    public static final String SEPARATOR_STRING = ":";

    /**
     * Initializes the internal key value to <code>null</code>.
     */
    public ComboKey()
    {
        super();
    }

    /**
     * Creates an ComboKey and set its internal representation
     *
     * @param key the key value as String
     */
    public ComboKey(String key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates an ComboKey and set its internal representation
     *
     * @param key the key value
     */
    public ComboKey(SimpleKey<?>[] key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates a ComboKey that is equivalent to key.
     *
     * @param key the key value
     */
    public ComboKey(ComboKey key)
    {
        super();
        setValue(key);
    }

    /**
     * Sets the internal representation using a String of the
     * form produced by the toString method.
     *
     * @param keys the key values
     */
    public void setValue(String keys)
    {
        int startPtr = 0;
        int indexOfSep = keys.indexOf(SEPARATOR);
        List<SimpleKey<?>> tmpKeys = new ArrayList<>();
        while (indexOfSep != -1)
        {
            if (indexOfSep == startPtr)
            {
                tmpKeys.add(null);
            }
            else
            {
                char keyType = keys.charAt(startPtr);
                String keyString = keys.substring(startPtr + 1, indexOfSep);

                SimpleKey<?> newKey = null;
                switch(keyType)
                {
                case 'N':
                    newKey = new NumberKey(keyString);
                    break;
                case 'S':
                    newKey = new StringKey(keyString);
                    break;
                case 'D':
                    try
                    {
                        newKey = new DateKey(keyString);
                    }
                    catch (NumberFormatException nfe)
                    {
                        newKey = new DateKey();
                    }
                    break;
                default:
                    // unexpected key type
                }
                tmpKeys.add(newKey);
            }
            startPtr = indexOfSep + 1;
            indexOfSep = keys.indexOf(SEPARATOR, startPtr);
        }

        SimpleKey<?>[] key = new SimpleKey[tmpKeys.size()];
        for (int i = 0; i < key.length; i++)
        {
            key[i] = tmpKeys.get(i);
        }
        
        setValue(key);
    }

    /**
     * Returns the JDBC type of the key
     * as defined in <code>java.sql.Types</code>.
     *
     * @return <code>Types.ARRAY</code>.
     */
    @Override
    public int getJdbcType()
    {
        return Types.ARRAY;
    }

    /**
     * This method will return true if the conditions for a looseEquals
     * are met and in addition no parts of the keys are null.
     *
     * @param keyObj the comparison value
     * @return whether the two objects are equal
     */
    @Override
    public boolean equals(Object keyObj)
    {
        boolean isEqual = false;
        SimpleKey<?>[] key = getValue();

        if (key != null)
        {
            // check that all keys are not null
            isEqual = true;
            for (int i = 0; i < key.length && isEqual; i++)
            {
                isEqual &= key[i] != null && key[i].getValue() != null;
            }

            isEqual &= looseEquals(keyObj);
        }

        return isEqual;
    }

    /**
     * keyObj is equal to this ComboKey if keyObj is a ComboKey, String,
     * ObjectKey[], or String[] that contains the same information this key
     * contains.
     * For example A String[] might be equal to this key, if this key was
     * instantiated with a String[] and the arrays contain equal Strings.
     * Another example, would be if keyObj is an ComboKey that was
     * instantiated with a ObjectKey[] and this ComboKey was instantiated with
     * a String[], but the ObjectKeys in the ObjectKey[] were instantiated
     * with Strings that equal the Strings in this KeyObject's String[]
     * This method is not as strict as the equals method which does not
     * allow any null keys parts, while the internal key may not be null
     * portions may be, and the two object will be considered equal if
     * their null portions match.
     *
     * @param keyObj the comparison value
     * @return whether the two objects are equal
     */
    public boolean looseEquals(Object keyObj)
    {
        boolean isEqual = false;
        SimpleKey<?>[] key = getValue();

        if (key != null)
        {
            // Checks  a compound key (ObjectKey[] or String[]
            // based) with the delimited String created by the
            // toString() method.  Slightly expensive, but should be less
            // than parsing the String into its constituents.
            if (keyObj instanceof String)
            {
                isEqual = toString().equals(keyObj);
            }
            // check against a ObjectKey. Two keys are equal, if their
            // internal keys equivalent.
            else if (keyObj instanceof ComboKey)
            {
                SimpleKey<?>[] obj = (SimpleKey[])
                        ((ComboKey) keyObj).getValue();

                SimpleKey<?>[] keys2 = obj;
                isEqual = key.length == keys2.length;
                for (int i = 0; i < key.length && isEqual; i++)
                {
                    isEqual &= Objects.equals(key[i], keys2[i]);
                }
            }
            else if (keyObj instanceof SimpleKey[])
            {
                SimpleKey<?>[] keys2 = (SimpleKey[]) keyObj;
                isEqual = key.length == keys2.length;
                for (int i = 0; i < key.length && isEqual; i++)
                {
                    isEqual &= Objects.equals(key[i], keys2[i]);
                }
            }
        }
        return isEqual;
    }

    /**
     *
     * @param sb the StringBuilder to append
     * @see #toString()
     */
    @Override
    public void appendTo(StringBuilder sb)
    {
        SimpleKey<?>[] key = getValue();
        if (key != null)
        {
            for (int i = 0; i < key.length; i++)
            {
                if (key[i] != null)
                {
                    if (key[i] instanceof StringKey)
                    {
                        sb.append("S");
                    }
                    else if (key[i] instanceof NumberKey)
                    {
                        sb.append("N");
                    }
                    else if (key[i] instanceof DateKey)
                    {
                        sb.append("D");
                    }
                    else
                    {
                        // unknown type
                        sb.append("U");
                    }
                    key[i].appendTo(sb);
                }
                // MUST BE ADDED AFTER EACH KEY, IN CASE OF NULL KEY!
                sb.append(SEPARATOR);
            }
        }
    }

    /**
     * if the underlying key array is not null and the first element is
     * not null this method returns the hashcode of the first element
     * in the key.  Otherwise calls ObjectKey.hashCode()
     *
     * @return an <code>int</code> value
     */
    @Override
    public int hashCode()
    {
        SimpleKey<?>[] key = getValue();
        if (key == null)
        {
            return super.hashCode();
        }

        SimpleKey<?> sk = key[0];
        if (sk == null)
        {
            return super.hashCode();
        }

        return sk.hashCode();
    }

    /**
     * A String that may consist of one section or multiple sections
     * separated by a colon.
     * 
     * <p>
     * Each Key is represented by <code>[type N|S|D][value][:]</code>. 
     * 
     * <p>
     * <b>Example:</b> 
     * 
     * <p>
     * the ComboKey(StringKey("key1"), NumberKey(2)) is represented as
     * <code><b>Skey1:N2:</b></code>
     *
     * @return a String representation
     */
    @Override
    public String toString()
    {
        StringBuilder sbuf = new StringBuilder();
        appendTo(sbuf);
        return sbuf.toString();
    }
}
