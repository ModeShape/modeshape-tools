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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.Messages;
import org.jboss.tools.modeshape.jcr.Utils;

/**
/**
 * A foundation for basic parsers that tokenizes input content and allows parsers to easily access and use those tokens. A
 * {@link TokenStream} object literally represents the stream of {@link Token} objects that each represent a word, symbol, comment
 * or other lexically-relevant piece of information. This simple framework makes it very easy to create a parser that walks
 * through (or "consumes") the tokens in the order they appear and do something useful with that content (usually creating another
 * representation of the content, such as some domain-specific Abstract Syntax Tree or object model).
 * <p>
 * </p>
 * <h3>The parts</h3>
 * <p>
 * This simple framework consists of a couple of pieces that fit together to do the whole job of parsing input content.
 * </p>
 * <p>
 * The {@link Tokenizer} is responsible for consuming the character-level input content and constructing {@link Token} objects for
 * the different words, symbols, or other meaningful elements contained in the content. Each Token object is a simple object that
 * records the character(s) that make up the token's value, but it does this in a very lightweight and efficient way by pointing
 * to the original character stream. Each token can be assigned a parser-specific integral <i>token type</i> that may make it
 * easier to do quickly figure out later in the process what kind of information each token represents. The general idea is to
 * keep the Tokenizer logic very simple, and very often Tokenizers will merely look for the different kinds of characters (e.g.,
 * symbols, letters, digits, etc.) as well as things like quoted strings and comments. However, Tokenizers are never called by the
 * parser, but instead are always given to the TokenStream that then calls the Tokenizer at the appropriate time.
 * </p>
 * <p>
 * The {@link TokenStream} is supplied the input content, a Tokenizer implementation, and a few options. Its job is to prepare the
 * content for processing, call the Tokenizer implementation to create the series of Token objects, and then provide an interface
 * for walking through and consuming the tokens. This interface makes it possible to discover the value and type of the current
 * token, and consume the current token and move to the next token. Plus, the interface has been designed to make the code that
 * works with the tokens to be as readable as possible.
 * </p>
 * <p>
 * The final component in this framework is the <b>Parser</b>. The parser is really any class that takes as input the content to
 * be parsed and that outputs some meaningful information. The parser will do this by defining the Tokenizer, constructing a
 * TokenStream object, and then using the TokenStream to walk through the sequence of Tokens and produce some meaningful
 * representation of the content. Parsers can create instances of some object model, or they can create a domain-specific Abstract
 * Syntax Tree representation.
 * </p>
 * <p>
 * The benefit of breaking the responsibility along these lines is that the TokenStream implementation is able to encapsulate
 * quite a bit of very tedious and very useful functionality, while still allowing a lot of flexibility as to what makes up the
 * different tokens. It also makes the parser very easy to write and read (and thus maintain), without placing very many
 * restrictions on how that logic is to be defined. Plus, because the TokenStream takes responsibility for tracking the positions
 * of every token (including line and column numbers), it can automatically produce meaningful errors.
 * </p>
 * <h3>Consuming tokens</h3>
 * <p>
 * A parser works with the tokens on the TokenStream using a variety of methods:
 * <ul>
 * <li>The {@link #start()} method must be called before any of the other methods. It performs initialization and tokenizing, and
 * prepares the internal state by finding the first token and setting an internal <i>current token</i> reference.</li>
 * <li>The {@link #hasNext()} method can be called repeatedly to determine if there is another token after the <i>current
 * token</i>. This is often useful when an unknown number of tokens is to be processed, and behaves very similarly to the
 * {@link Iterator#hasNext()} method.</li>
 * <li>The {@link #consume()} method returns the {@link Token#value() value} of the <i>current token</i> and moves the <i>current
 * token</i> pointer to the next available token.</li>
 * <li>The {@link #consume(String)} and {@link #consume(char)} methods look at the <i>current token</i> and ensure the token's
 * {@link Token#value() value} matches the value supplied as a method parameter, or they throw a {@link ParsingException} if the
 * values don't match. The {@link #consume(int)} method works similarly, except that it attempts to match the token's
 * {@link Token#type() type}. And, the {@link #consume(String, String...)} is a convenience method that is equivalent to calling
 * {@link #consume(String)} for each of the arguments.</li>
 * <li>The {@link #canConsume(String)} and {@link #canConsume(char)} methods look at the <i>current token</i> and check whether
 * the token's {@link Token#value() value} matches the value supplied as a method parameter. If there is a match, the method
 * advances the <i>current token</i> reference and returns true. Otherwise, the <i>current token</i> does not match and the method
 * returns false without advancing the <i>current token</i> reference or throwing a ParsingException. Similarly, the
 * {@link #canConsume(int)} method checks the token's {@link Token#type() type} rather than the value, consuming the token and
 * returning true if there is a match, or just returning false if there is no match. The {@link #canConsume(String, String...)}
 * method determines whether all of the supplied values can be consumed in the given order.</li>
 * <li>The {@link #matches(String)} and {@link #matches(char)} methods look at the <i>current token</i> and check whether the
 * token's {@link Token#value() value} matches the value supplied as a method parameter. The method then returns whether there was
 * a match, but does <i>not</i> advance the <i>current token</i> pointer. Similarly, the {@link #matches(int)} method checks the
 * token's {@link Token#type() type} rather than the value. The {@link #matches(String, String...)} method is a convenience method
 * that is equivalent to calling {@link #matches(String)} for each of the arguments, and the {@link #matches(int, int...)} method
 * is a convenience method that is equivalent to calling {@link #matches(int)} for each of the arguments.</li>
 * </ul>
 * <li>The {@link #matchesAnyOf(String, String...)} methods look at the <i>current token</i> and check whether the token's
 * {@link Token#value() value} matches at least one of the values supplied as method parameters. The method then returns whether
 * there was a match, but does <i>not</i> advance the <i>current token</i> pointer. Similarly, the
 * {@link #matchesAnyOf(int, int...)} method checks the token's {@link Token#type() type} rather than the value.</li> </ul>
 * </p>
 * <p>
 * With these methods, it's very easy to create a parser that looks at the current token to decide what to do, and then consume
 * that token, and repeat this process.
 * </p>
 * <h3>Example parser</h3>
 * <p>
 * Here is an example of a very simple parser that parses very simple and limited SQL <code>SELECT</code> and <code>DELETE</code>
 * statements, such as <code>SELECT * FROM Customers</code> or
 * <code>SELECT Name, StreetAddress AS Address, City, Zip FROM Customers</code> or
 * <code>DELETE FROM Customers WHERE Zip=12345</code>:
 *
 * <pre>
 * public class SampleSqlSelectParser {
 *     public List&lt;Statement&gt; parse( String ddl ) {
 *         TokenStream tokens = new TokenStream(ddl, new SqlTokenizer(), false);
 *         List&lt;Statement&gt; statements = new LinkedList&lt;Statement&gt;();
 *         token.start();
 *         while (tokens.hasNext()) {
 *             if (tokens.matches(&quot;SELECT&quot;)) {
 *                 statements.add(parseSelect(tokens));
 *             } else {
 *                 statements.add(parseDelete(tokens));
 *             }
 *         }
 *         return statements;
 *     }
 *
 *     protected Select parseSelect( TokenStream tokens ) throws ParsingException {
 *         tokens.consume(&quot;SELECT&quot;);
 *         List&lt;Column&gt; columns = parseColumns(tokens);
 *         tokens.consume(&quot;FROM&quot;);
 *         String tableName = tokens.consume();
 *         return new Select(tableName, columns);
 *     }
 *
 *     protected List&lt;Column&gt; parseColumns( TokenStream tokens ) throws ParsingException {
 *         List&lt;Column&gt; columns = new LinkedList&lt;Column&gt;();
 *         if (tokens.matches('*')) {
 *             tokens.consume(); // leave the columns empty to signal wildcard
 *         } else {
 *             // Read names until we see a ','
 *             do {
 *                 String columnName = tokens.consume();
 *                 if (tokens.canConsume(&quot;AS&quot;)) {
 *                     String columnAlias = tokens.consume();
 *                     columns.add(new Column(columnName, columnAlias));
 *                 } else {
 *                     columns.add(new Column(columnName, null));
 *                 }
 *             } while (tokens.canConsume(','));
 *         }
 *         return columns;
 *     }
 *
 *     protected Delete parseDelete( TokenStream tokens ) throws ParsingException {
 *         tokens.consume(&quot;DELETE&quot;, &quot;FROM&quot;);
 *         String tableName = tokens.consume();
 *         tokens.consume(&quot;WHERE&quot;);
 *         String lhs = tokens.consume();
 *         tokens.consume('=');
 *         String rhs = tokens.consume();
 *         return new Delete(tableName, new Criteria(lhs, rhs));
 *     }
 *  }
 *  public abstract class Statement { ... }
 *  public class Query extends Statement { ... }
 *  public class Delete extends Statement { ... }
 *  public class Column { ... }
 * </pre>
 *
 * This example shows an idiomatic way of writing a parser that is stateless and thread-safe. The <code>parse(...)</code> method
 * takes the input as a parameter, and returns the domain-specific representation that resulted from the parsing. All other
 * methods are utility methods that simply encapsulate common logic or make the code more readable.
 * </p>
 * <p>
 * In the example, the <code>parse(...)</code> first creates a TokenStream object (using a Tokenizer implementation that is not
 * shown), and then loops as long as there are more tokens to read. As it loops, if the next token is "SELECT", the parser calls
 * the <code>parseSelect(...)</code> method which immediately consumes a "SELECT" token, the names of the columns separated by
 * commas (or a '*' if there all columns are to be selected), a "FROM" token, and the name of the table being queried. The
 * <code>parseSelect(...)</code> method returns a <code>Select</code> object, which then added to the list of statements in the
 * <code>parse(...)</code> method. The parser handles the "DELETE" statements in a similar manner.
 * </p>
 * <h3>Case sensitivity</h3>
 * <p>
 * Very often grammars to not require the case of keywords to match. This can make parsing a challenge, because all combinations
 * of case need to be used. The TokenStream framework provides a very simple solution that requires no more effort than providing
 * a boolean parameter to the constructor.
 * </p>
 * <p>
 * When a <code>false</code> value is provided for the the <code>caseSensitive</code> parameter, the TokenStream performs all
 * matching operations as if each token's value were in uppercase only. This means that the arguments supplied to the
 * <code>match(...)</code>, <code>canConsume(...)</code>, and <code>consume(...)</code> methods should be upper-cased. Note that
 * the <i>actual value</i> of each token remains the <i>actual</i> case as it appears in the input.
 * </p>
 * <p>
 * Of course, when the TokenStream is created with a <code>true</code> value for the <code>caseSensitive</code> parameter, the
 * matching is performed using the <i>actual</i> value as it appears in the input content
 * </p>
 * <h3>Whitespace</h3>
 * <p>
 * Many grammars are independent of lines breaks or whitespace, allowing a lot of flexibility when writing the content. The
 * TokenStream framework makes it very easy to ignore line breaks and whitespace. To do so, the Tokenizer implementation must
 * simply not include the line break character sequences and whitespace in the token ranges. Since none of the tokens contain
 * whitespace, the parser never has to deal with them.
 * </p>
 * <p>
 * Of course, many parsers will require that some whitespace be included. For example, whitespace within a quoted string may be
 * needed by the parser. In this case, the Tokenizer should simply include the whitespace characters in the tokens.
 * </p>
 * <h3>Writing a Tokenizer</h3>
 * <p>
 * Each parser will likely have its own {@link Tokenizer} implementation that contains the parser-specific logic about how to
 * break the content into token objects. Generally, the easiest way to do this is to simply iterate through the character sequence
 * passed into the {@link Tokenizer#tokenize(CharacterStream, Tokens) tokenize(...)} method, and use a switch statement to decide
 * what to do.
 * </p>
 * <p>
 * Here is the code for a very basic Tokenizer implementation that ignores whitespace, line breaks and Java-style (multi-line and
 * end-of-line) comments, while constructing single tokens for each quoted string.
 *
 * <pre>
 *  public class BasicTokenizer implements Tokenizer {
 *      public void tokenize( CharacterStream input,
 *                            Tokens tokens ) throws ParsingException {
 *          while (input.hasNext()) {
 *              char c = input.next();
 *              switch (c) {
 *                  case ' ':
 *                  case '\t':
 *                  case '\n':
 *                  case '\r':
 *                      // Just skip these whitespace characters ...
 *                      break;
 *                  case '-':
 *                  case '(':
 *                  case ')':
 *                  case '{':
 *                  case '}':
 *                  case '*':
 *                  case ',':
 *                  case ';':
 *                  case '+':
 *                  case '%':
 *                  case '?':
 *                  case '$':
 *                  case '[':
 *                  case ']':
 *                  case '!':
 *                  case '<':
 *                  case '>':
 *                  case '|':
 *                  case '=':
 *                  case ':':
 *                      tokens.addToken(input.index(), input.index() + 1, SYMBOL);
 *                      break;
 *                  case '.':
 *                      tokens.addToken(input.index(), input.index() + 1, DECIMAL);
 *                      break;
 *                  case '\"':
 *                  case '\"':
 *                      int startIndex = input.index();
 *                      Position startingPosition = input.position();
 *                      boolean foundClosingQuote = false;
 *                      while (input.hasNext()) {
 *                          c = input.next();
 *                          if (c == '\\' && input.isNext('"')) {
 *                              c = input.next(); // consume the ' character since it is escaped
 *                          } else if (c == '"') {
 *                              foundClosingQuote = true;
 *                              break;
 *                          }
 *                      }
 *                      if (!foundClosingQuote) {
 *                          throw new ParsingException(startingPosition, "No matching closing double quote found");
 *                      }
 *                      int endIndex = input.index() + 1; // beyond last character read
 *                      tokens.addToken(startIndex, endIndex, DOUBLE_QUOTED_STRING);
 *                      break;
 *                  case '\'':
 *                      startIndex = input.index();
 *                      startingPosition = input.position();
 *                      foundClosingQuote = false;
 *                      while (input.hasNext()) {
 *                          c = input.next();
 *                          if (c == '\\' && input.isNext('\'')) {
 *                              c = input.next(); // consume the ' character since it is escaped
 *                          } else if (c == '\'') {
 *                              foundClosingQuote = true;
 *                              break;
 *                          }
 *                      }
 *                      if (!foundClosingQuote) {
 *                          throw new ParsingException(startingPosition, "No matching closing single quote found");
 *                      }
 *                      endIndex = input.index() + 1; // beyond last character read
 *                      tokens.addToken(startIndex, endIndex, SINGLE_QUOTED_STRING);
 *                      break;
 *                  case '/':
 *                      startIndex = input.index();
 *                      if (input.isNext('/')) {
 *                          // End-of-line comment ...
 *                          boolean foundLineTerminator = false;
 *                          while (input.hasNext()) {
 *                              c = input.next();
 *                              if (c == '\n' || c == '\r') {
 *                                  foundLineTerminator = true;
 *                                  break;
 *                              }
 *                          }
 *                          endIndex = input.index(); // the token won't include the '\n' or '\r' character(s)
 *                          if (!foundLineTerminator) ++endIndex; // must point beyond last char
 *                          if (c == '\r' && input.isNext('\n')) input.next();
 *                          if (useComments) {
 *                              tokens.addToken(startIndex, endIndex, COMMENT);
 *                          }
 *                      } else if (input.isNext('*')) {
 *                          // Multi-line comment ...
 *                          while (input.hasNext() && !input.isNext('*', '/')) {
 *                              c = input.next();
 *                          }
 *                          if (input.hasNext()) input.next(); // consume the '*'
 *                          if (input.hasNext()) input.next(); // consume the '/'
 *                          if (useComments) {
 *                              endIndex = input.index() + 1; // the token will include the '/' and '*' characters
 *                              tokens.addToken(startIndex, endIndex, COMMENT);
 *                          }
 *                      } else {
 *                          // just a regular slash ...
 *                          tokens.addToken(startIndex, startIndex + 1, SYMBOL);
 *                      }
 *                      break;
 *                  default:
 *                      startIndex = input.index();
 *                      // Read until another whitespace/symbol/decimal/slash is found
 *                      while (input.hasNext() && !(input.isNextWhitespace() || input.isNextAnyOf("/.-(){}*,;+%?$[]!<>|=:"))) {
 *                          c = input.next();
 *                      }
 *                      endIndex = input.index() + 1; // beyond last character that was included
 *                      tokens.addToken(startIndex, endIndex, WORD);
 *              }
 *          }
 *      }
 *  }
 * </pre>
 * Tokenizers with exactly this behavior can actually be created using the {@link #basicTokenizer(boolean)} method.  So while this very
 * basic implementation is not meant to be used in all situations, it may be useful in some situations.
 * </p>
 */
