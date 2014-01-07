package inter;

import lexer.Token;

/**
 * @author gayashan
 */
public class Leaf extends Node {
    public Token token;

    public Leaf(Token token) {
        super.value = Node.used.size() + 1;
        this.token = token;
    }
}
