package evaluator;

import java.util.Stack;

/**
 * @author gayashan
 */
public class StackMachine {
    private Stack machine;
    private float top;

    public StackMachine() {
        this.machine = new Stack();

    }

    public Object pop(){
        return this.machine.pop();
    }

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

    public float evalMachine() {
        String s;
        while (!machine.empty()) {
            s = (String) machine.pop();
            if (s.equals("+") || s.equals("*")) {

            }
        }
        return 0;
    }

    public float getTop(){
        return this.top;
    }
}
