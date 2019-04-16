package com.jjrising.android.dozcalc;

import com.google.common.collect.ImmutableBiMap;

class Characters {
    static final int OPERATOR_LOWER_LIMIT = 20;
    static final int FUNCTION_LOWER_LIMIT = 100;

    static final int OPERATOR_ADD = 21;
    static final int OPERATOR_SUBTRACT = 22;
    static final int OPERATOR_MULTIPLY = 23;
    static final int OPERATOR_DIVIDE = 24;
    static final int OPERATOR_EXPONENT = 25;
    static final int FUNCTION_SQRT = 101;
    static final int FUNCTION_FACTORIAL = 102;
    static final int FUNCTION_SIN = 103;
    static final int FUNCTION_COS = 104;
    static final int FUNCTION_TAN = 105;

    private static final ImmutableBiMap<Integer, String> charMap
            = new ImmutableBiMap.Builder<Integer, String>()
            .put(0, "0")
            .put(1, "1")
            .put(2, "2")
            .put(3, "3")
            .put(4, "4")
            .put(5, "5")
            .put(6, "6")
            .put(7, "7")
            .put(8, "8")
            .put(9, "9")
            .put(10, "X")
            .put(11, "E")
            .put(12, ".")
            .put(13, "(")
            .put(14, ")")
            .put(20, "=")
            .put(21, "+")
            .put(22, "-")
            .put(23, "*")
            .put(24, "/")
            .put(25, "^")
            .put(101, "sqrt")
            .put(102, "!")
            .put(103, "sin")
            .put(104, "cos")
            .put(105, "tan")
            .build();

    static char getCharacter(int num) {
        return charMap.get(num).charAt(0);
    }

    static int getInt(String str) {
        return charMap.inverse().get(str);
    }
}
