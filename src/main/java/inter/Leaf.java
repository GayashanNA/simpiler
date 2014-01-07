package inter;

import lexer.Token;

/**
 * @author gayashan
 */
public class Leaf extends AbsNode {
    public Token token;

    public Leaf(Token token) {
        super.value = AbsNode.used.size() + 1;
        this.token = token;
    }
}
