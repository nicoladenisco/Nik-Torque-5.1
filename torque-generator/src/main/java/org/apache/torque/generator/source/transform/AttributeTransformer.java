package org.apache.torque.generator.source.transform;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;

/**
 * Adds or replaces attributes in a SourceElement according to a definition
 * file. An example for a line in the definition file would be:
 * 
 * <p>
 * ${attribute(override):newAttribute}=prefix${attribute:oldAttribute}${option:suffix}
 * 
 * <p>
 * This would add the attribute newAttribute to the current sourceElement,
 * and its content would be (in java notation)
 * "prefix"
 *   + (currentElement.getAttribute("oldAttribute") == null
 *      ? ""
 *      : currentElement.getAttribute("oldAttribute"))
 *   + options.getOption("suffix")
 *   
 * <p>
 * On the left hand side, one can use attribute(override) (which replaces
 * the attribute if its already there) and attribute(noOverride) which
 * preserves the attribute if its already there.
 * On the right hand side, you can use either plain text,
 * ${attribute:attributeName} and ${option:optionName} in any combination.
 * Escape character is the backslash. Lines starting with # and empty lines
 * are disregarded.
 */
public class AttributeTransformer implements SourceTransformer
{
    /**
     * The character separating the source from the target in the definition
     * file.
     */
    private static final char SOURCE_TARGET_SEPARATION_CHAR = '=';

    /**
     * The character which starts a comment in the definition file.
     */
    private static final String COMMENT_CHAR = "#";

    /**
     * The escape character in the definition file.
     */
    private static final char ESCAPE_CHAR = '\\';

    /**
     * The character which starts a replacement in the definition file.
     */
    private static final char DEFINITION_CHAR = '$';

    /**
     * The character which starts the definition of a replacement
     * in the definition file.
     */
    private static final char DEFINITION_START_CHAR = '{';

    /**
     * The character which ends a replacement in the definition file.
     */
    private static final char DEFINITION_END_CHAR = '}';

    /**
     * The character which is used as separation in a replacement definition
     * in the definition file
     */
    private static final char DEFINITION_TYPE_SEPARATION_CHAR = ':';

    /** The logger of the class. */
    private static Log log = LogFactory.getLog(AttributeTransformer.class);

    /**
     * The list of transform rules which is created from the definition file.
     */
    private List<TransformRule> transformRules = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param transformDefinition the Reader from which the transformDefinition
     *        is read. The Reader is closed after processing.
     *
     * @throws SourceTransformerException if an error occurs while reading or
     *         parsing the transformDefinition.
     */
    public AttributeTransformer(Reader transformDefinition)
            throws SourceTransformerException
    {
        try (BufferedReader reader = new BufferedReader(transformDefinition))
        {
            String line = reader.readLine();
            while (line != null)
            {
                try
                {
                    if (line.trim().startsWith(COMMENT_CHAR)
                            || line.trim().length() == 0)
                    {
                        continue;
                    }
                    int equalsPos
                    = line.indexOf(SOURCE_TARGET_SEPARATION_CHAR);
                    String target = line.substring(0, equalsPos).trim();
                    String source = line.substring(equalsPos + 1).trim();

                    List<Definition> targetDefinitions = parse(target, false);
                    if (targetDefinitions.size() != 1)
                    {
                        log.error("While parsing \"" + line + "\" : "
                                + targetDefinitions.size()
                                + " definitions found before "
                                + SOURCE_TARGET_SEPARATION_CHAR
                                + " at position "
                                + equalsPos
                                + ", must be 1");
                    }

                    List<Definition> sourceDefinitions = parse(source, true);
                    TransformRule transformRule = new TransformRule(
                            targetDefinitions.get(0),
                            sourceDefinitions);
                    transformRules.add(transformRule);
                }
                finally
                {
                    line = reader.readLine();
                }
            }
        }
        catch (IOException e)
        {
            throw new SourceTransformerException(
                    "Could not read transformDefinition",
                    e);
        }
    }


