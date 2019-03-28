package com.jjrising.android.dozcalc;

import android.support.annotation.NonNull;

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
    static final Boolean LEFT = false;
    static final Boolean RIGHT = true;

    private int value;
    private int precedence;
    private Boolean associativity; // True: Right associative

    Operator(int op) {
        super(OPERATOR);
        switch (op) {
            case ADD:
                value = ADD;
                precedence = 2;
                associativity = LEFT;
                break;
            case SUBTRACT:
                value = SUBTRACT;
                precedence = 2;
                associativity = LEFT;
                break;
            case MULTIPLY:
                value = MULTIPLY;
                precedence = 3;
                associativity = LEFT;
                break;
            case DIVIDE:
                value = DIVIDE;
                precedence = 4;
                associativity = LEFT;
                break;
        }
    }
}