public class TokenStream {

    /**
     * A constant that can be used with the {@link #matches(String)}, {@link #matches(String, String...)},
     * {@link #consume(String)}, {@link #consume(String, String...)}, {@link #canConsume(String)} and
     * {@link #canConsume(String, String...)} methods to signal that any value is allowed to be matched.
     * <p>
     * Note that this exact instance must be used; an equivalent string will not work.
     * </p>
     */
    public static final String ANY_VALUE = "any value";
    /**
     * A constant that can be used with the {@link #matches(int)}, {@link #matches(int, int...)}, {@link #consume(int)}, and
     * {@link #canConsume(int)} methods to signal that any token type is allowed to be matched.
     */
    public static final int ANY_TYPE = Integer.MIN_VALUE;

    /**
     * Obtain a basic {@link Tokenizer} implementation that ignores whitespace but includes tokens for individual symbols, the
     * period ('.'), single-quoted strings, double-quoted strings, whitespace-delimited words, and optionally comments.
     * <p>
     * Note that the resulting Tokenizer may not be appropriate in many situations, but is provided merely as a convenience for
     * those situations that happen to be able to use it.
     * </p>
     *
     * @param includeComments true if the comments should be retained and be included in the token stream, or false if comments
     *        should be stripped and not included in the token stream
     * @return the tokenizer; never null
     */
    public static BasicTokenizer basicTokenizer( boolean includeComments ) {
        return new BasicTokenizer(includeComments);
    }

