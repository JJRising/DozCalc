package com.jjrising.android.dozcalc;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

class ExpressionElement {

    private type tp;

    ExpressionElement(type tp) {
        this.tp = tp;
    }

    type getType() {
        return tp;
    }

    //TODO: getSymbol
    String getSymbol() {
        return "S";
    }

    enum type {DIGIT, NUMBER, OPERATOR, PARENTHESES, FUNCTION}
}

class Digit extends ExpressionElement {
    private values val;
    private int num;

    Digit(values val) throws NumberException {
        super(type.DIGIT);
        if (val == values.dot) {
            this.val = val;
            num = -1;
        } else {
            throw new NumberException("Created a digit with no assigned number");
        }
    }

    Digit(int num) {
        super(type.DIGIT);
        this.val = values.number;
        this.num = num;
    }

    Digit(String s) {
        super(type.DIGIT);
        switch (s) {
            case ".":
                val = values.dot;
                break;
            case "X":
                val = values.number;
                num = 10;
                break;
            case "E":
                val = values.number;
                num = 11;
                break;
            default:
                val = values.number;
                num = Integer.parseInt(s);
                break;
        }
    }

    boolean isDot() {
        return val == values.dot;
    }

    int getNum() throws NumberException {
        if (val == values.dot) {
            throw new NumberException("Accessing value of a dot");
        } else {
            return num;
        }
    }

    @Override
    String getSymbol() {
        if (val == values.dot) {
            return Symbols.DOT;
        } else {
            return Character.toString(Symbols.getCharacter(num));
        }
    }

    enum values {dot, number}
}

/**
 * Class for all real number values used in expressions and
 * solutions.
 */
class Numeral extends ExpressionElement {
    private double value;
    private boolean exact;

    Numeral(double value) {
        super(type.NUMBER);
        this.value = value;
    }

    Numeral(ArrayList<Digit> builder) throws NumberException {
        super(type.NUMBER);
        Iterator<Digit> iter = builder.iterator();
        // Check to make sure it has at most 1 dot
        boolean hasDot = false;
        int dotIndex = -1;
        int i = 0;
        while (iter.hasNext()) {
            Digit d = iter.next();
            if (d.isDot()) {
                if (hasDot) {
                    throw new NumberException("Illegal number format (Too many dots).");
                } else {
                    hasDot = true;
                    dotIndex = i;
                }
            }
            i++;
        }
        iter = builder.iterator(); // reset the iterator;
        int size = builder.size();
        value = 0.0;
        int wholeNumberStopIndex = hasDot ? dotIndex : size;
        for (i = 0; i < wholeNumberStopIndex; i++) {
            Digit d = iter.next();
            double addValue = d.getNum();
            for (int j = 0; j < wholeNumberStopIndex - i - 1; j++) {
                addValue *= 12;
            }
            value += addValue;
        }
        if (hasDot) {
            iter.next(); // pass the dot.
            for (i = 0; i < size - dotIndex - 1; i++) {
                Digit d = iter.next();
                double addValue = d.getNum();
                for (int j = 0; j <= i; j++) {
                    addValue /= 12;
                }
                value += addValue;
            }
        }
    }

    Numeral(String str) {
        this(Symbols.specialStringMap.get(str));
    }

    double getValue() {
        return value;
    }

    void setValue(double value) {
        this.value = value;
    }

    @Override
    String getSymbol() {
        return Symbols.specialStringMap.inverse().get(value);
    }

    /**
     * Returns a string showing the value of the Numeral in dozenal.
     * <p>
     * Interesting part of the project. I am using the JDK source
     * code as a guide of how to implement the conversion of floating
     * point values into strings.
     *
     * @return - value as a string in dozenal.
     */
    @NonNull
    @Override
    public String toString() {
        // Lets handle some basic cases
        long d = Double.doubleToLongBits(value);
        boolean isNegative = (d & DoubleConstants.SIGN_MASK) != 0;
        int exp = (int) ((d & DoubleConstants.EXPONENT_MASK) >> DoubleConstants.EXP_SHIFT);
        long mantissa = d & DoubleConstants.MANTISSA_MASK;

        int numberOfSignificantBits; // Will be needed if not a trivial number
        if (exp == 2047) { // ALL 1s
            if (mantissa == 0)
                return isNegative ? "-INF" : "INF";
            else
                return "NaN";
        } else if (exp == 0) {
            if (mantissa == 0)
                return isNegative ? "-0" : "0";
            // Still a small number, but not zero
            int leadingZeros = Long.numberOfLeadingZeros(mantissa);
            int shift = leadingZeros - (63 - DoubleConstants.EXP_SHIFT);
            mantissa <<= shift;
            exp = 1 - shift;
            numberOfSignificantBits = 64 - leadingZeros;
        } else {
            mantissa |= DoubleConstants.MANTISSA_HOB;
            //numberOfSignificantBits = DoubleConstants.EXP_SHIFT + 1;
            int leadingZeros = 11;
            int tailZeros = Long.numberOfTrailingZeros(mantissa);
            numberOfSignificantBits = 64 - leadingZeros - tailZeros;
        }
        exp -= DoubleConstants.EXP_BIAS;

        FloatingDozenal.BinaryToDozBuffer buf = new FloatingDozenal.BinaryToDozBuffer();
        buf.setSign(isNegative);
        exact = buf.doubleToDoz(exp, mantissa, numberOfSignificantBits);
        return buf.toJavaFormatString();
    }