    /**
     * Adds attributes to the sourceElement according to the
     * transformDefinition.
     *
     * @param toTransform the object to transform. Must be a
     *        SourceElement.
     *
     * @param controllerState the current state of the controller, not null.
     *
     * @return the source element with the additional attributes added.
     *
     * @throws SourceTransformerException if an error occurs during
     *         transforming.
     */
    @Override
    public Object transform(
            Object toTransform,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        SourceElement toTransformElement = (SourceElement) toTransform;
        for (TransformRule transformRule : transformRules)
        {
            StringBuilder sourceResult = new StringBuilder();
            for (Definition definition : transformRule.getSource())
            {
                Definition.Type type = definition.getType();
                if (Definition.Type.PLAIN == type)
                {
                    sourceResult.append(definition.getContent());
                }
                else if (Definition.Type.ATTRIBUTE == type)
                {
                    Object attributeValue = toTransformElement.getAttribute(
                            definition.getContent());
                    if (attributeValue != null)
                    {
                        sourceResult.append(attributeValue.toString());
                    }
                }
                else if (Definition.Type.OPTION == type)
                {
                    Object optionValue = controllerState.getOption(
                            definition.getContent());
                    if (optionValue != null)
                    {
                        sourceResult.append(optionValue.toString());
                    }
                }
                else
                {
                    throw new SourceTransformerException(
                            "Unknown definition Type" + type + " in source");
                }
            }
            Definition target = transformRule.getTarget();
            if (Definition.Type.ATTRIBUTE_NO_OVERRIDE == target.getType())
            {
                String attributeName = target.getContent();
                if (toTransformElement.getAttribute(attributeName) == null)
                {
                    toTransformElement.setAttribute(
                            attributeName,
                            sourceResult.toString());
                    if (log.isTraceEnabled())
                    {
                        log.trace("Setting Attribute "
                                + attributeName
                                + " on element "
                                + toTransformElement.getName()
                                + " to "
                                + sourceResult);
                    }
                }
                else
                {
                    if (log.isTraceEnabled())
                    {
                        log.trace("Attribute "
                                + attributeName
                                + " already set on element "
                                + toTransformElement.getName()
                                + " skipping");
                    }
                }
            }
            else if (Definition.Type.ATTRIBUTE_OVERRIDE == target.getType())
            {
                String attributeName = target.getContent();
                if (toTransformElement.getAttribute(attributeName) == null)
                {
                    if (log.isTraceEnabled())
                    {
                        log.trace("Setting Attribute "
                                + attributeName
                                + " on element "
                                + toTransformElement.getName()
                                + " to "
                                + sourceResult);
                    }
                }
                else
                {
                    if (log.isTraceEnabled())
                    {
                        log.trace("Overriding Attribute "
                                + attributeName
                                + " on element "
                                + toTransformElement.getName()
                                + " with value "
                                + sourceResult);
                    }
                }
                toTransformElement.setAttribute(
                        attributeName,
                        sourceResult.toString());
            }
            else
            {
                throw new SourceTransformerException("Unknown target type "
                        + target.getType());
            }
        }
        return toTransform;
    }