    /**
     * Utility method to generate a highlighted fragment of a particular point in the stream.
     *
     * @param content the content from which the fragment should be taken; may not be null
     * @param indexOfProblem the index of the problem point that should be highlighted; must be a valid index in the content
     * @param charactersToIncludeBeforeAndAfter the maximum number of characters before and after the problem point to include in
     *        the fragment
     * @param highlightText the text that should be included in the fragment at the problem point to highlight the location, or an
     *        empty string if there should be no highlighting
     * @return the highlighted fragment; never null
     */
    static String generateFragment( String content,
                                    int indexOfProblem,
                                    int charactersToIncludeBeforeAndAfter,
                                    String highlightText ) {
        assert content != null;
        assert indexOfProblem < content.length();
        // Find the substring that immediately precedes the current position ...
        final int beforeStart = Math.max(0, indexOfProblem - charactersToIncludeBeforeAndAfter);
        final String before = content.substring(beforeStart, indexOfProblem);

        // Find the substring that immediately follows the current position ...
        final int afterEnd = Math.min(indexOfProblem + charactersToIncludeBeforeAndAfter, content.length());
        final String after = content.substring(indexOfProblem, afterEnd);

        return before + (highlightText != null ? highlightText : "") + after;
    }

    /**
     */
    protected final String inputString;
    /**
     */
    protected final String inputUppercased;
    private final char[] inputContent;
    private final boolean caseSensitive;
    private final Tokenizer tokenizer;
    private List<Token> tokens;
    /**
     * This class navigates the Token objects using this iterator. However, because it very often needs to access the
     * "current token" in the "consume(...)" and "canConsume(...)" and "matches(...)" methods, the class caches a "current token"
     * and makes this iterator point to the 2nd token.
     *
     * <pre>
     *     T1     T2    T3    T4    T5
     *         &circ;   &circ;  &circ;
     *         |   |  |
     *         |   |  +- The position of the tokenIterator, where tokenIterator.hasNext() will return T3
     *         |   +---- The token referenced by currentToken
     *         +-------- The logical position of the TokenStream object, where the &quot;consume()&quot; would return T2
     * </pre>
     */
    private ListIterator<Token> tokenIterator;

    private Token currentToken;

    private boolean completed;

    /**
     * @param content
     * @param tokenizer
     * @param caseSensitive
     */
    public TokenStream( String content,
                        Tokenizer tokenizer,
                        boolean caseSensitive ) {
        Utils.verifyIsNotNull(content, "content");
        Utils.verifyIsNotNull(tokenizer, "tokenizer");
        this.inputString = content;
        this.inputContent = content.toCharArray();
        this.caseSensitive = caseSensitive;
        this.inputUppercased = caseSensitive ? this.inputString : content.toUpperCase();
        this.tokenizer = tokenizer;
    }

