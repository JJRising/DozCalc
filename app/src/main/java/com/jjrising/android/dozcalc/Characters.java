package com.jjrising.android.dozcalc;

import com.google.common.collect.ImmutableBiMap;

class Characters {
    static final int OPERATOR_LOWER_LIMIT = 20;
    static final int OPERATOR_ADD = 21;
    static final int OPERATOR_SUBTRACT = 22;
    static final int OPERATOR_MULTIPLY = 23;
    static final int OPERATOR_DIVIDE = 24;
    static final int OPERATOR_EXPONENT = 25;
    static final int OPERATOR_SQRT = 26;
    static final int OPERATOR_FACTORIAL = 27;
    static final int OPERATOR_SIN = 28;
    static final int OPERATOR_COS = 29;
    static final int OPERATOR_TAN = 30;

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
            .put(20, "=")
            .put(21, "+")
            .put(22, "-")
            .put(23, "*")
            .put(24, "/")
            .put(25, "^")
            .put(26, "sqrt")
            .put(27, "!")
            .put(28, "sin")
            .put(29, "cos")
            .put(30, "tan")
            .build();

    static char getCharacter(int num) {
        return charMap.get(num).charAt(0);
    }

    static int getInt(String str) {
        return charMap.inverse().get(str);
    }
}