    /**
     * Parses a String into a List of Definitions.
     *
     * @param toParse the String to parse, not null.
     * @param source whether the String is the source part (rhs) (true) or
     *        the target part (lhs) (false) of the transform rule to parse.
     *
     * @return the list of definitions, not null.
     *
     * @throws SourceTransformerException if the syntax of the string is
     *         incorrect.
     */
    List<Definition> parse(String toParse, boolean source)
            throws SourceTransformerException
    {
        List<Definition> result = new ArrayList<>();
        ParseState parseState = new ParseState();
        for (int position = 0; position < toParse.length(); ++position)
        {
            char current = toParse.charAt(position);

            if (parseState.isDefinitionStart())
            {
                parseAfterDefinitionStart(
                        current,
                        parseState,
                        toParse,
                        position);
                continue;
            }
            if (parseState.isEscape())
            {
                parseAfterEscape(current, parseState);
                continue;
            }
            if (current == ESCAPE_CHAR)
            {
                parseState.setEscape(true);
                continue;
            }
            if (current == DEFINITION_CHAR)
            {
                if (parseState.isDefinitionStart())
                {
                    log.error("While parsing \"" + toParse + "\" : "
                            + DEFINITION_CHAR
                            + " must be escaped inside Definition"
                            + " at Position "
                            + position);
                    throw new SourceTransformerException(
                            DEFINITION_CHAR
                            + " must be followed by "
                            + DEFINITION_START_CHAR);
                }
                if (parseState.getDefinitionValue().length() > 0)
                {
                    checkTypeAllowed(parseState.getDefinitionType(), source);
                    result.add(new Definition(
                            parseState.getDefinitionType(),
                            parseState.getDefinitionValue().toString()));
                    parseState.resetDefinitionValue();
                }
                parseState.setDefinitionStart(true);
                continue;
            }
            if (!parseState.isInDefinition())
            {
                parseState.getDefinitionValue().append(current);
                continue;
            }
            // we are inside a definition
            if (current == DEFINITION_END_CHAR)
            {
                Definition definition = parseDefinitionEnd(
                        parseState,
                        source,
                        toParse,
                        position);
                result.add(definition);
                continue;
            }
            if (parseState.isInType())
            {
                parseInType(current, parseState, source, toParse, position);
                continue;
            }
            parseState.getDefinitionValue().append(current);
        }
        if (Definition.Type.PLAIN != parseState.getDefinitionType())
        {
            log.error("While parsing \"" + toParse + "\" : "
                    + "Definition not closed at end of String.");
            throw new SourceTransformerException(
                    "Definition not closed at end of String.");
        }
        if (parseState.getDefinitionValue().length() > 0)
        {
            checkTypeAllowed(parseState.getDefinitionType(), source);
            result.add(
                    new Definition(
                            parseState.getDefinitionType(),
                            parseState.getDefinitionValue().toString()));
        }

        return result;
    }


    /**
     * Parse a character while inside a definition type.
     *
     * @param current the character to parse.
     * @param parseState the parsing state, not null.
     * @param source whether the String is the source part (rhs) (true) or
     *        the target part (lhs) (false) of the transform rule to parse.
     * @param toParse the complete string to parse, for error messages.
     * @param position the parsing position, for error messages.
     *
     * @throws SourceTransformerException if a charter is encountered
     *        which is not valid at this position.
     */
    private void parseInType(
            char current,
            ParseState parseState,
            boolean source,
            String toParse,
            int position)
                    throws SourceTransformerException
    {
        if (current != DEFINITION_TYPE_SEPARATION_CHAR)
        {
            parseState.getDefinitionTypeBuffer().append(current);
        }
        else
        {
            String typeString
            = parseState.getDefinitionTypeBuffer().toString();
            Definition.Type type = null;
            for (Definition.Type possibleType : Definition.Type.values())
            {
                if (possibleType.getName().equals(typeString))
                {
                    checkTypeAllowed(possibleType, source);
                    type = possibleType;
                    break;
                }
            }
            if (type == null)
            {
                log.error("While parsing \"" + toParse + "\" : "
                        + "Unknown definition type "
                        + typeString
                        + " at Position "
                        + position);
                throw new SourceTransformerException(
                        "Unknown definition type");
            }
            parseState.setDefinitionType(type);
            parseState.resetDefinitionTypeBuffer();
            parseState.setInType(false);
        }
    }