    /**
     * Attempt to consume this current token if it matches the expected value, and return whether this method was indeed able to
     * consume the token.
     *
     * @param expected the expected value of the current token token
     * @return true if the current token did match and was consumed, or false if the current token did not match and therefore was
     *         not consumed
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsume( char expected ) throws IllegalStateException {
        if (!matches(expected)) {
            return false;
        }
        moveToNextToken();
        return true;
    }

    /**
     * Attempt to consume this current token if it matches the expected token type, and return whether this method was indeed able
     * to consume the token.
     * <p>
     * The {@link #ANY_TYPE ANY_TYPE} constant can be used in the expected type as a wildcard.
     * </p>
     *
     * @param expectedType the expected token type of the current token
     * @return true if the current token did match and was consumed, or false if the current token did not match and therefore was
     *         not consumed
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsume( int expectedType ) throws IllegalStateException {
        if (!matches(expectedType)) {
            return false;
        }
        moveToNextToken();
        return true;
    }

    /**
     * Attempt to consume this current token and the next tokens if and only if they match the expected values, and return whether
     * this method was indeed able to consume all of the supplied tokens.
     * <p>
     * This is <i>not</i> the same as calling {@link #canConsume(String)} for each of the supplied arguments, since this method
     * ensures that <i>all</i> of the supplied values can be consumed.
     * </p>
     * <p>
     * This method <i>is</i> equivalent to calling the following:
     *
     * <pre>
     *
     * if (tokens.matches(currentExpected, expectedForNextTokens)) {
     *     tokens.consume(currentExpected, expectedForNextTokens);
     * }
     *
     * </pre>
     *
     * </p>
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param nextTokens the expected values of the next tokens
     * @return true if the current token did match and was consumed, or false if the current token did not match and therefore was
     *         not consumed
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsume( Iterable<String> nextTokens ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        Token token = null;
        for (final String nextExpected : nextTokens) {
            if (!iter.hasNext()) {
                return false;
            }
            token = iter.next();
            if (nextExpected == ANY_VALUE) {
                continue;
            }
            if (!token.matches(nextExpected)) {
                return false;
            }
        }
        this.tokenIterator = iter;
        this.currentToken = this.tokenIterator.hasNext() ? this.tokenIterator.next() : null;
        this.completed = this.currentToken == null;
        return true;
    }

    /**
     * Attempt to consume this current token if it matches the expected value, and return whether this method was indeed able to
     * consume the token.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected value as a wildcard.
     * </p>
     *
     * @param expected the expected value of the current token token
     * @return true if the current token did match and was consumed, or false if the current token did not match and therefore was
     *         not consumed
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsume( String expected ) throws IllegalStateException {
        if (!matches(expected)) {
            return false;
        }
        moveToNextToken();
        return true;
    }

    /**
     * Attempt to consume this current token and the next tokens if and only if they match the expected values, and return whether
     * this method was indeed able to consume all of the supplied tokens.
     * <p>
     * This is <i>not</i> the same as calling {@link #canConsume(String)} for each of the supplied arguments, since this method
     * ensures that <i>all</i> of the supplied values can be consumed.
     * </p>
     * <p>
     * This method <i>is</i> equivalent to calling the following:
     *
     * <pre>
     *
     * if (tokens.matches(currentExpected, expectedForNextTokens)) {
     *     tokens.consume(currentExpected, expectedForNextTokens);
     * }
     *
     * </pre>
     *
     * </p>
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param currentExpected the expected value of the current token
     * @param expectedForNextTokens the expected values fo the following tokens
     * @return true if the current token did match and was consumed, or false if the current token did not match and therefore was
     *         not consumed
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsume( String currentExpected,
                               String... expectedForNextTokens ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        if (!iter.hasNext()) {
            return false;
        }
        Token token = iter.next();
        if (currentExpected != ANY_VALUE && !token.matches(currentExpected)) {
            return false;
        }
        for (final String nextExpected : expectedForNextTokens) {
            if (!iter.hasNext()) {
                return false;
            }
            token = iter.next();
            if (nextExpected == ANY_VALUE) {
                continue;
            }
            if (!token.matches(nextExpected)) {
                return false;
            }
        }
        this.tokenIterator = iter;
        this.currentToken = this.tokenIterator.hasNext() ? this.tokenIterator.next() : null;
        this.completed = this.currentToken == null;
        return true;
    }

    /**
     * Attempt to consume this current token and the next tokens if and only if they match the expected values, and return whether
     * this method was indeed able to consume all of the supplied tokens.
     * <p>
     * This is <i>not</i> the same as calling {@link #canConsume(String)} for each of the supplied arguments, since this method
     * ensures that <i>all</i> of the supplied values can be consumed.
     * </p>
     * <p>
     * This method <i>is</i> equivalent to calling the following:
     *
     * <pre>
     *
     * if (tokens.matches(currentExpected, expectedForNextTokens)) {
     *     tokens.consume(currentExpected, expectedForNextTokens);
     * }
     *
     * </pre>
     *
     * </p>
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param nextTokens the expected values of the next tokens
     * @return true if the current token did match and was consumed, or false if the current token did not match and therefore was
     *         not consumed
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsume( String[] nextTokens ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        Token token = null;
        for (final String nextExpected : nextTokens) {
            if (!iter.hasNext()) {
                return false;
            }
            token = iter.next();
            if (nextExpected == ANY_VALUE) {
                continue;
            }
            if (!token.matches(nextExpected)) {
                return false;
            }
        }
        this.tokenIterator = iter;
        this.currentToken = this.tokenIterator.hasNext() ? this.tokenIterator.next() : null;
        this.completed = this.currentToken == null;
        return true;
    }

    /**
     * Attempt to consume the next token if it matches one of the supplied types.
     *
     * @param firstTypeOption the first option for the type of the current token
     * @param additionalTypeOptions the additional options for the type of the current token
     * @return true if the current token's type matched one of the supplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsumeAnyOf( int firstTypeOption,
                                    int... additionalTypeOptions ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        if (canConsume(firstTypeOption)) {
            return true;
        }
        for (final int nextTypeOption : additionalTypeOptions) {
            if (canConsume(nextTypeOption)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempt to consume the next token if it matches one of the supplied types.
     *
     * @param typeOptions the options for the type of the current token
     * @return true if the current token's type matched one of the supplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsumeAnyOf( int[] typeOptions ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        for (final int nextTypeOption : typeOptions) {
            if (canConsume(nextTypeOption)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempt to consume the next token if it matches one of the supplied values.
     *
     * @param options the options for the value of the current token
     * @return true if the current token's value did match one of the suplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsumeAnyOf( Iterable<String> options ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        for (final String option : options) {
            if (canConsume(option)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempt to consume the next token if it matches one of the supplied values.
     *
     * @param firstOption the first option for the value of the current token
     * @param additionalOptions the additional options for the value of the current token
     * @return true if the current token's value did match one of the suplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsumeAnyOf( String firstOption,
                                    String... additionalOptions ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        if (canConsume(firstOption)) {
            return true;
        }
        for (final String nextOption : additionalOptions) {
            if (canConsume(nextOption)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempt to consume the next token if it matches one of the supplied values.
     *
     * @param options the options for the value of the current token
     * @return true if the current token's value did match one of the suplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean canConsumeAnyOf( String[] options ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        for (final String option : options) {
            if (canConsume(option)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the value of this token and move to the next token.
     *
     * @return the value of the current token
     * @throws ParsingException if there is no such token to consume
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public String consume() throws ParsingException, IllegalStateException {
        if (this.completed) {
            throwNoMoreContent();
        }
        // Get the value from the current token ...
        final String result = currentToken().value();
        moveToNextToken();
        return result;
    }

    /**
     * Attempt to consume this current token as long as it matches the expected character, or throw an exception if the token does
     * not match.
     *
     * @param expected the expected character of the current token
     * @throws ParsingException if the current token doesn't match the supplied value
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public void consume( char expected ) throws ParsingException, IllegalStateException {
        if (this.completed) {
            final String msg = NLS.bind(Messages.noMoreContentButWasExpectingCharacter, expected);
            throw new ParsingException(this.tokens.get(this.tokens.size() - 1).position(), msg);
        }
        // Get the value from the current token ...
        if (!currentToken().matches(expected)) {
            final String found = currentToken().value();
            final Position pos = currentToken().position();
            final String fragment = generateFragment();
            final String msg = NLS.bind(Messages.unexpectedCharacter,
                                        new Object[] {expected, found, pos.getLine(), pos.getColumn(), fragment});
            throw new ParsingException(pos, msg);
        }
        moveToNextToken();
    }

    /**
     * Attempt to consume this current token as long as it matches the expected character, or throw an exception if the token does
     * not match.
     * <p>
     * The {@link #ANY_TYPE ANY_TYPE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param expectedType the expected token type of the current token
     * @throws ParsingException if the current token doesn't match the supplied value
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public void consume( int expectedType ) throws ParsingException, IllegalStateException {
        if (this.completed) {
            final String msg = NLS.bind(Messages.noMoreContentButWasExpectingTokenType, expectedType);
            throw new ParsingException(this.tokens.get(this.tokens.size() - 1).position(), msg);
        }
        // Get the value from the current token ...
        if (expectedType != ANY_TYPE && currentToken().type() != expectedType) {
            final String found = currentToken().value();
            final Position pos = currentToken().position();
            final String fragment = generateFragment();
            final String msg = NLS.bind(Messages.unexpectedTokenType,
                                        new Object[] {expectedType, found, pos.getLine(), pos.getColumn(), fragment});
            throw new ParsingException(pos, msg);
        }
        moveToNextToken();
    }

    /**
     * Attempt to consume this current token as the next tokens as long as they match the expected values, or throw an exception
     * if the token does not match.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param nextTokens the expected values for the next tokens
     * @throws ParsingException if the current token doesn't match the supplied value
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public void consume( Iterable<String> nextTokens ) throws ParsingException, IllegalStateException {
        for (final String nextExpected : nextTokens) {
            consume(nextExpected);
        }
    }

    /**
     * Attempt to consume this current token as long as it matches the expected value, or throw an exception if the token does not
     * match.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param expected the expected value of the current token
     * @throws ParsingException if the current token doesn't match the supplied value
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public void consume( String expected ) throws ParsingException, IllegalStateException {
        if (this.completed) {
            final String msg = NLS.bind(Messages.noMoreContentButWasExpectingToken, expected);
            throw new ParsingException(this.tokens.get(this.tokens.size() - 1).position(), msg);
        }
        // Get the value from the current token ...
        if (expected != ANY_VALUE && !currentToken().matches(expected)) {
            final String found = currentToken().value();
            final Position pos = currentToken().position();
            final String fragment = generateFragment();
            final String msg = NLS.bind(Messages.unexpectedToken, new Object[] {expected, found, pos.getLine(), pos.getColumn(),
                fragment});
            throw new ParsingException(pos, msg);
        }
        moveToNextToken();
    }

    /**
     * Attempt to consume this current token as the next tokens as long as they match the expected values, or throw an exception
     * if the token does not match.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param expected the expected value of the current token
     * @param expectedForNextTokens the expected values fo the following tokens
     * @throws ParsingException if the current token doesn't match the supplied value
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public void consume( String expected,
                         String... expectedForNextTokens ) throws ParsingException, IllegalStateException {
        consume(expected);
        for (final String nextExpected : expectedForNextTokens) {
            consume(nextExpected);
        }
    }

    /**
     * Attempt to consume this current token as the next tokens as long as they match the expected values, or throw an exception
     * if the token does not match.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param nextTokens the expected values for the next tokens
     * @throws ParsingException if the current token doesn't match the supplied value
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public void consume( String[] nextTokens ) throws ParsingException, IllegalStateException {
        for (final String nextExpected : nextTokens) {
            consume(nextExpected);
        }
    }

    /**
     * Convert the value of this token to an integer, return it, and move to the next token.
     *
     * @return the current token's value, converted to an integer
     * @throws ParsingException if there is no such token to consume, or if the token cannot be converted to an integer
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean consumeBoolean() throws ParsingException, IllegalStateException {
        if (this.completed) {
            throwNoMoreContent();
        }
        // Get the value from the current token ...
        final String value = currentToken().value();
        try {
            final boolean result = Boolean.parseBoolean(value);
            moveToNextToken();
            return result;
        } catch (final NumberFormatException e) {
            final Position position = currentToken().position();
            final String msg = NLS.bind(Messages.expectingValidBooleanAtLineAndColumn, new Object[] {value, position.getLine(),
                position.getColumn()});
            throw new ParsingException(position, msg);
        }
    }

    /**
     * Convert the value of this token to an integer, return it, and move to the next token.
     *
     * @return the current token's value, converted to an integer
     * @throws ParsingException if there is no such token to consume, or if the token cannot be converted to an integer
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public int consumeInteger() throws ParsingException, IllegalStateException {
        if (this.completed) {
            throwNoMoreContent();
        }
        // Get the value from the current token ...
        final String value = currentToken().value();
        try {
            final int result = Integer.parseInt(value);
            moveToNextToken();
            return result;
        } catch (final NumberFormatException e) {
            final Position position = currentToken().position();
            final String msg = NLS.bind(Messages.expectingValidIntegerAtLineAndColumn, new Object[] {value, position.getLine(),
                position.getColumn()});
            throw new ParsingException(position, msg);
        }
    }

    /**
     * Convert the value of this token to a long, return it, and move to the next token.
     *
     * @return the current token's value, converted to an integer
     * @throws ParsingException if there is no such token to consume, or if the token cannot be converted to a long
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public long consumeLong() throws ParsingException, IllegalStateException {
        if (this.completed) {
            throwNoMoreContent();
        }
        // Get the value from the current token ...
        final String value = currentToken().value();
        try {
            final long result = Long.parseLong(value);
            moveToNextToken();
            return result;
        } catch (final NumberFormatException e) {
            final Position position = currentToken().position();
            final String msg = NLS.bind(Messages.expectingValidLongAtLineAndColumn, new Object[] {value, position.getLine(),
                position.getColumn()});
            throw new ParsingException(position, msg);
        }
    }

    /**
     * Get the current token.
     *
     * @return the current token; never null
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     * @throws NoSuchElementException if there are no more tokens
     */
    final Token currentToken() throws IllegalStateException, NoSuchElementException {
        if (this.currentToken == null) {
            if (this.completed) {
                throw new NoSuchElementException(Messages.noMoreContent);
            }
            throw new IllegalStateException(Messages.startMethodMustBeCalledBeforeConsumingOrMatching);
        }
        assert this.currentToken != null;
        return this.currentToken;
    }

