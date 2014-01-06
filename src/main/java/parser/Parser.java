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
 * @author gayashan
 */
public class Parser {
    private StackMachine stackMachine;
    private Token lookahead;
    private Lexer lex;
    private ArrayList<Token> symbols;
    private BufferedWriter bw;
    private Id id;
    private Num num;
    private int topToken = -1;

    public Parser(Lexer lex) throws IOException {
        this.lex = lex;
        this.stackMachine = new StackMachine();
        this.lookahead = lex.scan();
        this.symbols  = new ArrayList<Token>();
        this.bw = new BufferedWriter(new FileWriter("compile.txt"));
        symbols.add(lookahead);
        topToken++;
    }

    public void P() throws IOException {
        if (lookahead.tag == Tag.INT || lookahead.tag == Tag.FLOAT) {
            D();
            L();
            bw.close();
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    public void D() throws IOException {
        String temp;
        if (lookahead.tag == Tag.INT || lookahead.tag == Tag.FLOAT) {
            temp = B();
            N(temp);
            match(';');
            D1();
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    public void D1() throws IOException {
        if (lookahead.tag == Tag.INT || lookahead.tag == Tag.FLOAT) {
            D();
        } else {
        }

    }

    public String B() throws IOException {
        String type;
        if (lookahead.tag == Tag.INT) {
            match(Tag.INT);
            type = "int";

        } else if (lookahead.tag == Tag.FLOAT) {
            match(Tag.FLOAT);
            type = "float";
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
        return type;
    }

    public void N(String btype) throws IOException {

        String inh = btype;
        if (lookahead.tag == Tag.ID) {
            id = (Id) lookahead;
            id.type = inh;
            System.out.println(id.type);
            match(Tag.ID);
            N1(inh);
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    public void N1(String ntype) throws IOException {
        String inh = ntype;
        if (lookahead.tag == ',') {
            match(',');
            id = (Id) lookahead;
            id.type = inh;
            System.out.println(id.type);
            match(Tag.ID);
            N1(inh);
        } else {
        }

    }

    public void L() throws IOException {
        if (lookahead.tag == Tag.ID || lookahead.tag == '(' || lookahead.tag == Tag.NUM) {
            S();
            match(';');
            AbsNode.used = new ArrayList<AbsNode>(); // new set of nodes for new S
            AbsNode.statVal = 0;
            System.out.println(";" + " " + stackMachine.pop());
            L1();
        } else {
            throw new Error("syntax error at line" + lex.line);

        }
    }

    public void L1() throws IOException {
        if (lookahead.tag == Tag.ID || lookahead.tag == '(' || lookahead.tag == Tag.NUM) {
            L();
        } else {
        }
    }

    public void S() throws IOException {
        AbsNode node;
        AbsNode exprn;
        Id assId;
        if (lookahead.tag == '(' || lookahead.tag == Tag.NUM) {
            node = E();
        } else if (lookahead.tag == Tag.ID) {
            id = (Id) lookahead;     // id matches in both id=E and E
            assId = id; //save id before it is modified by other methods
            System.out.print(id.lexeme);
            match(Tag.ID);
            if (lookahead.tag == '=') {  //implies the production is id=E
                match('=');
                stackMachine.addToMachine(Float.toString(id.value)); //pass the id to the stack
                exprn = E(); // returns the node of nonterminal Expr
                node = getNode(getLeaf(assId), exprn, "="); //3AC for assignment
                stackMachine.addToMachine("=");   // pass the = to the stack
                id.value = stackMachine.getTop();
                System.out.print("=");
            } else {              //production is E
                topToken--;    //have to go back
                lookahead = symbols.get(topToken);
                node = E();
            }
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
    }

    public AbsNode E() throws IOException {
        AbsNode node = null;
        AbsNode termnode = null;
        if (lookahead.tag == Tag.ID || lookahead.tag == '(' || lookahead.tag == Tag.NUM) {
            termnode = T();
            node = E1(termnode);
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
        return node;
    }

    public AbsNode E1(AbsNode termnodeinh) throws IOException {
        AbsNode node; // node which rerpesents the operation so far
        AbsNode snode;  // synthesised attribute which gives the full answer
        AbsNode pretn = termnodeinh; //previous T nonterminal is inherited
        AbsNode curtn;
        if (lookahead.tag == '+') {
            match('+');
            curtn = T();
            stackMachine.addToMachine("+");
            System.out.print("+");
            node = getNode(pretn, curtn, "+");
            snode = E1(node);
        } else {
            snode = termnodeinh;
        }
        return snode;  // return the final answer to the highest parent recursively
    }

    public AbsNode T() throws IOException {
        AbsNode node = null;
        AbsNode factnode = null;
        if (lookahead.tag == Tag.ID || lookahead.tag == '(' || lookahead.tag == Tag.NUM) {
            factnode = F(); //same as E
            node = T1(factnode); //same as E
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
        return node;
    }

    public AbsNode T1(AbsNode factnodeinh) throws IOException {
        AbsNode node; // node which rerpesents the operation so far
        AbsNode snode;  // synthesised attribute which gives the full answer
        AbsNode prefn = factnodeinh; //previous factor nonterminal is inherited
        AbsNode curfn;
        if (lookahead.tag == '*') {
            match('*');
            curfn = F();
            stackMachine.addToMachine("*");
            System.out.print("*");

            node = getNode(prefn, curfn, "*");
            snode = T1(node);
        } else {
            snode = factnodeinh;
        }
        return snode; // return the final answer to the highest parent recursively
    }

    public AbsNode F() throws IOException {
        AbsNode node = null;
        if (lookahead.tag == '(') {
            match('(');
            node = E();
            match(')');
        } else if (lookahead.tag == Tag.NUM) {
            num = (Num) lookahead;
            match(Tag.NUM);
            stackMachine.addToMachine(num.lexeme);
            System.out.print(num.lexeme);
            node = getLeaf(num);// changed
        } else if (lookahead.tag == Tag.ID) {
            id = (Id) lookahead;
            match(Tag.ID);
            stackMachine.addToMachine(Float.toString(id.value));
            System.out.print(id.lexeme);
            node = getLeaf(id);// changed
        } else {
            throw new Error("syntax error at line " + lex.line);
        }
        return node;
    }

    public void match(int tagType) throws IOException {
        if (lookahead.tag == tagType && symbols.size() == topToken + 1) {
            lookahead = lex.scan();
            symbols.add(lookahead);
            topToken++;
        } else if (lookahead.tag == tagType && symbols.size() > topToken + 1) {
            topToken++;
            lookahead = symbols.get(topToken);
        } else {
            throw new Error("syntax error at line " + lex.line + "\n expected " + (char) tagType + " received " + (char) lookahead.tag);
        }
    }

    public Node getNode(AbsNode l, AbsNode r, String op) throws IOException {
        Node n = null, temp;
        for (int i = 0; i < AbsNode.used.size(); i++) {
            if(AbsNode.used.get(i) instanceof  Node){
                n = (Node) AbsNode.used.get(i);
                if (n.op.equals(op) && l == n.left && r == n.right) {
                    return n;
                }
            }
        }
        if (!op.equals("=")) { // widen is done for both operands if not assignment
            if (widen(l, typeMax(l, r))) {
                AbsNode.statVal++;
                l = new Node("(float)", l, null); //make a unary op for casting
                l.type = "float";
                toIncCode(l, bw); //generate code for temporary
            }
            if (widen(r, typeMax(l, r))) {
                AbsNode.statVal++;
                r = new Node("(float)", r, null); //make a unary op for casting
                r.type = "float";
                toIncCode(r, bw); //generate code for temporary
            }
        } else { // if assignment only right side is updated
            if (widen(r, typeMax(l, r))) {
                AbsNode.statVal++;
                r = new Node("(float)", r, null); //make a unary op for casting
                r.type = "float";
                toIncCode(r, bw); //generate code for temporary
            }
        }
        AbsNode.statVal++; //give a new number to the temperory
        n = new Node(op, l, r);
        if (!n.op.equals("=")) {
            n.type = typeMax(l, r);  // in assignment we can't change type
        }
        toIncCode(n, bw);
        AbsNode.used.add(n);
        return n;
    }

    public Leaf getLeaf(Token token) throws IOException {
        Leaf l;
        for (int i = 0; i < AbsNode.used.size(); i++) {
            if(AbsNode.used.get(i) instanceof Leaf){
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


    private void toIncCode(AbsNode inNode, BufferedWriter bw) throws IOException {
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
                if(n.left instanceof Leaf){
                    l = (Leaf) n.left;
                    if (l.token.tag == Tag.ID) {
                        id = (Id) l.token;
                        leftstr = id.lexeme;
                    } else {
                        num = (Num) l.token;
                        leftstr = num.lexeme;
                    }
                }
                if(n.right instanceof Leaf){
                    l = (Leaf) n.right;
                    if (l.token.tag == Tag.ID) {
                        id = (Id) l.token;
                        rightstr = id.lexeme;
                    } else {
                        num = (Num) l.token;
                        rightstr = num.lexeme;
                    }
                }

                bw.write("t" + n.value + "= " + leftstr + n.op + rightstr);

            } else {  //an assignment
                l = (Leaf) n.left;
                assId = (Id) l.token; // left of assignment is definitely id

                if (assId.type.equals("int") && n.right.type.equals("float")) {
                    throw new Error("narrowing conversion");
                }
                if(!(n.right instanceof Leaf)){
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
                bw.write(assId.lexeme + "= " + rightstr);
            }
            bw.newLine();
        } else {
            if(n.left instanceof Leaf){
                l = (Leaf) n.left;
                bw.write("t" + n.value + "= " + n.op + l.token.lexeme);
            } else {
                bw.write("t" + n.value + "= " + n.op + "t" + n.left.value);
            }
            bw.newLine();
        }
    }

    private boolean widen(AbsNode n, String maxtype) throws IOException {//returns true if a widening conversion should be done
        if (maxtype.equals(n.type)) return false;
        else {
            return true;
        }
    }

    private String typeMax(AbsNode l, AbsNode r) {
        String max = "int";
        if (r.type.equals("float") || l.type.equals("float")) {
            max = "float";
        }
        return max;
    }
}
