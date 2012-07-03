/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tools.modeshape.jcr.text;

/**
 * A class that represents the position of a particular character in terms of the lines and columns of a character sequence.
 */
public final class Position {

    /**
     * The position is used when there is no content.
     */
    public final static Position EMPTY_CONTENT_POSITION = new Position(-1, 1, 0);

    private final int line;
    private final int column;
    private final int indexInContent;

    /**
     * @param indexInContent
     * @param line
     * @param column
     */
    public Position( int indexInContent,
                     int line,
                     int column ) {
        this.indexInContent = indexInContent < 0 ? -1 : indexInContent;
        this.line = line;
        this.column = column;

        assert this.indexInContent >= -1;
        assert this.line > 0;
        assert this.column >= 0;

        // make sure that negative index means an EMPTY_CONTENT_POSITION
        assert this.indexInContent < 0 ? this.line == 1 && this.column == 0 : true;
    }

    /**
     * Return a new position that is the addition of this position and that supplied.
     * 
     * @param position the position to add to this object; may not be null
     * @return the combined position
     */
    public Position add( Position position ) {
        if (this.getIndexInContent() < 0) {
            return position.getIndexInContent() < 0 ? EMPTY_CONTENT_POSITION : position;
        }

        if (position.getIndexInContent() < 0) {
            return this;
        }

        final int index = this.getIndexInContent() + position.getIndexInContent();
        final int line = position.getLine() + this.getLine() - 1;
        final int column = this.getLine() == 1 ? this.getColumn() + position.getColumn() : this.getColumn();

        return new Position(index, line, column);
    }

    /**
     * Get the 1-based column number of the character.
     * 
     * @return the column number; always positive
     */
    public int getColumn() {
        return this.column;
    }

    /**
     * Get the 0-based index of this position in the content character array.
     * 
     * @return the index; never negative except for the first position in an empty content.
     */
    public int getIndexInContent() {
        return this.indexInContent;
    }

    /**
     * Get the 1-based line number of the character.
     * 
     * @return the line number; always positive
     */
    public int getLine() {
        return this.line;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.indexInContent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "" + this.indexInContent + ':' + this.line + ':' + this.column;
    }
}
