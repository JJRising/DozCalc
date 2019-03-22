package com.jjrising.android.dozcalc;

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

class Number extends ExpressionElement {
    double value;

    Number(double value) {
        super(NUMBER);
        this.value = value;
    }
}

class Operator extends ExpressionElement {
    static final int ADD = 0;
    static final int SUBTRACT = 1;
    static final int MULTIPLY = 2;
    static final int DIVIDE = 3;
    static final Boolean LEFT = false;
    static final Boolean RIGHT = true;

    int value;
    int precedence;
    Boolean associativity; // True: Right associative

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