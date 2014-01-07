package inter;

/**
 * @author gayashan
 */
public class IntermediateNode extends Node {
    public String op;
    public Node left, right;

    public IntermediateNode(String op, Node left, Node right) {

        super.value = Node.tempvarcount;
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
