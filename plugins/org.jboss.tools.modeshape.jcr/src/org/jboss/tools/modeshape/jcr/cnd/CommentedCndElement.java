/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import org.jboss.tools.modeshape.jcr.Utils;

/**
 * An element found in a CND file that may have an attached comment.
 */
public interface CommentedCndElement extends CndElement {

    /**
     * The characters that end a block comment. Value is {@value} .
     */
    String BLOCK_COMMENT_END_CHARS = "*/"; //$NON-NLS-1$

    /**
     * The characters that begin all lines besides the first and last line of a block comment. Value is {@value} .
     */
    String BLOCK_COMMENT_INNER_CHARS = " * "; //$NON-NLS-1$

    /**
     * The characters that start a block comment. Value is {@value} .
     */
    String BLOCK_COMMENT_START_CHARS = "/*"; //$NON-NLS-1$

    /**
     * The characters that start a line comment. Value is {@value} .
     */
    String LINE_COMMENT_CHARS = "//"; //$NON-NLS-1$

    /**
     * @return the comment associated with this CND element (can be <code>null</code> or empty)
     */
    String getComment();

    /**
     * @param text the comment text (can be <code>null</code> or empty)
     * @return <code>true</code> if the comment was changed
     */
    boolean setComment( String text );

    /**
     * Utilities related to a commented CND elements.
     */
    public class Helper {

        private static String BLOCK_COMMENT_INNER_CHARS2 = "* "; //$NON-NLS-1$

        /**
         * @param text the text that the comment characters will be added to (cannot be <code>null</code>)
         * @return the commented text (never <code>null</code>)
         */
        public static String addCommentCharacters( final String text ) {
            Utils.verifyIsNotNull(text, "text"); //$NON-NLS-1$

            final StringBuilder builder = new StringBuilder(BLOCK_COMMENT_START_CHARS);

            if (!text.startsWith("\n")) { //$NON-NLS-1$
                builder.append('\n');
            }

            builder.append(BLOCK_COMMENT_INNER_CHARS);
            builder.append(text.replace("\n", '\n' + BLOCK_COMMENT_INNER_CHARS)); //$NON-NLS-1$

            if (!text.endsWith("\n")) { //$NON-NLS-1$
                builder.append('\n');
            }

            builder.append(Utils.SPACE_STRING).append(BLOCK_COMMENT_END_CHARS).append('\n');
            return builder.toString();
        }

        /**
         * @param comment the comment whose comment characters are being removed (cannot be <code>null</code>)
         * @return the comment stripped of all comment characters (never <code>null</code>) or the original text if the comment is
         *         not properly formatted
         */
        public static String removeCommentCharacters( final String comment ) {
            Utils.verifyIsNotNull(comment, "comment"); //$NON-NLS-1$

            if (Utils.isEmpty(comment)) {
                return comment;
            }

            String result = comment.trim();

            if (result.startsWith(BLOCK_COMMENT_START_CHARS)) {
                // invalid since no end comment block
                if (result.length() == BLOCK_COMMENT_START_CHARS.length()) {
                    return comment;
                }

                // remove start comment chars
                result = comment.substring(BLOCK_COMMENT_START_CHARS.length());
                result = result.trim();

                if (result.endsWith(BLOCK_COMMENT_END_CHARS)) {
                    result = result.substring(0, (result.length() - BLOCK_COMMENT_END_CHARS.length()));
                    result = result.trim();

                    // empty comment
                    if (result.isEmpty()) {
                        return result;
                    }

                    // remove beginning inner comment chars
                    if (result.startsWith(BLOCK_COMMENT_INNER_CHARS)) {
                        result = result.substring(BLOCK_COMMENT_INNER_CHARS.length());
                    } else if (result.startsWith(BLOCK_COMMENT_INNER_CHARS2)) {
                        result = result.substring(BLOCK_COMMENT_INNER_CHARS2.length());
                    }

                    // remove other inner comment chars
                    result = result.replace('\n' + BLOCK_COMMENT_INNER_CHARS, "\n"); //$NON-NLS-1$

                    return result.trim();
                }
            } else if (comment.startsWith(LINE_COMMENT_CHARS)) {
                if (comment.length() == LINE_COMMENT_CHARS.length()) {
                    return Utils.EMPTY_STRING;
                }

                return comment.substring(LINE_COMMENT_CHARS.length()).trim();
            }

            return comment;
        }
    }
}