    boolean isExact() {
        return exact;
    }
}

/**
 * Class for all the operators that can be used in an expression.
 */
class Operator extends ExpressionElement {

    private operator value;
    private associativity associate;
    private int precedence;

    Operator(operator value) {
        super(type.OPERATOR);
        this.value = value;
        switch (value) {
            case ADD:
                precedence = 2;
                associate = associativity.LEFT;
                break;
            case SUBTRACT:
                precedence = 2;
                associate = associativity.LEFT;
                break;
            case MULTIPLY:
                precedence = 3;
                associate = associativity.LEFT;
                break;
            case DIVIDE:
                precedence = 3;
                associate = associativity.LEFT;
                break;
            case EXPONENT:
                precedence = 4;
                associate = associativity.RIGHT;
                break;
        }
    }

    Operator(String sym) {
        this(Symbols.opStringMap.inverse().get(sym));
    }

    associativity associativity() {
        return associate;
    }

    enum operator {ADD, SUBTRACT, MULTIPLY, DIVIDE, EXPONENT}

    void run(Numeral a, Numeral b) {
        switch (value) {
            case ADD:
                a.setValue(a.getValue() + b.getValue());
                break;
            case SUBTRACT:
                a.setValue(a.getValue() - b.getValue());
                break;
            case MULTIPLY:
                a.setValue(a.getValue() * b.getValue());
                break;
            case DIVIDE:
                a.setValue(a.getValue() / b.getValue());
                break;
            case EXPONENT:
                a.setValue(Math.pow(a.getValue(), b.getValue()));
                break;
        }
    }

    int precedence() {
        return precedence;
    }

    enum associativity {LEFT, RIGHT}

    @Override
    String getSymbol() {
        return Symbols.opStringMap.get(value);
    }
}

class Paren extends ExpressionElement {
    direction dir;

    Paren() {
        super(type.PARENTHESES);
    }

    boolean isOpen() {
        return dir == direction.OPEN;
    }

    enum direction {OPEN, CLOSE}
}

class OpenParen extends Paren {
    OpenParen() {
        super();
        this.dir = direction.OPEN;
    }

    @Override
    String getSymbol() {
        return Symbols.OPEN_PAREN;
    }
}

class CloseParen extends Paren {
    CloseParen() {
        super();
        this.dir = direction.CLOSE;
    }

    @Override
    String getSymbol() {
        return Symbols.CLOSE_PAREN;
    }
}

class Function extends ExpressionElement {

    private function func;
    private associativity associate;

    Function(function func) {
        super(type.FUNCTION);
        this.func = func;
        switch (func) {
            case SQRT:
                associate = associativity.RIGHT;
                break;
            case FACTORIAL:
                associate = associativity.LEFT;
                break;
            case SIN:
                associate = associativity.RIGHT;
                break;
            case COS:
                associate = associativity.RIGHT;
                break;
            case TAN:
                associate = associativity.RIGHT;
                break;
        }
    }

    Function(String sym) {
        this(Symbols.funcStringMap.inverse().get(sym));
    }

    associativity associativity() {
        return associate;
    }

    enum function {SQRT, FACTORIAL, SIN, COS, TAN}

    void run(Numeral a) {
        switch (func) {
            case SQRT:
                a.setValue(Math.sqrt(a.getValue()));
                break;
            case FACTORIAL:
                double value = a.getValue();
                if (Double.isInfinite(value) || value != Math.floor(value)) {
                    a.setValue(Double.NaN);
                } else if (value == 0.0 || value == 1.0) {
                    a.setValue(1.0);
                } else {
                    int ret = 1;
                    for (int i = 2; i <= value; i++) {
                        ret *= i;
                    }
                    a.setValue((double) ret);
                }
                break;
            case SIN:
                a.setValue(Math.sin(a.getValue()));
                break;
            case COS:
                a.setValue(Math.cos(a.getValue()));
                break;
            case TAN:
                a.setValue(Math.tan(a.getValue()));
                break;
        }
    }

    enum associativity {LEFT, RIGHT}

    @Override
    String getSymbol() {
        return Symbols.funcStringMap.get(func);
    }
}

class NumberException extends Exception {
    NumberException(String s) {
        super(s);
    }
}