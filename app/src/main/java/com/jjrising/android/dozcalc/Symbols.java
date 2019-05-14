package com.jjrising.android.dozcalc;

import java.util.HashMap;

interface SymbolCode {
}

class Symbols {

    static char getCharacter(int i) {
        if (i < 0 || i > 11) {
            return 'z';
        } else if (i < 10) {
            return (char) (i + 48);
        } else if (i == 10) {
            return 'X';
        } else { // i == 11
            return 'E';
        }
    }

    static HashMap<String, SymbolCode> tagMap = new HashMap<String, SymbolCode>() {{
        put("d/0", Digit.value.d0);
        put("d/1", Digit.value.d1);
        put("d/2", Digit.value.d2);
        put("d/3", Digit.value.d3);
        put("d/4", Digit.value.d4);
        put("d/5", Digit.value.d5);
        put("d/6", Digit.value.d6);
        put("d/7", Digit.value.d7);
        put("d/8", Digit.value.d8);
        put("d/9", Digit.value.d9);
        put("d/X", Digit.value.dX);
        put("d/E", Digit.value.dE);
        put("d/.", Digit.value.DOT);

        put("special/pi", Numeral.specials.PI);
        put("special/eulersNum", Numeral.specials.EULERS_NUM);

        put("o/+", Operator.operator.ADD);
        put("o/-", Operator.operator.SUBTRACT);
        put("o/*", Operator.operator.MULTIPLY);
        put("o//", Operator.operator.DIVIDE);
        put("o/^", Operator.operator.EXPONENT);

        put("f/sin", Function.function.SIN);
        put("f/cos", Function.function.COS);
        put("f/tan", Function.function.TAN);
        put("f/arcsin", Function.function.ARCSIN);
        put("f/arccos", Function.function.ARCCOS);
        put("f/arctan", Function.function.ARCTAN);
        put("f/sqrt", Function.function.SQRT);
        put("f/!", Function.function.FACTORIAL);
        put("f/square", Function.function.SQUARE);
        put("f/ln", Function.function.LN);
        put("f/logx", Function.function.LOGX);
        put("f/log10", Function.function.LOG10);
        put("f/logz", Function.function.LOGZ);
    }};

    static HashMap<SymbolCode, String> symMap = new HashMap<SymbolCode, String>() {{
        put(Digit.value.d0, "0");
        put(Digit.value.d1, "1");
        put(Digit.value.d2, "2");
        put(Digit.value.d3, "3");
        put(Digit.value.d4, "4");
        put(Digit.value.d5, "5");
        put(Digit.value.d6, "6");
        put(Digit.value.d7, "7");
        put(Digit.value.d8, "8");
        put(Digit.value.d9, "9");
        put(Digit.value.dX, "X");
        put(Digit.value.dE, "E");
        put(Digit.value.DOT, ".");

        put(Operator.operator.ADD, "+");
        put(Operator.operator.SUBTRACT, "+");
        put(Operator.operator.MULTIPLY, "+");
        put(Operator.operator.DIVIDE, "+");
        put(Operator.operator.EXPONENT, "+");

        put(Paren.direction.OPEN, "(");
        put(Paren.direction.CLOSE, ")");

        put(Numeral.specials.EULERS_NUM, "e");
        put(Numeral.specials.PI, "pi");

        put(Function.function.SQRT, "sqrt");
        put(Function.function.FACTORIAL, "!");
        put(Function.function.SIN, "sin");
        put(Function.function.COS, "cos");
        put(Function.function.TAN, "tan");
    }};
}
