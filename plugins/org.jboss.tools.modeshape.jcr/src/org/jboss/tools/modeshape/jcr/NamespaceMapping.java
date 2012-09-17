/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement;

/**
 * The <code>NamespaceMapping</code> class represents a namespace. Each namespace mapping includes a prefix and a URI.
 */
public class NamespaceMapping implements CommentedCndElement, Comparable<NamespaceMapping>, JcrModelObject {

    /**
     * The delimeter used to separate the prefix from the URI.
     */
    public static final String NOTATION_DELIMITER = "="; //$NON-NLS-1$

    /**
     * The prefix used in CND notation before the namespace mapping.
     */
    public static final String NOTATION_PREFIX = "<"; //$NON-NLS-1$

    /**
     * The suffix used in CND notation after the namespace mapping.
     */
    public static final String NOTATION_SUFFIX = ">"; //$NON-NLS-1$

    /**
     * @param namespaceMappingToCopy the namespace mapping being copied (cannot be <code>null</code>)
     * @return a new namespace mapping exactly equal to the one that was copied (never <code>null</code>)
     */
    public static NamespaceMapping copy( final NamespaceMapping namespaceMappingToCopy ) {
        final NamespaceMapping copy = new NamespaceMapping(namespaceMappingToCopy.getPrefix(), namespaceMappingToCopy.getUri());
        copy.comment = namespaceMappingToCopy.comment;
        return copy;
    }

    /**
     * An optional comment (can be <code>null</code> or empty).
     */
    private String comment;

    /**
     * The registered property change listeners (never <code>null</code>).
     */
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners;

    /**
     * The namespace prefix (can be <code>null</code> ore empty).
     */
    private final LocalName prefix;

    /**
     * The namespace URI (can be <code>null</code> or empty).
     */
    private final LocalName uri;

    /**
     * Constructs an instance with no prefix and no URI.
     */
    public NamespaceMapping() {
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
        this.prefix = new LocalName();
        this.uri = new LocalName();
    }

    /**
     * Constructs an instance with the specified prefix and URI.
     * 
     * @param initialPrefix the initial prefix (can be <code>null</code> or empty)
     * @param initialUri the initial URI (can be <code>null</code> or empty)
     */
    public NamespaceMapping( final String initialPrefix,
                             final String initialUri ) {
        this();
        this.prefix.set(initialPrefix);
        this.uri.set(initialUri);
    }

    /**
     * @param newListener the listener being registered (cannot be <code>null</code>)
     * @return <code>true</code> if registered
     */
    public boolean addListener( final PropertyChangeListener newListener ) {
        Utils.verifyIsNotNull(newListener, "newListener"); //$NON-NLS-1$
        return this.listeners.addIfAbsent(newListener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( final NamespaceMapping that ) {
        final String thisPrefix = getPrefix();
        final String thatPrefix = that.getPrefix();

        if (Utils.isEmpty(thisPrefix)) {
            if (Utils.isEmpty(thatPrefix)) {
                return 0;
            }

            // thatName is not empty
            return 1;
        }

        // thisName is not empty
        if (thatPrefix == null) {
            return 1;
        }

        // thisName and thatName are not empty
        return thisPrefix.compareTo(thatPrefix);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ((obj != null) && getClass().equals(obj.getClass())) {
            final NamespaceMapping that = (NamespaceMapping)obj;

            if (this.prefix.equals(that.prefix) && this.uri.equals(that.uri)) {
                return Utils.equals(this.comment, that.comment);
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement#getComment()
     */
    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * @return the prefix (can be <code>null</code> or empty)
     */
    public String getPrefix() {
        return this.prefix.get();
    }

    /**
     * @return the URI (can be <code>null</code> or empty)
     */
    public String getUri() {
        return this.uri.get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(this.prefix, this.uri, this.comment);
    }

    /**
     * @param property the property that was changed (never <code>null</code>)
     * @param oldValue the old value (can be <code>null</code>)
     * @param newValue the new value (can be <code>null</code>)
     */
    private void notifyChangeListeners( final PropertyName property,
                                        final Object oldValue,
                                        final Object newValue ) {
        assert (property != null) : "property is null"; //$NON-NLS-1$

        final PropertyChangeEvent event = new PropertyChangeEvent(this, property.toString(), oldValue, newValue);

        for (final Object listener : this.listeners.toArray()) {
            try {
                ((PropertyChangeListener)listener).propertyChange(event);
            } catch (final Exception e) {
                if (Platform.isRunning()) {
                    Activator.get().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
                } else {
                    System.err.print(e.getMessage());
                }

                this.listeners.remove(listener);
            }
        }
    }

    /**
     * @param listener the listener being unregistered (cannot be <code>null</code>)
     * @return <code>true</code> if removed
     */
    public boolean removeListener( final PropertyChangeListener listener ) {
        Utils.verifyIsNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement#setComment(java.lang.String)
     */
    @Override
    public boolean setComment( String newComment ) {
        if (!Utils.isEmpty(newComment)) {
            newComment = newComment.trim();
        }

        final Object oldValue = this.comment;
        final boolean changed = !Utils.equivalent(this.comment, newComment);

        if (changed) {
            this.comment = newComment;
            notifyChangeListeners(PropertyName.COMMENT, oldValue, newComment);
        }

        return changed;
    }

    /**
     * @param newPrefix the new prefix value (can be <code>null</code> or empty)
     * @return true if the prefix was changed
     */
    public boolean setPrefix( final String newPrefix ) {
        final Object oldValue = this.prefix.get();
        final boolean changed = this.prefix.set(newPrefix);

        if (changed) {
            notifyChangeListeners(PropertyName.PREFIX, oldValue, newPrefix);
        }

        return changed;
    }

    /**
     * @param newUri then new URI value (can be <code>null</code> or empty)
     * @return <code>true</code> if the URI was changed
     */
    public boolean setUri( final String newUri ) {
        final Object oldValue = this.uri.get();
        final boolean changed = this.uri.set(newUri);

        if (changed) {
            notifyChangeListeners(PropertyName.URI, oldValue, newUri);
        }

        return changed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        final StringBuilder builder = new StringBuilder();

        // comment
        if (!Utils.isEmpty(this.comment)) {
            String commentNotation = Utils.EMPTY_STRING;

            if (NotationType.LONG == notationType) {
                commentNotation += '\n';
            }

            commentNotation += CommentedCndElement.Helper.addCommentCharacters(this.comment, null) + '\n';

            // add comment above namespace
            builder.append(commentNotation);
        }

        // namespace mapping
        builder.append(NOTATION_PREFIX);
        builder.append(this.prefix.toCndNotation(notationType));

        if (NotationType.LONG == notationType) {
            builder.append(Utils.SPACE_STRING);
        }

        builder.append(NOTATION_DELIMITER);

        if (NotationType.LONG == notationType) {
            builder.append(Utils.SPACE_STRING);
        }

        builder.append(Utils.SINGLE_QUOTE);
        builder.append(this.uri.get());
        builder.append(Utils.SINGLE_QUOTE);
        builder.append(NOTATION_SUFFIX);

        return builder.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.prefix);
        builder.append(NOTATION_DELIMITER);
        builder.append(this.uri);

        return builder.toString();
    }

    /**
     * The property names whose <code>toString()</code> is used in {@link PropertyChangeEvent}s.
     */
    public enum PropertyName {

        /**
         * The comment.
         */
        COMMENT,

        /**
         * The namespace prefix.
         */
        PREFIX,

        /**
         * The namespace URI.
         */
        URI;

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return (getClass().getName() + '.' + super.toString());
        }
    }
}
