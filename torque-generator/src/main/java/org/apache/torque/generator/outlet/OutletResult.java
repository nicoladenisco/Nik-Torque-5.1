package org.apache.torque.generator.outlet;

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

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.generator.GeneratorException;

/**
 * The result of an outlet. Can either be a String or a byte array.
 */
public class OutletResult
{
    /** The string result. */
    private String stringResult;

    /** The byte array result. */
    private byte[] byteArrayResult;

    /**
     * Constructor for a String result.
     *
     * @param stringResult the String result.
     */
    public OutletResult(final String stringResult)
    {
        this.stringResult = stringResult;
    }

    /**
     * Constructor for a byte array result.
     *
     * @param byteArrayResult the byte array result.
     */
    public OutletResult(final byte[] byteArrayResult)
    {
        this.byteArrayResult = byteArrayResult;
    }

    /**
     * Returns the string result.
     *
     * @return the string result, or null if this class contains
     *         a byte array result.
     */
    public String getStringResult()
    {
        return stringResult;
    }

    /**
     * Returns the byte array result.
     *
     * @return the byte array result, or null if this class contains
     *         a string result.
     */
    public byte[] getByteArrayResult()
    {
        return byteArrayResult;
    }

    /**
     * Returns whether the result type is String.
     * Note: If the instance was constructed with null byte array,
     * this method also returns true.
     *
     * @return false if the contained byteArray is not null, true otherwise.
     */
    public boolean isStringResult()
    {
        return byteArrayResult == null;
    }

    /**
     * Returns whether the result type is byte array.
     * Note: If the instance was constructed with null string,
     * this method also returns true.
     *
     * @return false if the contained String is not null, true otherwise.
     */
    public boolean isByteArrayResult()
    {
        return stringResult == null;
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder()
                .append(stringResult)
                .append(byteArrayResult);
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        OutletResult other = (OutletResult) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder()
                .append(stringResult, other.stringResult)
                .append(byteArrayResult, other.byteArrayResult);
        return equalsBuilder.isEquals();
    }


    @Override
    public String toString()
    {
        return "OutletResult [stringResult=" + stringResult
                + ", byteArrayResult=" + Arrays.toString(byteArrayResult) + "]";
    }

    /**
     * Concatenates an array of OutletResults.
     *
     * @param input the OutletResults to concatenate,
     *        not null, not empty, must not contain null, all of the same type
     *        (either all string or all byte array).
     * @return the concatenated input.
     *
     * @throws GeneratorException if the input outlet results are
     *         of a different type.
     */
    public static OutletResult concatenate(final OutletResult... input)
            throws GeneratorException
    {
        if (input == null)
        {
            throw new NullPointerException("input must not be null");
        }
        return concatenate(Arrays.asList(input));
    }

    /**
     * Concatenates a list of OutletResults.
     *
     * @param input the OutletResults to concatenate,
     *        not null, not empty, must not contain null, all of the same type
     *        (either all string or all byte array).
     * @return the concatenated input.
     *
     * @throws GeneratorException if the input outlet results are
     *         of a different type.
     */
    public static OutletResult concatenate(final Iterable<OutletResult> input)
            throws GeneratorException
    {
        if (input == null)
        {
            throw new NullPointerException("input must not be null");
        }
        if (!input.iterator().hasNext())
        {
            throw new IllegalArgumentException("input must not be empty");
        }
        Boolean isStringResult = null;
        for (OutletResult part : input)
        {
            if (!part.isByteArrayResult())
            {
                isStringResult = true;
                break;
            }
            if (!part.isStringResult())
            {
                isStringResult = false;
                break;
            }
        }
        if (isStringResult == null)
        {
            return new OutletResult((String) null);
        }
        if (Boolean.TRUE.equals(isStringResult))
        {
            StringBuilder result = new StringBuilder();
            for (OutletResult part : input)
            {
                if (!part.isStringResult())
                {
                    throw new GeneratorException(
                            "first OutletResult to concatenate is a "
                                    + "String result but a following result is a "
                                    + "byte array."
                                    + " All concatenated results must be "
                                    + "of the same type");
                }
                String partContent = part.getStringResult();
                if (partContent != null)
                {
                    result.append(partContent);
                }
            }
            return new OutletResult(result.toString());
        }
        int totalLength = 0;
        for (OutletResult part : input)
        {
            if (!part.isByteArrayResult())
            {
                throw new GeneratorException(
                        "first OutletResult to concatenate is a "
                                + "byte array result but a following result is a "
                                + "String result."
                                + " All concatenated results must be "
                                + "of the same type");
            }
            byte[] partContent = part.getByteArrayResult();
            if (partContent != null)
            {
                totalLength += partContent.length;
            }
        }
        byte[] result = new byte[totalLength];
        int alreadyFilledBytes = 0;
        for (OutletResult part : input)
        {
            byte[] partContent = part.getByteArrayResult();
            if (partContent == null)
            {
                continue;
            }
            System.arraycopy(partContent,
                    0,
                    result,
                    alreadyFilledBytes,
                    partContent.length);
            alreadyFilledBytes += partContent.length;

        }
        return new OutletResult(result);
    }
}
