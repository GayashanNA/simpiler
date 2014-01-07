package lexer;

/**
 * @author gayashan
 */
public class Num extends Token {
    public final String lexeme;

    public Num(int t, String s, String type) {
        super(t);
        lexeme = s;
        super.type = type;
        super.lexeme = lexeme;
    }
}
