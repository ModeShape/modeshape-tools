/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import java.util.LinkedList;
import org.jboss.tools.modeshape.jcr.text.Position;
import org.jboss.tools.modeshape.jcr.text.TokenStream;
import org.jboss.tools.modeshape.jcr.text.TokenStream.Tokens;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class CndTokenizerTest {

    private Tokens tokenFactory;

    private CndTokenizer tokenizer;

    private LinkedList<int[]> tokenValues;

    @Before
    public void beforeEach() {
        this.tokenizer = new CndTokenizer();
        final LinkedList<int[]> tokenValues = new LinkedList<int[]>();
        this.tokenFactory = new Tokens() {
            @Override
            public void addToken( Position position,
                                  int index ) {
                int[] token = new int[] { index, index + 1, 0 };
                tokenValues.add(token);
                // THIS IS NOT CALLED BY CndTokenizer
            }

            @Override
            public void addToken( Position position,
                                  int startIndex,
                                  int endIndex ) {
                int[] token = new int[] { startIndex, endIndex, 0 };
                tokenValues.add(token);
                // THIS IS NOT CALLED BY CndTokenizer
            }

            @Override
            public void addToken( Position position,
                                  int startIndex,
                                  int endIndex,
                                  int type ) {
                int[] token = new int[] { startIndex, endIndex, type };
                tokenValues.add(token);
            }
        };
        this.tokenValues = tokenValues;
    }

    protected void tokenize( String input ) {
        tokenizer.tokenize(new TokenStream.CharacterArrayStream(input.toCharArray()), this.tokenFactory);
    }

    @Test
    public void foo() {
        String cnd = "<java='http://www.modeshape.org/java/1.0'>"; //$NON-NLS-1$
        tokenize(cnd);

        for (int[] token : this.tokenValues) {
            System.err.println("startIndex=" + token[0] + ", endIndex=" + token[1] + ", type=" + getType(token[2]) + ", text=" + cnd.substring(token[0], token[1])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
    }

    private String getType(int i) {
        if (i == 1) return "WORD"; //$NON-NLS-1$
        if (i == 2) return "SYMBOL"; //$NON-NLS-1$
        if (i == 3) return "DECIMAL"; //$NON-NLS-1$
        if (i == 4) return "SINGLE_QUOTED_STRING"; //$NON-NLS-1$
        if (i == 5) return "DOUBLE_QUOTED_STRING"; //$NON-NLS-1$
        if (i == 6) return "COMMENT"; //$NON-NLS-1$
        if (i == 7) return "VENDOR_EXTENSION"; //$NON-NLS-1$
        return "UNKNOWN"; //$NON-NLS-1$
    }
}
