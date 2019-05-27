package com.jjrising.android.dozcalc;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

class ExpressionElement {

    private type tp;

    ExpressionElement(type tp) {
        this.tp = tp;
    }

    type getType() {
        return tp;
    }

    String getSymbol() {
        return "null";
    }

    enum type {DIGIT, NUMBER, OPERATOR, PARENTHESES, FUNCTION}
}

class Digit extends ExpressionElement {
    private digitType val;
    private int num;
    private value[] valueReference = new value[]{
            value.d0, value.d1, value.d2, value.d3, value.d4, value.d5,
            value.d6, value.d7, value.d8, value.d9, value.dX, value.dE
    };

    private Digit(digitType val) {
        super(type.DIGIT);
        this.val = val;
        if (val == digitType.dot) {
            num = -1;
        } else {
            num = 0;
        }
    }

    private Digit(int num) {
        super(type.DIGIT);
        this.val = digitType.number;
        this.num = num;
    }

    static Digit fromString(String s) {
        switch (s) {
            case ".":
                return new Digit(digitType.dot);
            case "X":
                return new Digit(10);
            case "E":
                return new Digit(11);
            default:
                return new Digit(Integer.valueOf(s));
        }
    }

    static Digit fromTag(String tag) {
        switch (tag) {
            case "d/.":
                return new Digit(digitType.dot);
            case "d/X":
                return new Digit(10);
            case "d/E":
                return new Digit(11);
            default:
                return new Digit(Integer.valueOf(tag.substring(2)));
        }
    }

    boolean isDot() {
        return val == digitType.dot;
    }

    int getNum() throws NumberException {
        if (val == digitType.dot) {
            throw new NumberException("Accessing value of a dot");
        } else {
            return num;
        }
    }

    @Override
    String getSymbol() {
        if (val == digitType.dot) {
            return Symbols.symMap.get(value.DOT);
        } else {
            return Symbols.symMap.get(valueReference[num]);
        }
    }

    enum value implements SymbolCode {
        DOT, d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, dX, dE
    }

    enum digitType {dot, number}
}

/**
 * Class for all real number digitType used in expressions and
 * solutions.
 */
class Numeral extends ExpressionElement {
    private double value;
    private boolean exact;
    private boolean isSpecial;
    private specials symCode;

    Numeral(double value) {
        super(type.NUMBER);
        this.value = value;
        this.exact = true;
        isSpecial = false;
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
        this.exact = true;
        this.isSpecial = false;
    }

    private Numeral(specials symCode) {
        super(type.NUMBER);
        switch (symCode) {
            case EULERS_NUM:
                this.value = Math.E;
                break;
            case PI:
                this.value = Math.PI;
                break;
        }
        this.exact = false;
        this.isSpecial = true;
        this.symCode = symCode;
    }

    static Numeral fromTag(String tag) {
        switch (tag) {
            case "special/pi":
                return new Numeral(specials.PI);
            case "special/eulersNum":
                return new Numeral(specials.EULERS_NUM);
            default:
                return new Numeral(0);
        }
    }

    static Numeral fromString(String s) throws StringException {
        switch (s) {
            case "pi":
                return new Numeral(specials.PI);
            case "e":
                return new Numeral(specials.EULERS_NUM);
            default:
                throw new StringException();
        }
    }

    @Override
    String getSymbol() {
        if (isSpecial) {
            return Symbols.symMap.get(symCode);
        } else {
            return "n";
        }
    }

    double getValue() {
        return value;
    }