    String generateFragment() {
        // Find the current position ...
        assert this.currentToken != null;
        final int startIndex = this.currentToken.startIndex();
        return generateFragment(this.inputString, startIndex, 20, " ===>> ");
    }

    /**
     * Gets the content string starting at the first position (inclusive) and continuing up to the end position (exclusive).
     *
     * @param starting the position marking the beginning of the desired content string.
     * @param end the position located directly after the returned content string; can be null, which means end of content
     * @return the content string; never null
     */
    public String getContentBetween( Position starting,
                                     Position end ) {
        Utils.verifyIsNotNull(starting, "starting");

        final int startIndex = starting.getIndexInContent();
        int endIndex = this.inputString.length();
        if (end != null) {
            endIndex = end.getIndexInContent();
        }

        if (startIndex >= endIndex) {
            throw new IllegalArgumentException(NLS.bind(Messages.endPositionMustBeGreaterThanStartingPosition,
                                                        endIndex,
                                                        startIndex));
        }

        return this.inputString.substring(startIndex, endIndex);
    }

    /**
     * Determine if this stream has another token to be consumed.
     *
     * @return true if there is another token ready for consumption, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean hasNext() {
        if (this.tokenIterator == null) {
            throw new IllegalStateException(Messages.startMethodMustBeCalledBeforeNext);
        }
        return !this.completed;
    }

    /**
     * Method to allow subclasses to preprocess the set of tokens and return the correct tokens to use. The default behavior is to
     * simply return the supplied tokens.
     *
     * @param tokens
     * @return list of tokens.
     */
    protected List<Token> initializeTokens( List<Token> tokens ) {
        return tokens;
    }

    /**
     * Determine if the current token matches the expected value.
     *
     * @param expected the expected value of the current token token
     * @return true if the current token did match, or false if the current token did not match
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matches( char expected ) throws IllegalStateException {
        return !this.completed && currentToken().matches(expected);
    }

    /**
     * Determine if the current token matches the expected token type.
     *
     * @param expectedType the expected token type of the current token
     * @return true if the current token did match, or false if the current token did not match
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matches( int expectedType ) throws IllegalStateException {
        return !this.completed && currentToken().matches(expectedType);
    }

    /**
     * Determine if the next few tokens have the supplied types.
     * <p>
     * The {@link #ANY_TYPE ANY_TYPE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param currentExpectedType the expected type of the current token
     * @param expectedTypeForNextTokens the expected type for the following tokens
     * @return true if the tokens did match, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matches( int currentExpectedType,
                            int... expectedTypeForNextTokens ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        if (!iter.hasNext()) {
            return false;
        }
        Token token = iter.next();
        if (currentExpectedType != ANY_TYPE && currentToken().type() != currentExpectedType) {
            return false;
        }
        for (final int nextExpectedType : expectedTypeForNextTokens) {
            if (!iter.hasNext()) {
                return false;
            }
            token = iter.next();
            if (nextExpectedType == ANY_TYPE) {
                continue;
            }
            if (token.type() != nextExpectedType) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if the next few tokens have the supplied types.
     * <p>
     * The {@link #ANY_TYPE ANY_TYPE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param typesForNextTokens the expected type for each of the next tokens
     * @return true if the tokens did match, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matches( int[] typesForNextTokens ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        Token token = null;
        for (final int nextExpectedType : typesForNextTokens) {
            if (!iter.hasNext()) {
                return false;
            }
            token = iter.next();
            if (nextExpectedType == ANY_TYPE) {
                continue;
            }
            if (!token.matches(nextExpectedType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if the next few tokens match the expected values.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param nextTokens the expected value of the next tokens
     * @return true if the tokens did match, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matches( Iterable<String> nextTokens ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        Token token = null;
        for (final String nextExpected : nextTokens) {
            if (!iter.hasNext()) {
                return false;
            }
            token = iter.next();
            if (nextExpected == ANY_VALUE) {
                continue;
            }
            if (!token.matches(nextExpected)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if the current token matches the expected value.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used as a wildcard.
     * </p>
     *
     * @param expected the expected value of the current token token
     * @return true if the current token did match, or false if the current token did not match
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matches( String expected ) throws IllegalStateException {
        return !this.completed && (expected == ANY_VALUE || currentToken().matches(expected));
    }

    /**
     * Determine if the next few tokens match the expected values.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param currentExpected the expected value of the current token
     * @param expectedForNextTokens the expected values for the following tokens
     * @return true if the tokens did match, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matches( String currentExpected,
                            String... expectedForNextTokens ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        if (!iter.hasNext()) {
            return false;
        }
        Token token = iter.next();
        if (currentExpected != ANY_VALUE && !token.matches(currentExpected)) {
            return false;
        }
        for (final String nextExpected : expectedForNextTokens) {
            if (!iter.hasNext()) {
                return false;
            }
            token = iter.next();
            if (nextExpected == ANY_VALUE) {
                continue;
            }
            if (!token.matches(nextExpected)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if the next few tokens match the expected values.
     * <p>
     * The {@link #ANY_VALUE ANY_VALUE} constant can be used in the expected values as a wildcard.
     * </p>
     *
     * @param nextTokens the expected value of the next tokens
     * @return true if the tokens did match, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matches( String[] nextTokens ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        Token token = null;
        for (final String nextExpected : nextTokens) {
            if (!iter.hasNext()) {
                return false;
            }
            token = iter.next();
            if (nextExpected == ANY_VALUE) {
                continue;
            }
            if (!token.matches(nextExpected)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if the next token have one of the supplied types.
     *
     * @param firstTypeOption the first option for the type of the current token
     * @param additionalTypeOptions the additional options for the type of the current token
     * @return true if the current token's type matched one of the supplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matchesAnyOf( int firstTypeOption,
                                 int... additionalTypeOptions ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final int currentType = currentToken().type();
        if (currentType == firstTypeOption) {
            return true;
        }
        for (final int nextTypeOption : additionalTypeOptions) {
            if (currentType == nextTypeOption) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the next token have one of the supplied types.
     *
     * @param typeOptions the options for the type of the current token
     * @return true if the current token's type matched one of the supplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matchesAnyOf( int[] typeOptions ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final int currentType = currentToken().type();
        for (final int nextTypeOption : typeOptions) {
            if (currentType == nextTypeOption) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the next token matches one of the supplied values.
     *
     * @param options the options for the value of the current token
     * @return true if the current token's value did match one of the suplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matchesAnyOf( Iterable<String> options ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final Token current = currentToken();
        for (final String option : options) {
            if (current.matches(option)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the next token matches one of the supplied values.
     *
     * @param firstOption the first option for the value of the current token
     * @param additionalOptions the additional options for the value of the current token
     * @return true if the current token's value did match one of the suplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matchesAnyOf( String firstOption,
                                 String... additionalOptions ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final Token current = currentToken();
        if (current.matches(firstOption)) {
            return true;
        }
        for (final String nextOption : additionalOptions) {
            if (current.matches(nextOption)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the next token matches one of the supplied values.
     *
     * @param options the options for the value of the current token
     * @return true if the current token's value did match one of the suplied options, or false otherwise
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     */
    public boolean matchesAnyOf( String[] options ) throws IllegalStateException {
        if (this.completed) {
            return false;
        }
        final Token current = currentToken();
        for (final String option : options) {
            if (current.matches(option)) {
                return true;
            }
        }
        return false;
    }

    private void moveToNextToken() {
        // And move the currentToken to the next token ...
        if (!this.tokenIterator.hasNext()) {
            this.completed = true;
            this.currentToken = null;
        } else {
            this.currentToken = this.tokenIterator.next();
        }
    }

    /**
     * Get the position of the next (or current) token.
     *
     * @return the current token's position; never null
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     * @throws NoSuchElementException if there is no previous token
     */
    public Position nextPosition() {
        return currentToken().position();
    }

    /**
     * Get the position of the previous token.
     *
     * @return the previous token's position; never null
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     * @throws NoSuchElementException if there is no previous token
     */
    public Position previousPosition() {
        return previousToken().position();
    }

