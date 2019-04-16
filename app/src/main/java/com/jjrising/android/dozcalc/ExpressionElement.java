package com.jjrising.android.dozcalc;

import android.support.annotation.NonNull;

import java.util.ArrayList;

class ExpressionElement {
    static final int NUMBER = 0;
    static final int OPERATOR = 1;
    static final int PARENTHESES = 2;
    static final int FUNCTION = 3;

    private int type;

    ExpressionElement(int type) {
        this.type = type;
    }

    int getType() {
        return type;
    }
}

/**
 * Class for all real number values used in expressions and
 * solutions.
 */
class Numeral extends ExpressionElement {
    private double value;

    Numeral(double value) {
        super(NUMBER);
        this.value = value;
    }

    Numeral(ArrayList<Integer> builder) {
        super(NUMBER);
        boolean hasDot = builder.contains(Characters.getInt("."));
        int dotIndex;
        if (hasDot)
            dotIndex = builder.indexOf(Characters.getInt("."));
        else
            dotIndex = builder.size();
        for (int i = 0; i < dotIndex; i++) {
            int addValue = builder.get(dotIndex - i - 1);
            for (int j = 0; j < i; j++)
                addValue *= 12;
            value += addValue;
        }
        for (int i = dotIndex + 1; i < builder.size(); i++) {
            double addValue = builder.get(i);
            for (int j = 0; j < i - dotIndex; j++)
                addValue /= 12;
            value += addValue;
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
        boolean exact = buf.doubleToDoz(exp, mantissa, numberOfSignificantBits);
        return buf.toJavaFormatString();
    }
}

/**
 * Class for all the operators that can be used in an expression.
 */
class Operator extends ExpressionElement {
    static final int ADD = 0;
    static final int SUBTRACT = 1;
    static final int MULTIPLY = 2;
    static final int DIVIDE = 3;
    static final int EXPONENT = 4;
    static final boolean LEFT = false;
    static final boolean RIGHT = true;

    private int value;
    private int precedence;
    private boolean associativity; // True: Right associative

    Operator(int sym) {
        super(OPERATOR);
        switch (sym) {
            case Characters.OPERATOR_ADD:
                value = ADD;
                precedence = 2;
                associativity = LEFT;
                break;
            case Characters.OPERATOR_SUBTRACT:
                value = SUBTRACT;
                precedence = 2;
                associativity = LEFT;
                break;
            case Characters.OPERATOR_MULTIPLY:
                value = MULTIPLY;
                precedence = 3;
                associativity = LEFT;
                break;
            case Characters.OPERATOR_DIVIDE:
                value = DIVIDE;
                precedence = 3;
                associativity = LEFT;
                break;
            case Characters.OPERATOR_EXPONENT:
                value = EXPONENT;
                precedence = 4;
                associativity = RIGHT;
                break;
        }
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

    boolean associativity() {
        return associativity;
    }
}

class Paren extends ExpressionElement {
    static final boolean OPEN = true;
    static final boolean CLOSE = false;

    private boolean open;

    Paren(boolean open) {
        super(PARENTHESES);
        this.open = open;
    }

    boolean isOpen() {
        return open;
    }
}

class Function extends ExpressionElement {
    static final int SQRT = 0;
    static final int FACTORIAL = 1;
    static final int SIN = 2;
    static final int COS = 3;
    static final int TAN = 4;
    static final boolean LEFT = false;
    static final boolean RIGHT = true;

    private int func;
    private boolean associativity;

    Function(int sym) {
        super(FUNCTION);
        switch (sym) {
            case Characters.FUNCTION_SQRT:
                func = SQRT;
                associativity = RIGHT;
                break;
            case Characters.FUNCTION_FACTORIAL:
                func = FACTORIAL;
                associativity = LEFT;
                break;
            case Characters.FUNCTION_SIN:
                func = SIN;
                associativity = RIGHT;
                break;
            case Characters.FUNCTION_COS:
                func = COS;
                associativity = RIGHT;
                break;
            case Characters.FUNCTION_TAN:
                func = TAN;
                associativity = RIGHT;
                break;
        }
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
        }
    }

    boolean associativity() {
        return associativity;
    }
}