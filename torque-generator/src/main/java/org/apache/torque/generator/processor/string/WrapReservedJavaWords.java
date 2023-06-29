package org.apache.torque.generator.processor.string;

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

import java.util.HashSet;
import java.util.Set;

/**
 * Processes an input String as follows: If the input String is a reserved word
 * in java, a suffix and/or prefix is appended to the input String and the
 * result is returned; otherwise the input string is returned unchanged.
 */
public class WrapReservedJavaWords implements StringProcessor
{
    /**
     * The prefix which should be prepended to a reserved word, not null.
     */
    private String prependWhenReserved = "_";

    /**
     * The suffix which should be prepended to a reserved word, not null.
     */
    private String appendWhenReserved = "";

    /**
     * the set of reserved words.
     */
    private static final Set<String> RESERVED_WORDS = new HashSet<>();

    static
    {
        RESERVED_WORDS.add("abstract");
        RESERVED_WORDS.add("assert");
        RESERVED_WORDS.add("boolean");
        RESERVED_WORDS.add("break");
        RESERVED_WORDS.add("byte");
        RESERVED_WORDS.add("case");
        RESERVED_WORDS.add("catch");
        RESERVED_WORDS.add("char");
        RESERVED_WORDS.add("class");
        RESERVED_WORDS.add("const");
        RESERVED_WORDS.add("continue");
        RESERVED_WORDS.add("default");
        RESERVED_WORDS.add("do");
        RESERVED_WORDS.add("double");
        RESERVED_WORDS.add("else");
        RESERVED_WORDS.add("enum");
        RESERVED_WORDS.add("extends");
        RESERVED_WORDS.add("final");
        RESERVED_WORDS.add("finally");
        RESERVED_WORDS.add("float");
        RESERVED_WORDS.add("for");
        RESERVED_WORDS.add("goto");
        RESERVED_WORDS.add("if");
        RESERVED_WORDS.add("implements");
        RESERVED_WORDS.add("import");
        RESERVED_WORDS.add("instanceof");
        RESERVED_WORDS.add("int");
        RESERVED_WORDS.add("interface");
        RESERVED_WORDS.add("long");
        RESERVED_WORDS.add("native");
        RESERVED_WORDS.add("new");
        RESERVED_WORDS.add("package");
        RESERVED_WORDS.add("private");
        RESERVED_WORDS.add("protected");
        RESERVED_WORDS.add("public");
        RESERVED_WORDS.add("return");
        RESERVED_WORDS.add("short");
        RESERVED_WORDS.add("static");
        RESERVED_WORDS.add("strictfp");
        RESERVED_WORDS.add("super");
        RESERVED_WORDS.add("switch");
        RESERVED_WORDS.add("synchronized");
        RESERVED_WORDS.add("this");
        RESERVED_WORDS.add("throw");
        RESERVED_WORDS.add("throws");
        RESERVED_WORDS.add("transient");
        RESERVED_WORDS.add("try");
        RESERVED_WORDS.add("void");
        RESERVED_WORDS.add("volatile");
        RESERVED_WORDS.add("while");
    }

    /**
     * Sets the prefix to be prepended if the input is a reserved word.
     *
     * @param prefix the new prefix, not null.
     *
     * @throws NullPointerException if prefix is null.
     */
    public void setPrependWhenReserved(String prefix)
    {
        if (prefix == null)
        {
            throw new NullPointerException("prefix must not be null");
        }
        this.prependWhenReserved = prefix;
    }

    /**
     * Sets the suffix to be appended if the input is a reserved word.
     *
     * @param suffix the new suffix, not null.
     *
     * @throws NullPointerException if suffix is null.
     */
    public void setAppendWhenReserved(String suffix)
    {
        if (suffix == null)
        {
            throw new NullPointerException("suffix must not be null");
        }
        this.appendWhenReserved = suffix;
    }

    /**
     * Checks whether the input is a reserved java word. If yes,
     * prefix and suffix are prepended and appended, and the result is returned.
     * If no, the input is returned unchanged.
     *
     * @param toProcess the input.
     *
     * @return the output.
     */
    @Override
    public String process(String toProcess)
    {
        if (RESERVED_WORDS.contains(toProcess))
        {
            return prependWhenReserved + toProcess + appendWhenReserved;
        }
        return toProcess;
    }
}