    /**
     * Get the previous token. This does not modify the state.
     *
     * @return the previous token; never null
     * @throws IllegalStateException if this method was called before the stream was {@link #start() started}
     * @throws NoSuchElementException if there is no previous token
     */
    final Token previousToken() throws IllegalStateException, NoSuchElementException {
        if (this.currentToken == null) {
            if (this.completed) {
                if (this.tokens.isEmpty()) {
                    throw new NoSuchElementException(Messages.noMoreContent);
                }
                return this.tokens.get(this.tokens.size() - 1);
            }
            throw new IllegalStateException(Messages.startMethodMustBeCalledBeforeConsumingOrMatching);
        }
        if (this.tokenIterator.previousIndex() == 0) {
            throw new NoSuchElementException(Messages.noMoreContent);
        }
        return this.tokens.get(this.tokenIterator.previousIndex() - 1);
    }

    /**
     * Method to allow tokens to be re-used from the start without re-tokenizing content.
     */
    public void rewind() {
        // recreate the iterator ...
        this.tokenIterator = this.tokens.listIterator();
        this.completed = false;
        this.currentToken = null;
        moveToNextToken();
    }

    /**
     * Begin the token stream, including (if required) the tokenization of the input content.
     *
     * @return this object for easy method chaining; never null
     * @throws ParsingException if an error occurs during tokenization of the content
     */
    public TokenStream start() throws ParsingException {
        // Create the tokens ...
        if (this.tokens == null) {
            final TokenFactory tokenFactory = this.caseSensitive ? new CaseSensitiveTokenFactory() : new CaseInsensitiveTokenFactory();
            final CharacterStream characterStream = new CharacterArrayStream(this.inputContent);
            this.tokenizer.tokenize(characterStream, tokenFactory);
            this.tokens = initializeTokens(tokenFactory.getTokens());
        }

        // Create the iterator ...
        this.tokenIterator = this.tokens.listIterator();
        moveToNextToken();
        return this;
    }

    /**
     * @throws ParsingException
     */
    protected void throwNoMoreContent() throws ParsingException {
        final String msg = Messages.noMoreContent;
        final Position pos = this.tokens.isEmpty() ? new Position(-1, 1, 0) : this.tokens.get(this.tokens.size() - 1).position();
        throw new ParsingException(pos, msg);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final ListIterator<Token> iter = this.tokens.listIterator(this.tokenIterator.previousIndex());
        final StringBuilder sb = new StringBuilder();
        if (iter.hasNext()) {
            sb.append(iter.next());
            int count = 1;
            while (iter.hasNext()) {
                if (count > 20) {
                    sb.append(" ...");
                    break;
                }
                sb.append("  ");
                ++count;
                sb.append(iter.next());
            }
        }
        return sb.toString();
    }

    /**
     * A basic {@link Tokenizer} implementation that ignores whitespace but includes tokens for individual symbols, the period
     * ('.'), single-quoted strings, double-quoted strings, whitespace-delimited words, and optionally comments.
     * <p>
     * Note this Tokenizer may not be appropriate in many situations, but is provided merely as a convenience for those situations
     * that happen to be able to use it.
     * </p>
     */
    public static class BasicTokenizer implements Tokenizer {
        /**
         * The {@link Token#type() token type} for tokens that represent an unquoted string containing a character sequence made
         * up of non-whitespace and non-symbol characters.
         */
        public static final int WORD = 1;
        /**
         * The {@link Token#type() token type} for tokens that consist of an individual "symbol" character. The set of characters
         * includes: <code>-(){}*,;+%?$[]!<>|=:</code>
         */
        public static final int SYMBOL = 2;
        /**
         * The {@link Token#type() token type} for tokens that consist of an individual '.' character.
         */
        public static final int DECIMAL = 4;
        /**
         * The {@link Token#type() token type} for tokens that consist of all the characters within single-quotes. Single quote
         * characters are included if they are preceded (escaped) by a '\' character.
         */
        public static final int SINGLE_QUOTED_STRING = 8;
        /**
         * The {@link Token#type() token type} for tokens that consist of all the characters within double-quotes. Double quote
         * characters are included if they are preceded (escaped) by a '\' character.
         */
        public static final int DOUBLE_QUOTED_STRING = 16;
        /**
         * The {@link Token#type() token type} for tokens that consist of all the characters between "/*" and "&#42;/" or between
         * "//" and the next line terminator (e.g., '\n', '\r' or "\r\n").
         */
        public static final int COMMENT = 32;

        private final boolean useComments;

        /**
         * @param useComments
         */
        protected BasicTokenizer( boolean useComments ) {
            this.useComments = useComments;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Tokenizer#tokenize(org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream,
         *      org.jboss.tools.modeshape.jcr.text.TokenStream.Tokens)
         */
        @Override
        public void tokenize( CharacterStream input,
                              Tokens tokens ) throws ParsingException {
            while (input.hasNext()) {
                char c = input.next();
                switch (c) {
                    case ' ':
                    case '\t':
                    case '\n':
                    case '\r':
                        // Just skip these whitespace characters ...
                        break;
                    case '-':
                    case '(':
                    case ')':
                    case '{':
                    case '}':
                    case '*':
                    case ',':
                    case ';':
                    case '+':
                    case '%':
                    case '?':
                    case '$':
                    case '[':
                    case ']':
                    case '!':
                    case '<':
                    case '>':
                    case '|':
                    case '=':
                    case ':':
                        tokens.addToken(input.position(input.index()), input.index(), input.index() + 1, SYMBOL);
                        break;
                    case '.':
                        tokens.addToken(input.position(input.index()), input.index(), input.index() + 1, DECIMAL);
                        break;
                    case '\"':
                        int startIndex = input.index();
                        Position startingPosition = input.position(startIndex);
                        boolean foundClosingQuote = false;
                        while (input.hasNext()) {
                            c = input.next();
                            if (c == '\\' && input.isNext('"')) {
                                c = input.next(); // consume the ' character since it is escaped
                            } else if (c == '"') {
                                foundClosingQuote = true;
                                break;
                            }
                        }
                        if (!foundClosingQuote) {
                            final String msg = NLS.bind(Messages.noMatchingDoubleQuoteFound,
                                                        startingPosition.getLine(),
                                                        startingPosition.getColumn());
                            throw new ParsingException(startingPosition, msg);
                        }
                        int endIndex = input.index() + 1; // beyond last character read
                        tokens.addToken(startingPosition, startIndex, endIndex, DOUBLE_QUOTED_STRING);
                        break;
                    case '\'':
                        startIndex = input.index();
                        startingPosition = input.position(startIndex);
                        foundClosingQuote = false;
                        while (input.hasNext()) {
                            c = input.next();
                            if (c == '\\' && input.isNext('\'')) {
                                c = input.next(); // consume the ' character since it is escaped
                            } else if (c == '\'') {
                                foundClosingQuote = true;
                                break;
                            }
                        }
                        if (!foundClosingQuote) {
                            final String msg = NLS.bind(Messages.noMatchingSingleQuoteFound,
                                                        startingPosition.getLine(),
                                                        startingPosition.getColumn());
                            throw new ParsingException(startingPosition, msg);
                        }
                        endIndex = input.index() + 1; // beyond last character read
                        tokens.addToken(startingPosition, startIndex, endIndex, SINGLE_QUOTED_STRING);
                        break;
                    case '/':
                        startIndex = input.index();
                        startingPosition = input.position(startIndex);
                        if (input.isNext('/')) {
                            // End-of-line comment ...
                            boolean foundLineTerminator = false;
                            while (input.hasNext()) {
                                c = input.next();
                                if (c == '\n' || c == '\r') {
                                    foundLineTerminator = true;
                                    break;
                                }
                            }
                            endIndex = input.index(); // the token won't include the '\n' or '\r' character(s)
                            if (!foundLineTerminator) {
                                ++endIndex; // must point beyond last char
                            }
                            if (c == '\r' && input.isNext('\n')) {
                                input.next();
                            }
                            if (this.useComments) {
                                tokens.addToken(startingPosition, startIndex, endIndex, COMMENT);
                            }
                        } else if (input.isNext('*')) {
                            // Multi-line comment ...
                            while (input.hasNext() && !input.isNext('*', '/')) {
                                c = input.next();
                            }
                            if (input.hasNext()) {
                                input.next(); // consume the '*'
                            }
                            if (input.hasNext()) {
                                input.next(); // consume the '/'
                            }
                            if (this.useComments) {
                                endIndex = input.index() + 1; // the token will include the '/' and '*' characters
                                tokens.addToken(startingPosition, startIndex, endIndex, COMMENT);
                            }
                        } else {
                            // just a regular slash ...
                            tokens.addToken(startingPosition, startIndex, startIndex + 1, SYMBOL);
                        }
                        break;
                    default:
                        startIndex = input.index();
                        startingPosition = input.position(startIndex);
                        // Read until another whitespace/symbol/decimal/slash is found
                        while (input.hasNext() && !(input.isNextWhitespace() || input.isNextAnyOf("/.-(){}*,;+%?$[]!<>|=:"))) {
                            c = input.next();
                        }
                        endIndex = input.index() + 1; // beyond last character that was included
                        tokens.addToken(startingPosition, startIndex, endIndex, WORD);
                }
            }
        }
    }

