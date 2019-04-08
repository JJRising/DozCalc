package com.jjrising.android.dozcalc;

import android.support.annotation.NonNull;

import java.util.ArrayList;

class ExpressionElement {
    static final int NUMBER = 0;
    static final int OPERATOR = 1;

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

    void add(Numeral a) {
        value += a.getValue();
    }

    void subtract(Numeral a) {
        value -= a.getValue();
    }

    void multiply(Numeral a) {
        value *= a.getValue();
    }

    void divide(Numeral a) {
        value /= a.getValue();
    }

    void exp(Numeral a) {
        value = Math.pow(value, a.value);
    }

    void sqrt() {
        value = Math.sqrt(value);
    }

    void factorial() {
        if (Double.isInfinite(value) || value == Math.floor(value)) {
            value = Double.NaN;
        } else if (value == 0.0 || value == 1.0) {
            value = 1;
        } else {
            int ret = 1;
            for (int i = 2; i <= value; i++) {
                ret *= i;
            }
            value = ret;
        }
    }

    void sin() {
        value = Math.sin(value);
    }

    void cos() {
        value = Math.cos(value);
    }

    void tan() {
        value = Math.tan(value);
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
    static final int SQRT = 5;
    static final int FACTORIAL = 6;
    static final int SIN = 7;
    static final int COS = 8;
    static final int TAN = 9;
    static final Boolean LEFT = false;
    static final Boolean RIGHT = true;

    private int value;
    private int precedence;
    private Boolean associativity; // True: Right associative

    Operator(int op) {
        super(OPERATOR);
        switch (op) {
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
            case Characters.OPERATOR_SQRT:
                value = SQRT;
                precedence = 5;
                associativity = RIGHT;
                break;
            case Characters.OPERATOR_FACTORIAL:
                value = FACTORIAL;
                precedence = 5;
                associativity = RIGHT;
                break;
            case Characters.OPERATOR_SIN:
                value = SIN;
                precedence = 5;
                associativity = RIGHT;
                break;
            case Characters.OPERATOR_COS:
                value = COS;
                precedence = 5;
                associativity = RIGHT;
                break;
            case Characters.OPERATOR_TAN:
                value = TAN;
                precedence = 5;
                associativity = RIGHT;
                break;
        }
    }

    int op() {
        return value;
    }

    int precedence() {
        return precedence;
    }

    Boolean associativity() {
        return associativity;
    }
}