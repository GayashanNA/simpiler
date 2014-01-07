package parser;

import evaluator.StackMachine;
import inter.AbsNode;
import inter.Leaf;
import inter.Node;
import lexer.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Conducts parsing, evaluation and 3AC generation
 *
 * @author gayashan
 */
public class Parser {
    private final StackMachine stackMachine;
    private Token lookahead;
    private final Lexer lex;
    private ArrayList<Token> symbols;
    private BufferedWriter bw;
    private Id id;
    private int topToken = -1;

    public Parser(Lexer lex) throws IOException {
        this.lex = lex;
        this.stackMachine = new StackMachine();
        this.lookahead = lex.scan();
        this.symbols = new ArrayList<Token>();
        this.bw = new BufferedWriter(new FileWriter("compile.out"));
        symbols.add(lookahead);
        topToken++;
    }


    /**
     * Match and get the next lookahead
     *
     * @param tagType
     * @throws IOException
     */
    public void match(int tagType) throws IOException {
        if (lookahead.tag == tagType && symbols.size() == topToken + 1) {
            lookahead = lex.scan();
            symbols.add(lookahead);
            topToken++;
        } else if (lookahead.tag == tagType && symbols.size() > topToken + 1) {
            topToken++;
            lookahead = symbols.get(topToken);
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    /**
     * P -> D L
     *
     * @throws IOException
     */
    public void P() throws IOException {
        if (lookahead.tag == Tag.INT || lookahead.tag == Tag.FLOAT) {   //P -> D L
            D();
            L();
            bw.flush();
            bw.close();
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    /**
     * D -> B N ; D1
     *
     * @throws IOException
     */
    public void D() throws IOException {
        String type;
        if (lookahead.tag == Tag.INT || lookahead.tag == Tag.FLOAT) {   //D -> B N ; D1
            type = B();
            N(type);
            match(';');
            D1();
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    /**
     * D1 -> D | empty
     *
     * @throws IOException
     */
    public void D1() throws IOException {
        if (lookahead.tag == Tag.INT || lookahead.tag == Tag.FLOAT) {   //D1 -> D
            D();
        } else {    //D1 -> empty
        }

    }

    /**
     * B -> int | float
     *
     * @return
     * @throws IOException
     */
    public String B() throws IOException {
        String type;
        if (lookahead.tag == Tag.INT) {     //B -> int
            match(Tag.INT);
            type = "int";
        } else if (lookahead.tag == Tag.FLOAT) {    //B -> float
            match(Tag.FLOAT);
            type = "float";
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
        return type;
    }

    /**
     * N -> id N1
     *
     * @param btype
     * @throws IOException
     */
    public void N(String btype) throws IOException {
        if (lookahead.tag == Tag.ID) {  //N -> id N1
            id = (Id) lookahead;
            id.type = btype;
            System.out.println("(" + id.lexeme + ":" + id.type + ")");
            match(Tag.ID);
            N1(btype);
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    /**
     * N1 -> , id N1 | empty
     *
     * @param ntype
     * @throws IOException
     */
    public void N1(String ntype) throws IOException {
        if (lookahead.tag == ',') {     //N1 -> , id N1
            match(',');
            id = (Id) lookahead;
            id.type = ntype;
            System.out.println("(" + id.lexeme + ":" + id.type + ")");
            match(Tag.ID);
            N1(ntype);
        } else {    //N1 -> empty
        }

    }

    /**
     * L -> S ; L1
     *
     * @throws IOException
     */
    public void L() throws IOException {
        if (lookahead.tag == Tag.ID || lookahead.tag == '(' || lookahead.tag == Tag.NUM) {  //L -> S ; L1
            S();
            match(';');
            AbsNode.used = new ArrayList<AbsNode>(); // new set of nodes for new S
            AbsNode.tempvarcount = 0;
            System.out.println("//result: " + stackMachine.pop());
            L1();
        } else {
            throw new Error("syntax error at line" + lex.line);
        }
    }

    /**
     * L1 -> L | empty
     *
     * @throws IOException
     */
    public void L1() throws IOException {
        if (lookahead.tag == Tag.ID || lookahead.tag == '(' || lookahead.tag == Tag.NUM) {  //L1 -> L
            L();
        } else {    //L1 -> empty
        }
    }

    /**
     * S -> id = E | E
     *
     * @throws IOException
     */
    public void S() throws IOException {
        AbsNode node;
        AbsNode exprn;
        Id assId;
        if (lookahead.tag == '(' || lookahead.tag == Tag.NUM) {     //S -> E
            node = E();
        } else if (lookahead.tag == Tag.ID) {           //S -> id = E | E
            id = (Id) lookahead;
            assId = id;
            System.out.print(id.lexeme);
            match(Tag.ID);
            if (lookahead.tag == '=') { //S -> id = E
                match('=');
                stackMachine.addToMachine(Float.toString(id.value)); //pass the id to the stack
                exprn = E();
                node = getNode(getLeaf(assId), exprn, "="); //3AC for assignment
                stackMachine.addToMachine("=");   // let the stack machine evaluate the assignment
                id.value = stackMachine.getTop();   //get the calculated value on top of the stack
                System.out.print("=");
            } else {              //S -> E
                topToken--;    //have to go back
                lookahead = symbols.get(topToken);
                node = E();
            }
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    /**
     * E -> T E1
     *
     * @return
     * @throws IOException
     */
    public AbsNode E() throws IOException {
        AbsNode node;
        AbsNode termnode;
        if (lookahead.tag == Tag.ID || lookahead.tag == '(' || lookahead.tag == Tag.NUM) {  //E -> T E1
            termnode = T();
            node = E1(termnode);
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
        return node;
    }

    /**
     * E1 -> + T E1 | empty
     *
     * @param termnodeinh
     * @return
     * @throws IOException
     */
    public AbsNode E1(AbsNode termnodeinh) throws IOException {
        AbsNode node; // node which represents the operation so far
        AbsNode snode;  // synthesised attribute which gives the full answer
        AbsNode curtn;
        if (lookahead.tag == '+') {     //E1 -> + T E1
            match('+');
            curtn = T();
            stackMachine.addToMachine("+");
            System.out.print("+");
            node = getNode(termnodeinh, curtn, "+");
            snode = E1(node);
        } else {    //E1 -> empty
            snode = termnodeinh;
        }
        return snode;
    }

    /**
     * T -> F T1
     *
     * @return
     * @throws IOException
     */
    public AbsNode T() throws IOException {
        AbsNode node;
        AbsNode factnode;
        if (lookahead.tag == Tag.ID || lookahead.tag == '(' || lookahead.tag == Tag.NUM) {  //T -> F T1
            factnode = F();
            node = T1(factnode);
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
        return node;
    }

    /**
     * T1 -> * F T1 | empty
     *
     * @param factnodeinh
     * @return
     * @throws IOException
     */
    public AbsNode T1(AbsNode factnodeinh) throws IOException {
        AbsNode node; // node which represents the operation so far
        AbsNode snode;  // synthesised attribute which gives the full answer
        AbsNode curfn;
        if (lookahead.tag == '*') { //T1 -> * F T1
            match('*');
            curfn = F();
            stackMachine.addToMachine("*");
            System.out.print("*");
            node = getNode(factnodeinh, curfn, "*");
            snode = T1(node);
        } else {    //T1 -> empty
            snode = factnodeinh;
        }
        return snode;
    }

    /**
     * F -> ( E ) | num | id
     *
     * @return
     * @throws IOException
     */
    public AbsNode F() throws IOException {
        AbsNode node;
        if (lookahead.tag == '(') {     //F -> ( E )
            match('(');
            node = E();
            match(')');
        } else if (lookahead.tag == Tag.NUM) {  //F -> num
            Num num = (Num) lookahead;
            match(Tag.NUM);
            stackMachine.addToMachine(num.lexeme);
            System.out.print(num.lexeme);
            node = getLeaf(num);
        } else if (lookahead.tag == Tag.ID) {   //F -> id
            id = (Id) lookahead;
            match(Tag.ID);
            stackMachine.addToMachine(Float.toString(id.value));
            System.out.print(id.lexeme);
            node = getLeaf(id);
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
        return node;
    }

    /**
     * Get a node given its left, right nodes and the operator.
     * Widening conversions are also done here.
     *
     * @param l
     * @param r
     * @param op
     * @return
     * @throws IOException
     */
    public Node getNode(AbsNode l, AbsNode r, String op) throws IOException {
        Node n;
        for (int i = 0; i < AbsNode.used.size(); i++) {
            if (AbsNode.used.get(i) instanceof Node) {
                n = (Node) AbsNode.used.get(i);
                if (n.op.equals(op) && l == n.left && r == n.right) {
                    return n;
                }
            }
        }
        if (!op.equals("=")) { // widen is done for both operands if not assignment
            if (widen(l, typeMax(l, r))) {
                AbsNode.tempvarcount++;
                l = new Node("(float)", l, null);
                l.type = "float";
                generate3AC(l);
            }
            if (widen(r, typeMax(l, r))) {
                AbsNode.tempvarcount++;
                r = new Node("(float)", r, null);
                r.type = "float";
                generate3AC(r);
            }
        } else { // if assignment only right side is updated
            if (widen(r, typeMax(l, r))) {
                AbsNode.tempvarcount++;
                r = new Node("(float)", r, null);
                r.type = "float";
                generate3AC(r);
            }
        }
        AbsNode.tempvarcount++; //give a new number to the temperory
        n = new Node(op, l, r);
        if (!n.op.equals("=")) {
            n.type = typeMax(l, r);  // in assignment we can't change type
        }
        generate3AC(n);
        AbsNode.used.add(n);
        return n;
    }

    /**
     * Get the leaf node of AST
     *
     * @param token
     * @return
     */
    public Leaf getLeaf(Token token) {
        Leaf l;
        for (int i = 0; i < AbsNode.used.size(); i++) {
            if (AbsNode.used.get(i) instanceof Leaf) {
                l = (Leaf) AbsNode.used.get(i);
                if (token == (l.token))
                    return l;
            }
        }
        l = new Leaf(token);
        l.type = token.type;
        AbsNode.used.add(l);
        return l;
    }

    /**
     * Generate the 3AC using temporaries if appropriate
     *
     * @param inNode
     * @throws IOException
     */
    private void generate3AC(AbsNode inNode) throws IOException {
        Leaf l;
        Node n;
        Id id, assId;
        Num num;
        String leftstr, rightstr;
        n = (Node) inNode;
        if (n.right != null) {
            if (!n.op.equals("=")) {
                leftstr = "t" + n.left.value;
                rightstr = "t" + n.right.value;
                if (n.left instanceof Leaf) {
                    l = (Leaf) n.left;
                    if (l.token.tag == Tag.ID) {
                        id = (Id) l.token;
                        leftstr = id.lexeme;
                    } else {
                        num = (Num) l.token;
                        leftstr = num.lexeme;
                    }
                }
                if (n.right instanceof Leaf) {
                    l = (Leaf) n.right;
                    if (l.token.tag == Tag.ID) {
                        id = (Id) l.token;
                        rightstr = id.lexeme;
                    } else {
                        num = (Num) l.token;
                        rightstr = num.lexeme;
                    }
                }

                bw.write("t" + n.value + " = " + leftstr + n.op + rightstr);

            } else {  //an assignment
                l = (Leaf) n.left;
                assId = (Id) l.token; // left of assignment is definitely id

                if (assId.type.equals("int") && n.right.type.equals("float")) {
                    throw new Error("Narrowing conversions are not allowed.");
                }
                if (!(n.right instanceof Leaf)) {
                    rightstr = "t" + n.right.value;
                } else {

                    l = (Leaf) n.right;
                    if (l.token.tag == Tag.ID) {
                        id = (Id) l.token;
                        rightstr = id.lexeme;
                    } else {
                        num = (Num) l.token;
                        rightstr = num.lexeme;
                    }
                }
                bw.write(assId.lexeme + " = " + rightstr);
            }
            bw.newLine();
        } else {
            if (n.left instanceof Leaf) {
                l = (Leaf) n.left;
                bw.write("t" + n.value + " = " + n.op + l.token.lexeme);
            } else {
                bw.write("t" + n.value + " = " + n.op + "t" + n.left.value);
            }
            bw.newLine();
        }
    }

    /**
     * @param n
     * @param maxtype
     * @return returns true if a widening conversion should be done
     */
    private boolean widen(AbsNode n, String maxtype) {
        return !maxtype.equals(n.type);
    }

    /**
     * Get the maximum type given two nodes
     * Used for widening conversions.
     *
     * @param l
     * @param r
     * @return
     */
    private String typeMax(AbsNode l, AbsNode r) {
        String max = "int";
        if (r.type.equals("float") || l.type.equals("float")) {
            max = "float";
        }
        return max;
    }
}
