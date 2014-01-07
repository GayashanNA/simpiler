package lexer;

/**
 * @author gayashan
 */
public class Type extends Token {
    public int width = 0;
    public String lexeme;

    public Type(String lex, int tag, int w) {
        super(tag);
        this.lexeme = lex;
        this.width = w;
    }

    public static final Type Int = new Type("int", Tag.INT, 4), Float = new Type("float", Tag.FLOAT, 8);

    public static boolean numeric(Type p) {
        return p == Type.Int || p == Type.Float;
    }

    public static Type max(Type p1, Type p2) {
        if (!numeric(p1) || !numeric(p2)) return null;
        else if (p1 == Type.Float || p2 == Type.Float) return Type.Float;
        else if (p1 == Type.Int || p2 == Type.Int) return Type.Int;
        else return null;
    }
}
