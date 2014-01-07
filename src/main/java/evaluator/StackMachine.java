package evaluator;

import java.util.Stack;

/**
 * @author gayashan
 */
public class StackMachine {
    private final Stack machine;
    private float top;

    public StackMachine() {
        this.machine = new Stack();

    }

    /***
     * Returns the top of the stack
     * @return
     */
    public Object pop() {
        return this.machine.pop();
    }

    /***
     * Push a num/id to Stack, if it's an operator evaluate and push back
     * @param s
     */
    public void addToMachine(String s) {
        float op1, op2;
        if (s.equals("+")) {
            op1 = Float.parseFloat((String) machine.pop());
            op2 = Float.parseFloat((String) machine.pop());
            machine.push(Float.toString(op1 + op2));
        } else if (s.equals("*")) {
            op1 = Float.parseFloat((String) machine.pop());
            op2 = Float.parseFloat((String) machine.pop());
            machine.push(Float.toString(op1 * op2));
        } else if (s.equals("=")) {
            op1 = Float.parseFloat((String) machine.pop());
            op2 = Float.parseFloat((String) machine.pop());
            machine.push(Float.toString(op1));
            top = op1;
        } else {
            machine.push(s);
        }
    }

    /***
     * Returns the top of the stack
     * @return
     */
    public float getTop() {
        return this.top;
    }
}
