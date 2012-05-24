package org.jboss.tools.modeshape.jcr;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.osgi.util.NLS;
import org.modeshape.common.util.HashCode;

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

/**
 * Commonly used utilities and constants.
 */
public final class Utils {

    /**
     * A double quote character string.
     */
    public static final String DOUBLE_QUOTE = "\""; //$NON-NLS-1$ 

    /**
     * And empty object array.
     */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     * An empty string constant.
     */
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /**
     * And empty string array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * A single quote character string.
     */
    public static final String SINGLE_QUOTE = "'"; //$NON-NLS-1$ 

    /**
     * A string with one space character.
     */
    public static final String SPACE_STRING = " "; //$NON-NLS-1$ 

    /**
     * @param builder the builder where the text will be appended (cannot be <code>null</code>)
     * @param addDelimiter the flag indicating if a delimiter should be added
     * @param delimiter the delimiter string added before the text if there is text (can be <code>null</code> or empty)
     * @param text the text being appended (can be <code>null</code> or empty
     * @return <code>true</code> if text was appended
     */
    public static boolean build( final StringBuilder builder,
                                 final boolean addDelimiter,
                                 final String delimiter,
                                 final String text ) {
        if (!Utils.isEmpty(text)) {
            if (addDelimiter && !Utils.isEmpty(delimiter)) {
                builder.append(delimiter);
            }

            builder.append(text);
            return true;
        }

        return false;
    }

    /**
     * @param thisObject the object being compared to the second object (can be <code>null</code>)
     * @param thatObject the object being compared with the first object (can be <code>null</code>)
     * @return <code>true</code> if both objects are <code>null</code> or are equal
     */
    public static boolean equals( final Object thisObject,
                                  final Object thatObject ) {
        if (thisObject == null) {
            return (thatObject == null);
        }

        if (thatObject == null) {
            return false;
        }

        return thisObject.equals(thatObject);
    }

    /**
     * @param thisCollection the collection being compared to the second collection (can be <code>null</code>)
     * @param thatCollection the collection being compared to the first collection (can be <code>null</code>)
     * @return <code>true</code> if both collections are <code>null</code>, both are empty, or both contain the same items
     */
    public static boolean equivalent( final Collection<?> thisCollection,
                                      final Collection<?> thatCollection ) {
        if (isEmpty(thisCollection)) {
            return isEmpty(thatCollection);
        }

        if (isEmpty(thatCollection)) {
            return false;
        }

        if (thisCollection.size() != thatCollection.size()) {
            return false;
        }

        return thisCollection.containsAll(thatCollection);
    }

    /**
     * @param thisArray the array being compared to the second array (can be <code>null</code>)
     * @param thatArray the array being compared to the first array (can be <code>null</code>)
     * @return <code>true</code> if both arrays are <code>null</code>, both are empty, or both contain the same items
     */
    public static boolean equivalent( final Object[] thisArray,
                                      final Object[] thatArray ) {
        if (isEmpty(thisArray)) {
            return isEmpty(thatArray);
        }

        if (isEmpty(thatArray)) {
            return false;
        }

        if (thisArray.length != thatArray.length) {
            return false;
        }

        return Arrays.asList(thisArray).containsAll(Arrays.asList(thatArray));
    }

    /**
     * @param thisString the string being compared with the second string (can be <code>null</code> or empty)
     * @param thatString the string being compared with the first string (can be <code>null</code> or empty)
     * @return <code>true</code> if both are <code>null</code> or empty, or they are equal
     */
    public static boolean equivalent( final String thisString,
                                      final String thatString ) {
        if (isEmpty(thisString)) {
            return isEmpty(thatString);
        }

        if (isEmpty(thatString)) {
            return false;
        }

        return thisString.equals(thatString);
    }

    /**
     * @param x the objects used to construct the hashcode (can contain <code>null</code> objects)
     * @return the hashcode
     */
    public static final int hashCode( final Object... x ) {
        return HashCode.compute(x);
    }

    /**
     * @param items the collection being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if <code>null</code> or empty
     */
    public static boolean isEmpty( final Collection<?> items ) {
        return ((items == null) || items.isEmpty());
    }

    /**
     * @param items the array being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if <code>null</code> or empty
     */
    public static boolean isEmpty( final Object[] items ) {
        return ((items == null) || (items.length == 0));
    }

    /**
     * @param text the string being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if <code>null</code> or empty
     */
    public static boolean isEmpty( final String text ) {
        return ((text == null) || text.isEmpty());
    }

    /**
     * @param items the items being upper-cased (cannot be <code>null</code>)
     * @return a new collection of upper-cased items
     */
    public static String[] toUpperCase( final String[] items ) {
        Utils.verifyIsNotNull(items, "items"); //$NON-NLS-1$

        final String result[] = new String[items.length];
        int i = 0;

        for (final String item : items) {
            result[i++] = item.toUpperCase();
        }

        return result;
    }

    /**
     * @param text the string being checked (can be <code>null</code> or empty)
     * @param name the name of the object to use in the error message (cannot be <code>null</code>)
     * @throws IllegalArgumentException if the text is <code>null</code> or empty
     */
    public static void verifyIsNotEmpty( final String text,
                                         String name ) {
        if (isEmpty(text)) {
            if ((name == null) || name.isEmpty()) {
                name = Utils.EMPTY_STRING;
            }

            throw new IllegalArgumentException(NLS.bind(Messages.stringIsEmpty, name));
        }
    }

    /**
     * @param object the object being checked (can be <code>null</code>)
     * @param name the name of the object to use in the error message (cannot be <code>null</code>)
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void verifyIsNotNull( final Object object,
                                        String name ) {
        if (object == null) {
            if ((name == null) || name.isEmpty()) {
                name = Utils.EMPTY_STRING;
            }

            throw new IllegalArgumentException(NLS.bind(Messages.objectIsNull, name));
        }
    }

    /**
     * Don't allow construction.
     */
    private Utils() {
        // nothing to do
    }

}
