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

    Numeral calc() {
        ArrayList<ExpressionElement> calcQueue = new ArrayList<>();
        ArrayList<ExpressionElement> opStack = new ArrayList<>();

        // produce the calcQueue
        ExpressionElement el;
        while (!express.isEmpty()) {
            el = express.remove(0);
            if (el.getType() == ExpressionElement.NUMBER) {
                calcQueue.add(el);
            } else {
                if (opStack.isEmpty()) {
                    opStack.add(el);
                } else {
                    Operator top = (Operator) opStack.get(opStack.size() - 1);
                    while (top.precedence() > ((Operator) el).precedence() ||
                            (top.precedence() == ((Operator) el).precedence()
                                    && top.associativity() == Operator.LEFT)) {
                        calcQueue.add(opStack.remove(opStack.size() - 1));
                        if (opStack.isEmpty()) {
                            break;
                        } else {
                            top = (Operator) opStack.get(opStack.size() - 1);
                        }
                    }
                    opStack.add(el);
                }
            }
        }
        // Clear the remaining operators in the opStack
        while (!opStack.isEmpty()) {
            calcQueue.add(opStack.remove(opStack.size() - 1));
        }

        // Run the calculation through the calcQueue
        ListIterator<ExpressionElement> calcIterator = calcQueue.listIterator();
        while (calcIterator.hasNext()) {
            el = calcIterator.next();
            if (el.getType() == ExpressionElement.OPERATOR) {
                calcIterator.remove();
                Numeral a;
                Numeral b;
                switch (((Operator) el).op()) {
                    case Operator.ADD:
                        b = (Numeral) calcIterator.previous();
                        calcIterator.remove();
                        a = (Numeral) calcIterator.previous();
                        a.add(b);
                        calcIterator.next();
                        break;
                    case Operator.SUBTRACT:
                        b = (Numeral) calcIterator.previous();
                        calcIterator.remove();
                        a = (Numeral) calcIterator.previous();
                        a.subtract(b);
                        calcIterator.next();
                        break;
                    case Operator.MULTIPLY:
                        b = (Numeral) calcIterator.previous();
                        calcIterator.remove();
                        a = (Numeral) calcIterator.previous();
                        a.multiply(b);
                        calcIterator.next();
                        break;
                    case Operator.DIVIDE:
                        b = (Numeral) calcIterator.previous();
                        calcIterator.remove();
                        a = (Numeral) calcIterator.previous();
                        a.divide(b);
                        calcIterator.next();
                        break;
                }
            }
        }
        return (Numeral) calcIterator.previous();
    }
}
