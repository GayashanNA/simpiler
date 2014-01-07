package lexer;

import java.io.IOException;
import java.util.Hashtable;

/**
 * @author gayashan
 */
public class Lexer {
    public int line = 1;
    private char peek = ' ';
    private final Hashtable words = new Hashtable();

    /**
     * Reserve a word in the symbol table
     *
     * @param t
     */
    void reserve(Id t) {
        words.put(t.lexeme, t);
    }

    /***
     * Read next character from input stream
     * @throws IOException
     */
    private void readch() throws IOException {
        this.peek = (char) System.in.read();
    }

    public Lexer() {
        reserve(new Id(Tag.INT, "int"));
        reserve(new Id(Tag.FLOAT, "float"));
    }

    /**
     * Conduct scanning of input stream and tokenize
     *
     * @return
     * @throws IOException
     */
    public Token scan() throws IOException {
        for (; ; readch()) {
            if (peek == ' ' || peek == '\t' || peek == '\r') {      //ignore spaces, tabs and blanks
                continue;
            } else if (peek == '\n') {  //count number of lines, later used in error reporting
                line = line + 1;
            } else {
                break;
            }
        }

        //if token is an id
        if (Character.isLetter(peek)) {
            StringBuilder b = new StringBuilder();
            do {
                b.append(this.peek);
                readch();
            } while (Character.isLetterOrDigit(this.peek));
            String s = b.toString();
            Id w = (Id) words.get(s);
            if (w != null) return w;    //if the id is already in the symbol table
            w = new Id(Tag.ID, s);
            words.put(s, w);
            return w;
        }

        //If the token is a number
        if (Character.isDigit(peek)) {

            String type = "int";
            StringBuilder b = new StringBuilder();
            do {
                b.append(peek);
                readch();
            } while (Character.isDigit(peek)); // first digit is anyway added

            if (peek == '.') {  //if it's a float
                type = "float";
                do {
                    b.append(peek);
                    readch();

                } while (Character.isDigit(peek));
            }

            return new Num(Tag.NUM, b.toString(), type);
        }

        Token t = new Token(peek);
        peek = ' ';
        return t;
    }
}