    /**
     *
     */
    protected class CaseInsensitiveToken extends CaseSensitiveToken {

        /**
         * @param startIndex
         * @param endIndex
         * @param type
         * @param position
         */
        public CaseInsensitiveToken( int startIndex,
                                     int endIndex,
                                     int type,
                                     Position position ) {
            super(startIndex, endIndex, type, position);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CaseSensitiveToken#matchString()
         */
        @Override
        protected String matchString() {
            return TokenStream.this.inputUppercased;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CaseSensitiveToken#withType(int)
         */
        @Override
        public Token withType( int typeMask ) {
            final int type = this.type() | typeMask;
            return new CaseInsensitiveToken(startIndex(), endIndex(), type, position());
        }
    }

    /**
     *
     */
    public class CaseInsensitiveTokenFactory extends TokenFactory {

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Tokens#addToken(org.jboss.tools.modeshape.jcr.text.Position, int,
         *      int, int)
         */
        @Override
        public void addToken( Position position,
                              int startIndex,
                              int endIndex,
                              int type ) {
            this.tokens.add(new CaseInsensitiveToken(startIndex, endIndex, type, position));
        }
    }

    /**
     * An immutable {@link Token} that implements matching using case-sensitive logic.
     */
    protected class CaseSensitiveToken implements Token {
        private final int startIndex;
        private final int endIndex;
        private final int type;
        private final Position position;

