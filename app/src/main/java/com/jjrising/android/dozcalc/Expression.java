package com.jjrising.android.dozcalc;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

class Expression {
    private ArrayList<ExpressionElement> express;

    Expression() {
        express = new ArrayList<>();
    }

    Expression(ArrayList<ExpressionElement> express) {
        this.express = express;
    }

    Expression(String str) throws StringException {
        express = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (char ch : str.toCharArray()) {
            builder.append(ch);
            if (builder.toString().matches("[XE\\d.]")) {
                express.add(Digit.fromString(builder.toString()));
            } else if (builder.toString().matches("[+\\-*/^]")) {
                express.add(Operator.fromString(builder.toString()));
            } else if (builder.toString().matches("[!]|sqrt|sin|cos|tan")) {
                express.add(Function.fromString(builder.toString()));
            } else if (builder.toString().matches("pi|e")) {
                express.add(Numeral.fromString(builder.toString()));
            } else if (builder.toString().matches("[()]")) {
                if (builder.toString().equals("("))
                    express.add(new OpenParen());
                else // builder.equals(")")
                    express.add(new CloseParen());
            } else {
                continue;
            }
            builder = new StringBuilder(); // clear the string builder.
        }
    }

    void add(ExpressionElement a) {
        express.add(a);
    }

    void back() {
        if (express.size() != 0)
            express.remove(express.size() - 1);
    }

    void remove(int index) {
        express.remove(index);
    }

    void clear() {
        express.clear();
    }

    String getText() {
        StringBuilder builder = new StringBuilder();
        int length = express.size();
        for (int i = 0; i < length; i++) {
            ExpressionElement el = express.get(i);
            String sym = el.getSymbol();
            builder.append(sym);
        }
        return builder.toString();
    }

    Numeral calc() throws CalculationError {
        //Check for empty expression
        if (express.size() == 0) {
            return new Numeral(0);
        }

        //Combine digits into Numerals
        ArrayList<Digit> numBuilder = new ArrayList<>();
        ListIterator<ExpressionElement> iter = express.listIterator();
        while (iter.hasNext()) {
            ExpressionElement el = iter.next();
            if (el.getType() == ExpressionElement.type.DIGIT) {
                numBuilder.add((Digit) el);
                iter.remove();
            } else {
                if (!numBuilder.isEmpty()) {
                    try {
                        iter.previous();
                        iter.add(new Numeral(numBuilder));
                        iter.next();
                        numBuilder.clear();
                    } catch (NumberException e) {
                        throw new CalculationError(e.getMessage());
                    }
                } // else do nothing
            }
        }
        if (!numBuilder.isEmpty()) {
            try {
                iter.add(new Numeral(numBuilder));
                numBuilder.clear();
            } catch (NumberException e) {
                throw new CalculationError(e.getMessage());
            }
        }

        ArrayList<ExpressionElement> calcQueue = new ArrayList<>();
        ArrayList<ExpressionElement> opStack = new ArrayList<>();

        // produce the calcQueue
        ExpressionElement el;
        while (!express.isEmpty()) {
            el = express.remove(0);
            ExpressionElement.type type = el.getType();
            if (type == ExpressionElement.type.NUMBER) {
                calcQueue.add(el);
            } else if (type == ExpressionElement.type.FUNCTION) {
                if (((Function) el).associativity() == Function.associativity.RIGHT) {
                    opStack.add(el);
                } else {
                    calcQueue.add(el);
                }
            } else if (type == ExpressionElement.type.OPERATOR) {
                if (opStack.isEmpty()) {
                    opStack.add(el);
                } else {
                    ExpressionElement top = opStack.get(opStack.size() - 1);
                    while (top.getType() != ExpressionElement.type.PARENTHESES
                            && (
                            top.getType() == ExpressionElement.type.FUNCTION
                                    || ((Operator) top).precedence() > ((Operator) el).precedence()
                                    || ((Operator) top).precedence() == ((Operator) el).precedence()
                                    && ((Operator) top).associativity() == Operator.associativity.LEFT)) {
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
                        if (top.getType() == ExpressionElement.type.PARENTHESES) {
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
            try {
                el = calcIterator.next();
                ExpressionElement.type type = el.getType();
                if (type == ExpressionElement.type.OPERATOR) {
                    calcIterator.remove();
                    Numeral b = (Numeral) calcIterator.previous();
                    calcIterator.remove();
                    Numeral a = (Numeral) calcIterator.previous();
                    ((Operator) el).run(a, b);
                    calcIterator.next();
                    size -= 2;
                } else if (type == ExpressionElement.type.FUNCTION) {
                    calcIterator.remove();
                    Numeral a = (Numeral) calcIterator.previous();
                    ((Function) el).run(a);
                    calcIterator.next();
                    size -= 1;
                }
            } catch (NoSuchElementException e) {
                throw new CalculationError("Missing a Numeral somewhere.");
            }
        }
        // There should only be 1 OldSymbolCode remaining in the ListIterator and it should be
        // a Numeral.
        if (size != 1) {
            throw new CalculationError(
                    "Not all elements were handled properly. Likely an invalid input");
        }
        ExpressionElement ret = calcIterator.previous();
        if (ret.getType() != ExpressionElement.type.NUMBER) {
            throw new CalculationError(
                    "Remaining OldSymbolCode was an operator. Likely an invalid input");
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