import lexer.Lexer;
import parser.Parser;

import java.io.IOException;

/**
 * @author gayashan
 */
public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        try {
            Parser parser = new Parser(lexer);
            parser.P();
            System.out.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