        /**
         * @param startIndex
         * @param endIndex
         * @param type
         * @param position
         */
        public CaseSensitiveToken( int startIndex,
                                   int endIndex,
                                   int type,
                                   Position position ) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.type = type;
            this.position = position;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#endIndex()
         */
        @Override
        public final int endIndex() {
            return this.endIndex;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#length()
         */
        @Override
        public final int length() {
            return this.endIndex - this.startIndex;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#matches(char)
         */
        @Override
        public final boolean matches( char expected ) {
            return length() == 1 && matchString().charAt(this.startIndex) == expected;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#matches(int)
         */
        @Override
        public final boolean matches( int expectedType ) {
            return expectedType == ANY_TYPE || (currentToken().type() & expectedType) == expectedType;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#matches(java.lang.String)
         */
        @Override
        public final boolean matches( String expected ) {
            return matchString().substring(this.startIndex, this.endIndex).equals(expected);
        }

        /**
         * @return the input string
         */
        protected String matchString() {
            return TokenStream.this.inputString;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#position()
         */
        @Override
        public Position position() {
            return this.position;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#startIndex()
         */
        @Override
        public final int startIndex() {
            return this.startIndex;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return value();
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#type()
         */
        @Override
        public final int type() {
            return this.type;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#value()
         */
        @Override
        public final String value() {
            return TokenStream.this.inputString.substring(this.startIndex, this.endIndex);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Token#withType(int)
         */
        @Override
        public Token withType( int typeMask ) {
            final int type = this.type | typeMask;
            return new CaseSensitiveToken(this.startIndex, this.endIndex, type, this.position);
        }
    }

    /**
     *
     */
    public class CaseSensitiveTokenFactory extends TokenFactory {

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Tokens#addToken(org.jboss.tools.modeshape.jcr.text.Position, int,
         *      int, int)
         */
        @Override
        public void addToken( Position position,
                              int startIndex,
                              int endIndex,
                              int type ) {
            this.tokens.add(new CaseSensitiveToken(startIndex, endIndex, type, position));
        }
    }

    /**
     * An implementation of {@link CharacterStream} that works with a single character array.
     */
    public static final class CharacterArrayStream implements CharacterStream {
        private final char[] content;
        private int lastIndex = -1;
        private final int maxIndex;
        private int lineNumber = 1;
        private int columnNumber = 0;
        private boolean nextCharMayBeLineFeed;

        /**
         * @param content
         */
        public CharacterArrayStream( char[] content ) {
            this.content = content;
            this.maxIndex = content.length - 1;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#hasNext()
         */
        @Override
        public boolean hasNext() {
            return this.lastIndex < this.maxIndex;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#index()
         */
        @Override
        public int index() {
            return this.lastIndex;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNext(char)
         */
        @Override
        public boolean isNext( char c ) {
            final int nextIndex = this.lastIndex + 1;
            return nextIndex <= this.maxIndex && this.content[nextIndex] == c;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNext(char, char)
         */
        @Override
        public boolean isNext( char nextChar1,
                               char nextChar2 ) {
            final int nextIndex1 = this.lastIndex + 1;
            final int nextIndex2 = this.lastIndex + 2;
            return nextIndex2 <= this.maxIndex && this.content[nextIndex1] == nextChar1 && this.content[nextIndex2] == nextChar2;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNext(char, char, char)
         */
        @Override
        public boolean isNext( char nextChar1,
                               char nextChar2,
                               char nextChar3 ) {
            final int nextIndex1 = this.lastIndex + 1;
            final int nextIndex2 = this.lastIndex + 2;
            final int nextIndex3 = this.lastIndex + 3;
            return nextIndex3 <= this.maxIndex && this.content[nextIndex1] == nextChar1 && this.content[nextIndex2] == nextChar2
                   && this.content[nextIndex3] == nextChar3;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNextAnyOf(char[])
         */
        @Override
        public boolean isNextAnyOf( char[] characters ) {
            final int nextIndex = this.lastIndex + 1;
            if (nextIndex <= this.maxIndex) {
                final char nextChar = this.content[this.lastIndex + 1];
                for (final char c : characters) {
                    if (c == nextChar) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNextAnyOf(java.lang.String)
         */
        @Override
        public boolean isNextAnyOf( String characters ) {
            final int nextIndex = this.lastIndex + 1;
            if (nextIndex <= this.maxIndex) {
                final char nextChar = this.content[this.lastIndex + 1];
                if (characters.indexOf(nextChar) != -1) {
                    return true;
                }
            }
            return false;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNextLetterOrDigit()
         */
        @Override
        public boolean isNextLetterOrDigit() {
            final int nextIndex = this.lastIndex + 1;
            return nextIndex <= this.maxIndex && Character.isLetterOrDigit(this.content[nextIndex]);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNextValidXmlCharacter()
         */
        @Override
        public boolean isNextValidXmlCharacter() {
            final int nextIndex = this.lastIndex + 1;
            return nextIndex <= this.maxIndex && XmlCharacters.isValid(this.content[nextIndex]);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNextValidXmlNameCharacter()
         */
        @Override
        public boolean isNextValidXmlNameCharacter() {
            final int nextIndex = this.lastIndex + 1;
            return nextIndex <= this.maxIndex && XmlCharacters.isValidName(this.content[nextIndex]);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNextValidXmlNcNameCharacter()
         */
        @Override
        public boolean isNextValidXmlNcNameCharacter() {
            final int nextIndex = this.lastIndex + 1;
            return nextIndex <= this.maxIndex && XmlCharacters.isValidNcName(this.content[nextIndex]);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#isNextWhitespace()
         */
        @Override
        public boolean isNextWhitespace() {
            final int nextIndex = this.lastIndex + 1;
            return nextIndex <= this.maxIndex && Character.isWhitespace(this.content[nextIndex]);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#next()
         */
        @Override
        public char next() {
            if (this.lastIndex >= this.maxIndex) {
                throw new NoSuchElementException();
            }
            final char result = this.content[++this.lastIndex];
            ++this.columnNumber;
            if (result == '\r') {
                this.nextCharMayBeLineFeed = true;
                ++this.lineNumber;
                this.columnNumber = 0;
            } else if (result == '\n') {
                if (!this.nextCharMayBeLineFeed) {
                    ++this.lineNumber;
                }
                this.columnNumber = 0;
            } else if (this.nextCharMayBeLineFeed) {
                this.nextCharMayBeLineFeed = false;
            }
            return result;
        }

        /**
         * {@inheritDoc}
         *
         * @param startIndex
         * @return the position of the token. never null
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.CharacterStream#position(int)
         */
        @Override
        public Position position( int startIndex ) {
            return new Position(startIndex, this.lineNumber, this.columnNumber);
        }
    }

    /**
     * Interface used by a {@link Tokenizer} to iterate through the characters in the content input to the {@link TokenStream}.
     */
    public static interface CharacterStream {

        /**
         * Determine if there is another character available in this stream.
         *
         * @return true if there is another character (and {@link #next()} can be called), or false otherwise
         */
        boolean hasNext();

        /**
         * Get the index for the last character returned from {@link #next()}.
         *
         * @return the index of the last character returned
         */
        int index();

        /**
         * Determine if the next character on the stream is the supplied value. This method does <i>not</i> advance the stream.
         *
         * @param c the character value to compare to the next character on the stream
         * @return true if there is a {@link #next() next} character and it is the supplied character, or false otherwise
         */
        boolean isNext( char c );

        /**
         * Determine if the next two characters on the stream match the supplied values. This method does <i>not</i> advance the
         * stream.
         *
         * @param nextChar the character value to compare to the next character on the stream
         * @param followingChar the character value to compare to the character immediately after the next character on the stream
         * @return true if there are at least two characters left on the stream and the first matches <code>nextChar</code> and
         *         the second matches <code>followingChar</code>
         */
        boolean isNext( char nextChar,
                        char followingChar );

        /**
         * Determine if the next three characters on the sream match the supplied values. This method does <i>not</i> advance the
         * stream.
         *
         * @param nextChar the character value to compare to the next character on the stream
         * @param nextChar2 the character value to compare to the second character on the stream
         * @param nextChar3 the character value to compare to the second character on the stream
         * @return true if there are at least two characters left on the stream and the first matches <code>nextChar</code> and
         *         the second matches <code>followingChar</code>
         */
        boolean isNext( char nextChar,
                        char nextChar2,
                        char nextChar3 );

        /**
         * Determine if the next character on the stream matches one of the supplied characters. This method does <i>not</i>
         * advance the stream.
         *
         * @param characters the characters to match
         * @return true if there is a {@link #next() next} character and it does match one of the supplied characters, or false
         *         otherwise
         */
        boolean isNextAnyOf( char[] characters );

        /**
         * Determine if the next character on the stream matches one of the supplied characters. This method does <i>not</i>
         * advance the stream.
         *
         * @param characters the characters to match
         * @return true if there is a {@link #next() next} character and it does match one of the supplied characters, or false
         *         otherwise
         */
        boolean isNextAnyOf( String characters );

        /**
         * Determine if the next character on the stream is a {@link Character#isLetterOrDigit(char) letter or digit}. This method
         * does <i>not</i> advance the stream.
         *
         * @return true if there is a {@link #next() next} character and it is a letter or digit, or false otherwise
         */
        boolean isNextLetterOrDigit();

        /**
         * Determine if the next character on the stream is a {@link XmlCharacters#isValid(int) valid XML character}. This method
         * does <i>not</i> advance the stream.
         *
         * @return true if there is a {@link #next() next} character and it is a valid XML character, or false otherwise
         */
        boolean isNextValidXmlCharacter();

        /**
         * Determine if the next character on the stream is a {@link XmlCharacters#isValidName(int) valid XML NCName character}.
         * This method does <i>not</i> advance the stream.
         *
         * @return true if there is a {@link #next() next} character and it is a valid XML Name character, or false otherwise
         */
        boolean isNextValidXmlNameCharacter();

        /**
         * Determine if the next character on the stream is a {@link XmlCharacters#isValidNcName(int) valid XML NCName character}.
         * This method does <i>not</i> advance the stream.
         *
         * @return true if there is a {@link #next() next} character and it is a valid XML NCName character, or false otherwise
         */
        boolean isNextValidXmlNcNameCharacter();

        /**
         * Determine if the next character on the stream is a {@link Character#isWhitespace(char) whitespace character}. This
         * method does <i>not</i> advance the stream.
         *
         * @return true if there is a {@link #next() next} character and it is a whitespace character, or false otherwise
         */
        boolean isNextWhitespace();

        /**
         * Obtain the next character value, and advance the stream.
         *
         * @return the next character
         * @throws NoSuchElementException if there is no {@link #hasNext() next character}
         */
        char next();

        /**
         * Get the position for the last character returned from {@link #next()}.
         *
         * @param startIndex
         * @return the position of the last character returned; never null
         */
        Position position( int startIndex );

    }

    /**
     * The interface defining a token, which references the characters in the actual input character stream.
     *
     * @see CaseSensitiveTokenFactory
     * @see CaseInsensitiveTokenFactory
     */
    public interface Token {
        /**
         * Get the index in the raw stream past the last character in the token.
         *
         * @return the ending index of the token, which is past the last character
         */
        int endIndex();

        /**
         * Get the length of the token, which is equivalent to <code>endIndex() - startIndex()</code>.
         *
         * @return the length
         */
        int length();

        /**
         * Determine if the token matches the supplied character.
         *
         * @param expected the expected character value
         * @return true if the token's value matches the supplied character value, or false otherwise
         */
        boolean matches( char expected );

        /**
         * Determine if the token matches the supplied type.
         *
         * @param expectedType the expected integer type
         * @return true if the token's value matches the supplied integer type, or false otherwise
         */
        boolean matches( int expectedType );

        /**
         * Determine if the token matches the supplied string.
         *
         * @param expected the expected value
         * @return true if the token's value matches the supplied value, or false otherwise
         */
        boolean matches( String expected );

        /**
         * Get the position of this token, which includes the line number and column number of the first character in the token.
         *
         * @return the position; never null
         */
        Position position();

        /**
         * Get the index in the raw stream for the first character in the token.
         *
         * @return the starting index of the token
         */
        int startIndex();

        /**
         * Get the type of the token.
         *
         * @return the token's type
         */
        int type();

        /**
         * Get the value of the token, in actual case.
         *
         * @return the value
         */
        String value();

        /**
         * Bitmask ORed with existing type value.
         *
         * @param typeMask
         * @return copy of Token with new type
         */
        Token withType( int typeMask );
    }

    /**
     *
     */
    protected abstract class TokenFactory implements Tokens {

        /**
         */
        protected final List<Token> tokens = new ArrayList<Token>();

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Tokens#addToken(org.jboss.tools.modeshape.jcr.text.Position, int)
         */
        @Override
        public void addToken( Position position,
                              int index ) {
            addToken(position, index, index + 1, 0);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.text.TokenStream.Tokens#addToken(org.jboss.tools.modeshape.jcr.text.Position, int,
         *      int)
         */
        @Override
        public final void addToken( Position position,
                                    int startIndex,
                                    int endIndex ) {
            addToken(position, startIndex, endIndex, 0);
        }

        /**
         * @return tokens
         */
        public List<Token> getTokens() {
            return this.tokens;
        }
    }

    /**
     * Interface for a Tokenizer component responsible for processing the characters in a {@link CharacterStream} and constructing
     * the appropriate {@link Token} objects.
     */
    public static interface Tokenizer {
        /**
         * Process the supplied characters and construct the appropriate {@link Token} objects.
         *
         * @param input the character input stream; never null
         * @param tokens the factory for {@link Token} objects, which records the order in which the tokens are created
         * @throws ParsingException if there is an error while processing the character stream (e.g., a quote is not closed, etc.)
         */
        void tokenize( CharacterStream input,
                       Tokens tokens ) throws ParsingException;
    }

    /**
     * A factory for Token objects, used by a {@link Tokenizer} to create tokens in the correct order.
     */
    public static interface Tokens {
        /**
         * Create a single-character token at the supplied index in the character stream. The token type is set to 0, meaning this
         * is equivalent to calling <code>addToken(index,index+1)</code> or <code>addToken(index,index+1,0)</code>.
         *
         * @param position the position (line and column numbers) of this new token; may not be null
         * @param index the index of the character to appear in the token; must be a valid index in the stream
         */
        void addToken( Position position,
                       int index );

        /**
         * Create a single- or multi-character token with the characters in the range given by the starting and ending index in
         * the character stream. The character at the ending index is <i>not</i> included in the token (as this is standard
         * practice when using 0-based indexes). The token type is set to 0, meaning this is equivalent to calling <code>
         * addToken(startIndex,endIndex,0)</code> .
         *
         * @param position the position (line and column numbers) of this new token; may not be null
         * @param startIndex the index of the first character to appear in the token; must be a valid index in the stream
         * @param endIndex the index just past the last character to appear in the token; must be a valid index in the stream
         */
        void addToken( Position position,
                       int startIndex,
                       int endIndex );

        /**
         * Create a single- or multi-character token with the supplied type and with the characters in the range given by the
         * starting and ending index in the character stream. The character at the ending index is <i>not</i> included in the
         * token (as this is standard practice when using 0-based indexes).
         *
         * @param position the position (line and column numbers) of this new token; may not be null
         * @param startIndex the index of the first character to appear in the token; must be a valid index in the stream
         * @param endIndex the index just past the last character to appear in the token; must be a valid index in the stream
         * @param type the type of the token
         */
        void addToken( Position position,
                       int startIndex,
                       int endIndex,
                       int type );
    }
}
