package lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * @author gayashan
 */
public class Lexer {
    public int line = 1;
    private char peek = ' ' ;
    private InputStream in;
    private Hashtable words = new Hashtable() ;
    void reserve(Id t) { words.put (t.lexeme, t) ; }

    public Lexer(){
        in = System.in;
        reserve( new Id(Tag.INT, "int") ) ;
        reserve ( new Id(Tag.FLOAT, "float") ) ;
    }

    public Token scan() throws IOException {
        for( ; ; peek = (char)in.read() ) {
            if ( peek == ' '|| peek == '\t'||peek == '\r' ){  continue ;}
            else if(peek == '\n' ) { line = line + 1;
                continue;
            }
            else{ break;}
        }

        if ( Character. isLetter (peek) ) {
            StringBuffer b = new StringBuffer();

            do {
                b.append (peek) ;
                peek = (char)in.read() ;
            } while( Character.isLetterOrDigit(peek));

            String s = b.toString();
            Id w = (Id) words. get (s) ;
            if ( w != null) {
                return w;
            }
            if(s.length()>1) throw new Error("Identifier with more than one letter or wrong type name");

            w = new Id(Tag.ID, s) ;
            words .put (s, w) ;
            return w;
        }

        //If the token is a potential number
        if ( Character. isDigit (peek) ) {

            String type="int";
            StringBuffer b = new StringBuffer();
            do {
                b.append (peek) ;
                peek = (char)in. read() ;
            }while ( Character. isDigit (peek) ) ; // first digit is anyway added

            if(peek=='.'){
                type="float";
                do {
                    b.append (peek) ;
                    peek = (char)in. read() ;

                }while ( Character. isDigit (peek) ) ;
            } // decimal dot for floating point numbers

            return new Num(Tag.NUM,b.toString(),type) ;
        }

        Token t = new Token(peek) ;
        peek = ' ' ;
        return t;
    }
}