    /**
     * Parse the definition end character.
     *
     * @param parseState the parsing state, not null.
     * @param source whether the String is the source part (rhs) (true) or
     *        the target part (lhs) (false) of the transform rule to parse.
     * @param toParse the complete string to parse, for error messages.
     * @param position the parsing position, for error messages.
     *
     * @return the Definition which parsing was just ended.
     *
     * @throws SourceTransformerException if a charter is encountered
     *        which is not valid at this position.
     */
    private Definition parseDefinitionEnd(
            ParseState parseState,
            boolean source,
            String toParse,
            int position)
                    throws SourceTransformerException
    {
        if (parseState.isInType())
        {
            log.error("While parsing \"" + toParse + "\" : "
                    + "Seen "
                    + DEFINITION_END_CHAR
                    + " before "
                    + DEFINITION_TYPE_SEPARATION_CHAR
                    + " at Position "
                    + position);
            throw new SourceTransformerException(
                    "Seen "
                            + DEFINITION_END_CHAR
                            + " before "
                            + DEFINITION_TYPE_SEPARATION_CHAR);
        }
        checkTypeAllowed(parseState.getDefinitionType(), source);
        Definition result = new Definition(
                parseState.getDefinitionType(),
                parseState.getDefinitionValue().toString());
        parseState.setDefinitionType(Definition.Type.PLAIN);
        parseState.resetDefinitionValue();
        parseState.setInDefinition(false);
        return result;
    }

    /**
     * Parse the character after the escape character.
     *
     * @param current the character to parse.
     * @param parseState the parsing state, not null.
     */
    private void parseAfterEscape(char current, ParseState parseState)
    {
        if (parseState.isInType())
        {
            parseState.getDefinitionTypeBuffer().append(current);
        }
        else
        {
            parseState.getDefinitionValue().append(current);
        }
        parseState.setEscape(false);
    }


    /**
     * Parses a character if definitionStart is true in parseState.
     *
     * @param current the current character to parse.
     * @param parseState the current parse state.
     * @param toParse the complete string to parse (for error messages).
     * @param position the parse position (for error messages).
     *
     * @throws SourceTransformerException if an illegal character is
     *         encountered.
     */
    private void parseAfterDefinitionStart(
            char current,
            ParseState parseState,
            String toParse,
            int position)
                    throws SourceTransformerException
    {
        if (current != DEFINITION_START_CHAR)
        {
            log.error("While parsing \"" + toParse + "\" : "
                    + DEFINITION_CHAR
                    + " must be followed by "
                    + DEFINITION_START_CHAR
                    + " at Position "
                    + position);
            throw new SourceTransformerException(
                    DEFINITION_CHAR
                    + " must be followed by "
                    + DEFINITION_START_CHAR);
        }
        parseState.setDefinitionStart(false);
        parseState.setInDefinition(true);
        parseState.setInType(true);
    }

    /**
     * Checks whether the type is allowed in the current part of the transform
     * rule.
     *
     * @param type The type to check, not null.
     * @param source whether the type is in the source part (rhs) (true) or
     *        the target part (lhs) (false) of the transform rule to parse.
     *
     * @throws SourceTransformerException if the type is not allowed in its
     *         current place.
     */
    private void checkTypeAllowed(Definition.Type type, boolean source)
            throws SourceTransformerException
    {
        if (source && !type.inSource)
        {
            log.error("Definition type "
                    + type
                    + " may not occur in source ");
            throw new SourceTransformerException(
                    "definition type not allowed in source");
        }
        if (!source && !type.inTarget)
        {
            log.error("Definition type "
                    + type
                    + " may not occur in target ");
            throw new SourceTransformerException(
                    "definition type not allowed in target");
        }
    }

    /**
     * One single element in the left hand side or right hand side of a
     * transform rule.
     */
    static class Definition
    {
        /** The possible types of the elements. */
        enum Type
        {
            /**
             * The value of an attribute from the current Source Element
             * should be inserted
             */
            ATTRIBUTE("attribute", true, false),
            /**
             * The value of an option should be inserted
             */
            OPTION("option", true, false),
            /**
             * Plain text should be inserted
             */
            PLAIN("plain", true, false),
            /**
             * An attribute should be set and overridden if it already
             * exists
             */
            ATTRIBUTE_OVERRIDE("attribute(override)", false, true),
            /**
             * An attribute should be set, but not be overridden if it already
             * exists
             */
            ATTRIBUTE_NO_OVERRIDE("attribute(noOverride)", false, true);

