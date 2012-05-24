/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;

/**
 * @param <E> the class of the list items
 */
public abstract class ListAttributeState<E extends Comparable> extends AttributeState {

    /**
     * The CND notation for each notation type.
     */
    public static final String[] ITEM_DELIM_NOTATION = new String[] { ", ", ",", "," }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * The CND notation for each notation type.
     */
    public static final String[] PREFIX_END_DELIM_NOTATION = new String[] { Utils.SPACE_STRING, Utils.EMPTY_STRING,
            Utils.EMPTY_STRING };

    /**
     * A list of supported items (can be <code>null</code>).
     */
    private List<E> supported;

    /**
     * @param item the item being added (cannot be <code>null</code>)
     * @return <code>true</code> if successfully added
     */
    public boolean add( final E item ) {
        Utils.verifyIsNotNull(item, "item"); //$NON-NLS-1$

        if (this.supported == null) {
            this.supported = new ArrayList<E>();
        }

        boolean added = false;

        if (!this.supported.contains(item)) {
            added = this.supported.add(item);
        }

        if (added && !is()) {
            super.set(Value.IS);
        }

        return added;
    }

    /**
     * @return <code>true</code> if at least one item was cleared
     */
    public boolean clear() {
        boolean cleared = false;

        if (this.supported != null) {
            cleared = !this.supported.isEmpty();
            this.supported = null;
        }

        if (!isNot()) {
            super.set(Value.IS_NOT);
        }

        return cleared;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final ListAttributeState that = (ListAttributeState)obj;
        final List<E> thatSupportedItems = that.getSupportedItems();
        final List<E> thisSupportedItems = getSupportedItems();

        if (Utils.isEmpty(thisSupportedItems)) {
            return Utils.isEmpty(thatSupportedItems);
        }

        if (Utils.isEmpty(thatSupportedItems)) {
            return false;
        }

        if (thisSupportedItems.size() != thatSupportedItems.size()) {
            return false;
        }

        return thisSupportedItems.containsAll(thatSupportedItems);
    }

    /**
     * @param item the item being checked to see if it is already supported (cannot be <code>null</code>)
     * @return <code>true</code> if already exists
     */
    public boolean exists( final E item ) {
        Utils.verifyIsNotNull(item, "item"); //$NON-NLS-1$
        return getSupportedItems().contains(item);
    }

    /**
     * @param notationType the notation type whose CND notation prefix is being requested (cannot be <code>null</code>)
     * @return the CND notation prefix (can be <code>null</code> or empty)
     */
    protected abstract String getCndNotationPrefix( NotationType notationType );

    /**
     * @param notationType the notation type whose CND notation suffix is being requested (cannot be <code>null</code>)
     * @return the CND notation suffix (can be <code>null</code> or empty)
     */
    protected String getCndNotationSuffix( final NotationType notationType ) {
        return Utils.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getCompactCndNotation()
     */
    @Override
    protected String getCompactCndNotation() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getCompressedCndNotation()
     */
    @Override
    protected String getCompressedCndNotation() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the quote character (empty, single, or double) surrounding each item of the list (cannot be <code>null</code>)
     */
    protected String getItemQuoteCharacter() {
        final JcrPreferenceStore prefStore = JcrPreferenceStore.get();
        return prefStore.get(JcrPreferenceConstants.CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getLongCndNotation()
     */
    @Override
    protected String getLongCndNotation() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param notationType the notation type (cannot be <code>null</code>)
     * @return the delimiter after the prefix (never <code>null</code> but can be empty)
     */
    protected String getPrefixEndDelimiter(NotationType notationType) {
        if (NotationType.LONG == notationType) {
            return PREFIX_END_DELIM_NOTATION[NotationType.LONG_INDEX];
        }
        
        if (NotationType.COMPRESSED == notationType) {
            return PREFIX_END_DELIM_NOTATION[NotationType.COMPRESSED_INDEX];
        }
        
        return PREFIX_END_DELIM_NOTATION[NotationType.COMPACT_INDEX];
    }

    /**
     * @return the collection of supported items (never <code>null</code>)
     */
    public List<E> getSupportedItems() {
        if (this.supported == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(this.supported);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#hasCndNotation()
     */
    @Override
    protected boolean hasCndNotation() {
        return !isNot();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(getSupportedItems());
    }

    /**
     * @param item the item being removed (cannot be <code>null</code>)
     * @return <code>true</code> if successfully removed
     */
    public boolean remove( final E item ) {
        Utils.verifyIsNotNull(item, "item"); //$NON-NLS-1$

        if (this.supported == null) {
            return false;
        }

        final boolean removed = this.supported.remove(item);

        if (this.supported.isEmpty()) {
            this.supported = null;

            if (is()) {
                super.set(Value.IS_NOT);
            }
        }

        return removed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#set(org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value)
     */
    @Override
    public final boolean set( final Value newState ) {
        if (Value.VARIANT == newState) {
            clear();
            return super.set(Value.VARIANT);
        }

        return false; // other states set by adding and removing supported items
    }

    /**
     * @param notationType the CND notation type to use (cannot be <code>null</code>)
     * @return the CND notation (never <code>null</code> but can be empty)
     */
    protected String supportedItemsCndNotation( final NotationType notationType ) {
        final List<E> items = new ArrayList<E>(getSupportedItems());
        Collections.sort(items);

        if (items.isEmpty()) {
            return Utils.EMPTY_STRING;
        }

        String delimiter = null;

        if (NotationType.LONG == notationType) {
            delimiter = ITEM_DELIM_NOTATION[NotationType.LONG_INDEX];
        } else if (NotationType.COMPRESSED == notationType) {
            delimiter = ITEM_DELIM_NOTATION[NotationType.COMPRESSED_INDEX];
        } else {
            delimiter = ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX];
        }

        final String itemQuote = getItemQuoteCharacter();
        final boolean useQuote = !Utils.isEmpty(itemQuote);
        final StringBuilder builder = new StringBuilder();

        for (final Iterator<E> itr = items.iterator(); itr.hasNext();) {
            final E item = itr.next();

            if (useQuote) {
                builder.append(itemQuote);
            }

            if (item instanceof CndElement) {
                builder.append(((CndElement)item).toCndNotation(notationType));
            } else {
                builder.append(item.toString());
            }

            if (useQuote) {
                builder.append(itemQuote);
            }

            if (itr.hasNext()) {
                builder.append(delimiter);
            }
        }

        return builder.toString();
    }

    /**
     * @param item the item being checked (cannot be <code>null</code>)
     * @return <code>true</code> if item is contained in list
     */
    public boolean supports( final E item ) {
        Utils.verifyIsNotNull(item, "item"); //$NON-NLS-1$
        return getSupportedItems().contains(item);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        if (hasCndNotation()) {
            final StringBuilder builder = new StringBuilder();

            if (!Utils.isEmpty(getCndNotationPrefix(notationType))) {
                builder.append(getCndNotationPrefix(notationType));
            }

            builder.append(getPrefixEndDelimiter(notationType));

            if (isVariant()) {
                builder.append(AttributeState.VARIANT_CHAR);
            } else {
                // add the delimited list
                builder.append(supportedItemsCndNotation(notationType));
            }

            if (!Utils.isEmpty(getCndNotationSuffix(notationType))) {
                builder.append(getCndNotationSuffix(notationType));
            }

            return builder.toString();
        }

        return Utils.EMPTY_STRING;
    }
}
