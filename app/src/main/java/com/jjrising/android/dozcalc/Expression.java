package com.jjrising.android.dozcalc;

import java.util.ArrayList;
import java.util.ListIterator;

class Expression {
    private ArrayList<ExpressionElement> express;

    Expression() {
        express = new ArrayList<>();
    }

    Expression(ArrayList<ExpressionElement> express) {
        this.express = express;
    }

    void add(ExpressionElement a) {
        express.add(a);
    }

    Numeral calc() throws CalculationError {
        ArrayList<ExpressionElement> calcQueue = new ArrayList<>();
        ArrayList<ExpressionElement> opStack = new ArrayList<>();

        // produce the calcQueue
        ExpressionElement el;
        while (!express.isEmpty()) {
            el = express.remove(0);
            int type = el.getType();
            if (type == ExpressionElement.NUMBER) {
                calcQueue.add(el);
            } else if (type == ExpressionElement.FUNCTION) {
                if (((Function) el).associativity() == Function.RIGHT) {
                    opStack.add(el);
                } else {
                    calcQueue.add(el);
                }
            } else if (type == ExpressionElement.OPERATOR) {
                if (opStack.isEmpty()) {
                    opStack.add(el);
                } else {
                    ExpressionElement top = opStack.get(opStack.size() - 1);
                    while (top.getType() != ExpressionElement.PARENTHESES
                            && (
                            top.getType() == ExpressionElement.FUNCTION
                                    || ((Operator) top).precedence() > ((Operator) el).precedence()
                                    || ((Operator) top).precedence() == ((Operator) el).precedence()
                                    && ((Operator) top).associativity() == Operator.LEFT)) {
                        calcQueue.add(opStack.remove(opStack.size() - 1));
                        if (opStack.isEmpty()) {
                            break;
                        } else {
                            top = opStack.get(opStack.size() - 1);
                        }
                    }
                    opStack.add(el);
                }
            } else { // parentheses
                if (((Paren) el).isOpen()) { // '('
                    opStack.add(el);
                } else {  // ')'
                    if (opStack.isEmpty()) {
                        throw new CalculationError("No '(' found.");
                    }
                    while (true) {
                        ExpressionElement top = opStack.remove(opStack.size() - 1); // Peek at the top
                        if (top.getType() == ExpressionElement.PARENTHESES) {
                            break;
                        } else {
                            calcQueue.add(top);
                        }
                        if (opStack.isEmpty()) {
                            throw new CalculationError("No '(' found.");
                        }
                    }
                }
            }
        }
        // Clear the remaining operators in the opStack
        while (!opStack.isEmpty()) {
            calcQueue.add(opStack.remove(opStack.size() - 1));
        }

        // Run the calculation through the calcQueue
        ListIterator<ExpressionElement> calcIterator = calcQueue.listIterator();
        int size = calcQueue.size();
        while (calcIterator.hasNext()) {
            el = calcIterator.next();
            int type = el.getType();
            if (type == ExpressionElement.OPERATOR) {
                calcIterator.remove();
                Numeral b = (Numeral) calcIterator.previous();
                calcIterator.remove();
                Numeral a = (Numeral) calcIterator.previous();
                ((Operator) el).run(a, b);
                calcIterator.next();
                size -= 2;
            } else if (type == ExpressionElement.FUNCTION) {
                calcIterator.remove();
                Numeral a = (Numeral) calcIterator.previous();
                ((Function) el).run(a);
                calcIterator.next();
                size -= 1;
            }
        }
        // There should only be 1 element remaining in the ListIterator and it should be
        // a Numeral.
        if (size != 1) {
            throw new CalculationError(
                    "Not all elements were handled properly. Likely an invalid input");
        }
        ExpressionElement ret = calcIterator.previous();
        if (ret.getType() != ExpressionElement.NUMBER) {
            throw new CalculationError(
                    "Remaining element was an operator. Likely an invalid input");
        }
        return (Numeral) ret;
    }
}

class CalculationError extends Exception {
    String error;

    CalculationError(String s) {
        super(s);
        error = s;
    }
}