            /** The name of the type. */
            private String name;

            /**
             * Whether this type can occur in the right hand side (the source)
             * of a definition.
             */
            private boolean inSource;

            /**
             * Whether this type can occur in the left hand side (the target)
             * of a definition.
             */
            private boolean inTarget;


            /**
             * Constructor.
             *
             * @param name the name of the type.
             * @param inSource whether this type can occur in the right hand
             *        side (the source) of a definition
             * @param inTarget whether this type can occur in the left hand side
             *        (the target) of a definition.
             */
            private Type(String name, boolean inSource, boolean inTarget)
            {
                this.name = name;
                this.inSource = inSource;
                this.inTarget = inTarget;
            }

            /**
             * Returns the name (i.e. what should be in the definition).
             *
             * @return the name, not null.
             */
            public String getName()
            {
                return name;
            }

            /**
             * Returns whether the definition can occur in the source part.
             *
             * @return true if the definition can occur in the source part,
             *         false otherwise.
             */
            public boolean isInSource()
            {
                return inSource;
            }

            /**
             * Returns whether the definition can occur in the target part.
             *
             * @return true if the definition can occur in the target part,
             *         false otherwise.
             */
            public boolean isInTarget()
            {
                return inTarget;
            }

            /**
             * Returns a String for debugging the type.
             *
             * @return the name of the type.
             */
            @Override
            public String toString()
            {
                return name;
            }
        }

        /**
         * The type of the element.
         */
        private Type type;

        /**
         * The content of the element (which can be the name of the attribute,
         * option, or the plain text itself)
         */
        private String content;

        /**
         * Constructor.
         *
         * @param type the type of the definition, not null.
         * @param content the content (i.e name of the attribute, option,
         *        or the plain text itself).
         */
        public Definition(Type type, String content)
        {
            if (type == null)
            {
                throw new NullPointerException("type is null");
            }
            if (content == null)
            {
                throw new NullPointerException("content is null");
            }
            this.type = type;
            this.content = content;
        }

        /**
         * Returns the type of the definition.
         *
         * @return the type, not null.
         */
        public Type getType()
        {
            return type;
        }

        /**
         * Returns the content (i.e name of the attribute, option, or the plain
         * text itself) of the definition.
         *
         * @return the content, not null.
         */
        public String getContent()
        {
            return content;
        }
    }

    /**
     * Keeps the current parsing state.
     */
    static class ParseState
    {
        /** Whether the following character is escaped. */
        private boolean escape = false;

        /** Whether we are just after DEFINITION_CHAR */
        private boolean definitionStart = false;

        /** Whether we are inside a definition. */
        private boolean inDefinition = false;

        /**
         * The string builder for the definition type.
         */
        private StringBuilder definitionTypeBuffer = new StringBuilder();

        /**
         * The parsed definition type.
         */
        private Definition.Type definitionType = Definition.Type.PLAIN;

        /** The string builder for the definition value. */
        private StringBuilder definitionValue = new StringBuilder();
        /**
         *  Whether we are before DEFINITION_TYPE_SEPARATION_CHAR
         *  in a definition.
         */
        private boolean inType = false;

        /**
         * Returns whether the following character is escaped.
         *
         * @return whether the following character is escaped.
         */
        public boolean isEscape()
        {
            return escape;
        }

        /**
         * Sets whether the following character is escaped.
         *
         * @param escape whether the following character is escaped.
         */
        public void setEscape(boolean escape)
        {
            this.escape = escape;
        }

