package inter;

/**
 * @author gayashan
 */
public class Node extends AbsNode {
    public String op;
    public AbsNode left,right;

    public Node(String op, AbsNode left,AbsNode right) {

        super.value=AbsNode.statVal;
        this.op = op;
        this.left = left;
        this.right=right;
    }
}
