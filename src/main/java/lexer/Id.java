package lexer;

/**
 * @author gayashan
 */
public class Id extends Token {
    public final String lexeme;
    public float value;

    public Id(int t, String s) {
        super(t);
        lexeme = new String(s);
        super.lexeme=lexeme;
        value=0;
    }
}