        /**
         * Returns whether we are just after <code>DEFINITION_CHAR</code>.
         *
         * @return whether we are just after <code>DEFINITION_CHAR</code>.
         */
        public boolean isDefinitionStart()
        {
            return definitionStart;
        }

        /**
         * Sets whether we are just after <code>DEFINITION_CHAR</code>.
         *
         * @param definitionStart whether we are just after
         *        <code>DEFINITION_CHAR</code>.
         */
        public void setDefinitionStart(boolean definitionStart)
        {
            this.definitionStart = definitionStart;
        }

        /**
         * Returns whether we are inside a definition.
         *
         * @return whether we are inside a definition.
         */
        public boolean isInDefinition()
        {
            return inDefinition;
        }

        /**
         * Sets whether we are inside a definition.
         *
         * @param inDefinition whether we are inside a definition.
         */
        public void setInDefinition(boolean inDefinition)
        {
            this.inDefinition = inDefinition;
        }

        /**
         * Returns whether we are before
         * <code>DEFINITION_TYPE_SEPARATION_CHAR</code> in a definition.
         *
         * @return whether we are before
         *         <code>DEFINITION_TYPE_SEPARATION_CHAR</code> in a definition.
         */
        public boolean isInType()
        {
            return inType;
        }

        /**
         * Sets whether we are before
         * <code>DEFINITION_TYPE_SEPARATION_CHAR</code> in a definition.
         *
         * @param inType whether we are before
         *        <code>DEFINITION_TYPE_SEPARATION_CHAR</code> in a definition.
         */
        public void setInType(boolean inType)
        {
            this.inType = inType;
        }

        /**
         * Returns the string builder for the definition type.
         *
         * @return the string builder for the definition type, not null.
         */
        public StringBuilder getDefinitionTypeBuffer()
        {
            return definitionTypeBuffer;
        }

        /**
         * Resets the string builder for the definition type buffer.
         * This is done by using a new string builder, i. e. the old one
         * is discarded.
         */
        public void resetDefinitionTypeBuffer()
        {
            this.definitionTypeBuffer = new StringBuilder();
        }

        /**
         * Returns the parsed definition type.
         *
         * @return the parsed definition type.
         */
        public Definition.Type getDefinitionType()
        {
            return definitionType;
        }

        /**
         * Sets the parsed definition type.
         *
         * @param definitionType the parsed definition type.
         */
        public void setDefinitionType(Definition.Type definitionType)
        {
            this.definitionType = definitionType;
        }

        /**
         * Returns the string builder for the definition value.
         *
         * @return the string builder for the definition value, not null.
         */
        public StringBuilder getDefinitionValue()
        {
            return definitionValue;
        }

        /**
         * Resets the string builder for the definition value.
         * This is done by using a new string builder, i. e. the old one
         * is discarded.
         */
        public void resetDefinitionValue()
        {
            this.definitionValue = new StringBuilder();
        }
    }

    /**
     * A single transform rule.
     */
    static class TransformRule
    {
        /**
         * What should be set in which manner ?
         */
        private Definition target;

        /**
         * The rule of how to compose the new content.
         */
        private List<Definition> source;

        /**
         * Constructor.
         *
         * @param target What should be set in which manner; not null.
         *
         * @param source how to compose the new content, not null.
         */
        public TransformRule(Definition target, List<Definition> source)
        {
            if (target == null)
            {
                throw new NullPointerException("target is null");
            }
            if (source == null)
            {
                throw new NullPointerException("source is null");
            }
            this.target = target;
            this.source = source;
        }

        /**
         * Returns what should be set if the rule is applied.
         *
         * @return the target of the rule, not null.
         */
        public Definition getTarget()
        {
            return target;
        }

        /**
         * Returns the rule how the new content can be composed.
         * The Definitions should each be applied, the results should
         * compose a string in the order of the definitions.
         *
         * @return the source of the rule, not null.
         */
        public List<Definition> getSource()
        {
            return source;
        }
    }
}
