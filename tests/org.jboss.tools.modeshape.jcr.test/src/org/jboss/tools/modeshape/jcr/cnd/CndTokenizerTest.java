/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.util.LinkedList;
import org.jboss.tools.modeshape.jcr.text.ParsingException;
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

    protected void assertNextTokenIs( int startIndex,
                                      int endIndex,
                                      int type ) {
        int[] token = tokenValues.removeFirst();
        assertThat(token[0], is(startIndex));
        assertThat(token[1], is(endIndex));
        assertThat(token[2], is(type));
    }

    protected void assertNoMoreTokens() {
        assertThat(tokenValues.isEmpty(), is(true));
    }

    @Test
    public void shouldCreateNoTokensForEmptyContent() {
        tokenize("");
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateNoTokensForContentWithOnlyWhitespace() {
        tokenize("  \t   \n   \r\n  \r  ");
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForEachSymbolCharacter() {
        String content = "[]<>=-+(),";
        int numSymbols = content.length();
        tokenize(content);
        for (int i = 0; i != numSymbols; ++i) {
            assertNextTokenIs(i, i + 1, CndTokenizer.SYMBOL);
        }
        assertNoMoreTokens();
    }

    @Test
    public void shouldNotIncludeColonInListOfSymbolsSinceTheyCanAppearInNames() {
        tokenizer = new CndTokenizer();
        String content = "dna:someName";
        tokenize(content);
        assertNextTokenIs(0, content.length(), CndTokenizer.WORD);
        assertNoMoreTokens();
    }
//
//    @Test
//    public void shouldCreateVendorExtensionToken() {
//        tokenizer = new CndTokenizer(true, true);
//        String content = "{vendor extension}";
//        tokenize(content);
//        assertNextTokenIs(0, content.length(), CndTokenizer.VENDOR_EXTENSION);
//        assertNoMoreTokens();
//    }
//
//    @Test
//    public void shouldNotCreateVendorExtensionTokenIfTokenizerIsNotUsingThem() {
//        tokenizer = new CndTokenizer(true, false);
//        String content = "{vendor extension}";
//        tokenize(content);
//        assertNoMoreTokens();
//    }

    @Test
    public void shouldCreateTokenForEndOfLineComment() {
        String content = "--//this is a comment\n";
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, content.length() - 1, CndTokenizer.COMMENT); // -1 because '\n' is not included
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForEndOfLineCommentThatEndsWithEndOfString() {
        String content = "--//this is a comment";
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, content.length(), CndTokenizer.COMMENT);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForMultiLineComment() {
        String content = "--/*this is a comment*/-";
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, content.length() - 1, CndTokenizer.COMMENT);
        assertNextTokenIs(content.length() - 1, content.length(), CndTokenizer.SYMBOL);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForMultiLineCommentAtEndOfContent() {
        String content = "--/*this is a comment*/";
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, content.length(), CndTokenizer.COMMENT);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForMultiLineCommentWithoutTerminatingCharacters() {
        String content = "--/*this is a comment";
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, content.length(), CndTokenizer.COMMENT);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForMultiLineCommentWithoutAllTerminatingCharacters() {
        String content = "--/*this is a comment*";
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, content.length(), CndTokenizer.COMMENT);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForSingleQuotedString() {
        String content = "--'this is a single-quoted \n string'-";
        assertThat(content.charAt(2), is('\''));
        assertThat(content.charAt(35), is('\''));
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, 36, CndTokenizer.SINGLE_QUOTED_STRING);
        assertNextTokenIs(36, 37, CndTokenizer.SYMBOL);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForSingleQuotedStringWithEscapedSingleQuoteCharacters() {
        String content = "--'this \"is\" a \\'single-quoted\\' \n string'-";
        assertThat(content.charAt(2), is('\''));
        assertThat(content.charAt(41), is('\''));
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, 42, CndTokenizer.SINGLE_QUOTED_STRING);
        assertNextTokenIs(42, 43, CndTokenizer.SYMBOL);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForSingleQuotedStringAtEndOfContent() {
        String content = "--'this is a single-quoted \n string'";
        assertThat(content.charAt(2), is('\''));
        assertThat(content.charAt(35), is('\''));
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, 36, CndTokenizer.SINGLE_QUOTED_STRING);
        assertNoMoreTokens();
    }

    @Test( expected = ParsingException.class )
    public void shouldNotCreateTokenForSingleQuotedStringWithoutClosingQuote() {
        String content = "--'this is a single-quoted \n string";
        tokenize(content);
    }

    @Test
    public void shouldCreateTokenForDoubleQuotedString() {
        String content = "--\"this is a double-quoted \n string\"-";
        assertThat(content.charAt(2), is('"'));
        assertThat(content.charAt(35), is('"'));
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, 36, CndTokenizer.DOUBLE_QUOTED_STRING);
        assertNextTokenIs(36, 37, CndTokenizer.SYMBOL);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForDoubleQuotedStringWithEscapedDoubleQuoteCharacters() {
        String content = "--\"this 'is' a \\\"double-quoted\\\" \n string\"-";
        assertThat(content.charAt(2), is('"'));
        assertThat(content.charAt(41), is('"'));
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, 42, CndTokenizer.DOUBLE_QUOTED_STRING);
        assertNextTokenIs(42, 43, CndTokenizer.SYMBOL);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokenForDoubleQuotedStringAtEndOfContent() {
        String content = "--\"this is a double-quoted \n string\"";
        assertThat(content.charAt(2), is('"'));
        assertThat(content.charAt(35), is('"'));
        tokenize(content);
        assertNextTokenIs(0, 1, CndTokenizer.SYMBOL);
        assertNextTokenIs(1, 2, CndTokenizer.SYMBOL);
        assertNextTokenIs(2, 36, CndTokenizer.DOUBLE_QUOTED_STRING);
        assertNoMoreTokens();
    }

    @Test( expected = ParsingException.class )
    public void shouldNotCreateTokenForDoubleQuotedStringWithoutClosingQuote() {
        String content = "--\"this is a double-quoted \n string";
        tokenize(content);
    }

    @Test
    public void shouldCreateTokensForWordsWithAlphabeticCharacters() {
        String content = "This is a series of words.";
        tokenize(content);
        assertNextTokenIs(0, 4, CndTokenizer.WORD);
        assertNextTokenIs(5, 7, CndTokenizer.WORD);
        assertNextTokenIs(8, 9, CndTokenizer.WORD);
        assertNextTokenIs(10, 16, CndTokenizer.WORD);
        assertNextTokenIs(17, 19, CndTokenizer.WORD);
        assertNextTokenIs(20, 26, CndTokenizer.WORD);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokensForWordsWithNumericCharacters() {
        String content = "1234 4 5353.324";
        tokenize(content);
        assertNextTokenIs(0, 4, CndTokenizer.WORD);
        assertNextTokenIs(5, 6, CndTokenizer.WORD);
        assertNextTokenIs(7, 15, CndTokenizer.WORD);
        assertNoMoreTokens();
    }

    @Test
    public void shouldCreateTokensForWordsWithAlphaNumericCharacters() {
        String content = "123a 5353.324e100";
        tokenize(content);
        assertNextTokenIs(0, 4, CndTokenizer.WORD);
        assertNextTokenIs(5, 17, CndTokenizer.WORD);
        assertNoMoreTokens();
    }
}