    void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns a string showing the value of the Numeral in dozenal.
     * <p>
     * Interesting part of the project. I am using the JDK source
     * code as a guide of how to implement the conversion of floating
     * point digitType into strings.
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
        //return isExact() ? buf.toJavaFormatString() : "~" + buf.toJavaFormatString();
    }

    enum specials implements SymbolCode {EULERS_NUM, PI}

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

    Operator(operator symCode) {
        super(type.OPERATOR);
        this.value = symCode;
        switch (symCode) {
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

    static Operator fromTag(String tag) {
        return new Operator((operator) Objects.requireNonNull(Symbols.tagMap.get(tag)));
    }

    static Operator fromString(String s) throws StringException {
        switch (s) {
            case "+":
                return new Operator(operator.ADD);
            case "-":
                return new Operator(operator.SUBTRACT);
            case "*":
                return new Operator(operator.MULTIPLY);
            case "/":
                return new Operator(operator.DIVIDE);
            case "^":
                return new Operator(operator.EXPONENT);
            default:
                throw new StringException();
        }
    }

    @Override
    String getSymbol() {
        return Symbols.symMap.get(value);
    }

    enum operator implements SymbolCode {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, EXPONENT
    }

    associativity associativity() {
        return associate;
    }

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
}

class Paren extends ExpressionElement {
    direction dir;

    Paren() {
        super(type.PARENTHESES);
    }

    boolean isOpen() {
        return dir == direction.OPEN;
    }

    enum direction implements SymbolCode {OPEN, CLOSE}
}

class OpenParen extends Paren {
    OpenParen() {
        super();
        this.dir = direction.OPEN;
    }

    @Override
    String getSymbol() {
        return Symbols.symMap.get(direction.OPEN);
    }
}

class CloseParen extends Paren {
    CloseParen() {
        super();
        this.dir = direction.CLOSE;
    }

    @Override
    String getSymbol() {
        return Symbols.symMap.get(direction.CLOSE);
    }
}

class Function extends ExpressionElement {

    // 1.0 / Math.log(12)
    private final double INVERSE_LN_10 = Double.longBitsToDouble(0x3fd9c1681970c88fL);

    private function func;
    private associativity associate;

    private Function(function func) {
        super(type.FUNCTION);
        this.func = func;
        switch (func) {
            case SQRT:
            case SIN:
            case COS:
            case TAN:
            case ARCSIN:
            case ARCCOS:
            case ARCTAN:
            case LN:
            case LOGX:
            case LOGZ:
            case LOG10:
                associate = associativity.RIGHT;
                break;
            case FACTORIAL:
            case SQUARE:
                associate = associativity.LEFT;
                break;
        }
    }

    static Function fromTag(String tag) {
        return new Function((function) Objects.requireNonNull(Symbols.tagMap.get(tag)));
    }

    static Function fromString(String s) throws StringException {
        switch (s) {
            case "!":
                return new Function(function.FACTORIAL);
            case "sqrt":
                return new Function(function.SQRT);
            case "sin":
                return new Function(function.SIN);
            case "cos":
                return new Function(function.COS);
            case "tan":
                return new Function(function.TAN);
            default:
                throw new StringException();
        }
    }

    @Override
    String getSymbol() {
        return Symbols.symMap.get(func);
    }

    enum function implements SymbolCode {
        SQRT, FACTORIAL, SIN, COS, TAN, ARCSIN, ARCCOS, ARCTAN, SQUARE,
        LN, LOGX, LOG10, LOGZ
    }

    associativity associativity() {
        return associate;
    }

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
            case ARCSIN:
                a.setValue(Math.asin(a.getValue()));
                break;
            case ARCCOS:
                a.setValue(Math.acos(a.getValue()));
                break;
            case ARCTAN:
                a.setValue(Math.atan(a.getValue()));
                break;
            case SQUARE:
                a.setValue(a.getValue() * a.getValue());
                break;
            case LN:
                a.setValue(Math.log(a.getValue()));
                break;
            case LOG10:
                a.setValue(Math.log10(a.getValue()));
                break;
            case LOGX:
                a.setValue(Math.log(a.getValue()) * INVERSE_LN_10);
        }
    }

    enum associativity {LEFT, RIGHT}
}

class NumberException extends Exception {
    NumberException(String s) {
        super(s);
    }
}

class StringException extends Exception {
    StringException() {
        super("Invalid string input.");
    }
}