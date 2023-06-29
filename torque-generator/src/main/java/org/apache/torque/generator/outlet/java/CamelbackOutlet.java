package org.apache.torque.generator.outlet.java;

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

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.processor.string.Camelbacker;
import org.apache.torque.generator.processor.string.WrapReservedJavaWords;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Transform a String to its camelback version. This is typically
 * useful when creating java class or attribute names.
 *
 * The name can be truncated before/after special characters, other
 * special characters can be removed, and still other characters can be removed
 * plus the next character is transformed into upper case.
 *
 * If wrapReservedJavaWords is set to true, the result will be prepended with
 * an underscore if the result of the transwormation would be a reserved word
 * within the java syntax (e.g. int, for etc...)
 */
public class CamelbackOutlet extends StringInputOutlet
{
    /**
     * The processor which does the camelback processing.
     */
    private final Camelbacker camelbacker = new Camelbacker();

    /**
     * The processor which wraps reserved java words.
     */
    private final WrapReservedJavaWords reservedWordsWrapper
        = new WrapReservedJavaWords();

    /** Whether reserved java words are wrapped. */
    private boolean wrapReservedJavaWords = false;

    /**
     * Constructor.
     *
     * @param qualifiedName the unique name of the outlet, not null.
     */
    public CamelbackOutlet(QualifiedName qualifiedName)
    {
        super(qualifiedName);
    }

    /**
     * Sets which characters are removed from the processed String.
     * Default is "."
     *
     * @param removeWithoutUppercase a String containing all characters
     *        which are simply removed from the input String.
     */
    public void setRemoveWithoutUppercase(String removeWithoutUppercase)
    {
        camelbacker.setRemoveWithoutUppercase(removeWithoutUppercase);
    }

    /**
     * Sets the characters which are removed from the processed String
     * and cause the following character to be converted to upper case.
     * Default is "_-"
     *
     * @param removeWithUppercase a String containing all characters which are
     *        removed from the input String and which cause the following
     *        character to be converted to upper case.
     */
    public void setRemoveWithUppercase(String removeWithUppercase)
    {
        camelbacker.setRemoveWithUppercase(removeWithUppercase);
    }

    /**
     * Sets whether all characters in the processed String should be made
     * lower Case by default (i.e if none of the camelback rules is applicable).
     * Default is true.
     *
     * @param defaultLowerCase true if all characters are converted to lower
     *        case by default, false if not.
     */
    public void setDefaultLowerCase(boolean defaultLowerCase)
    {
        camelbacker.setDefaultLowerCase(defaultLowerCase);
    }

    /**
     * Sets the separation characters which define the prefix to be removed.
     * If one of the characters in this String
     * is encountered in the processed String, the  part of the processed
     * String before it are ignored. The character itself is retained,
     * unless it is removed by another rule.
     *
     * @param ignorePartBefore the separation chars which define the suffix
     *        to be removed.
     */
    public void setIgnorePartBefore(String ignorePartBefore)
    {
        camelbacker.setIgnorePartBefore(ignorePartBefore);
    }

    /**
     * Sets the separation chars which define the suffix to be removed.
     * If one of the characters in this String
     * is encountered in the processed String, the part of the processed
     * String after it are ignored. The character itself is retained,
     * unless it is removed by another rule.
     *
     * @param ignorePartAfter the separation chars for removing the tail.
     */
    public void setIgnorePartAfter(String ignorePartAfter)
    {
        camelbacker.setIgnorePartBefore(ignorePartAfter);
    }

    /**
     * Sets whether the first character should always be upper case.
     * Default is true.
     *
     * @param firstCharUppercase true if the first character should always
     *        be converted to upper case, false if not.
     */
    public void setFirstCharUppercase(boolean firstCharUppercase)
    {
        camelbacker.setFirstCharUppercase(firstCharUppercase);
    }

    /**
     * Sets whether reserved java words (as output of the camelbacker)
     * are wrapped (prepended or appended).
     * Default is true.
     *
     * @param wrapReservedJavaWords true if reserved java words should be
     *        wrapped, false if not.
     */
    public void setWrapReservedJavaWords(boolean wrapReservedJavaWords)
    {
        this.wrapReservedJavaWords = wrapReservedJavaWords;
    }

    /**
     * Sets the prefix which is prepended to reserved java words.
     * Default is "_".
     *
     * @param prefix the new prefix, not null.
     */
    public void setReservedJavaWordsPrefix(String prefix)
    {

        this.reservedWordsWrapper.setPrependWhenReserved(prefix);
    }

    /**
     * Sets the suffix which is prepended to reserved java words.
     * Default is the empty String.
     *
     * @param suffix the new suffix, not null.
     */
    public void setReservedJavaWordsSuffix(String suffix)
    {

        this.reservedWordsWrapper.setAppendWhenReserved(suffix);
    }

    /**
     * Processes the input according to the camelback rules.
     *
     * @param controllerState the current state of the controller, not null.
     *
     * @throws GeneratorException in processing fails.
     */
    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        String classnameInput = getInput(controllerState);

        String result = camelbacker.process(classnameInput);
        if (wrapReservedJavaWords)
        {
            result = reservedWordsWrapper.process(result);
        }
        return new OutletResult(result);
    }
}
