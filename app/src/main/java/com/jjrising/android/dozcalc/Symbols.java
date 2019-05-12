package com.jjrising.android.dozcalc;

import com.google.common.collect.ImmutableBiMap;

class Symbols {

    static ImmutableBiMap<Operator.operator, String> opStringMap
            = new ImmutableBiMap.Builder<Operator.operator, String>()
            .put(Operator.operator.ADD, "+")
            .put(Operator.operator.SUBTRACT, "-")
            .put(Operator.operator.MULTIPLY, "*")
            .put(Operator.operator.DIVIDE, "/")
            .put(Operator.operator.EXPONENT, "^")
            .build();

    static ImmutableBiMap<Function.function, String> funcStringMap
            = new ImmutableBiMap.Builder<Function.function, String>()
            .put(Function.function.SQRT, "sqrt")
            .put(Function.function.FACTORIAL, "!")
            .put(Function.function.SIN, "sin")
            .put(Function.function.COS, "cos")
            .put(Function.function.TAN, "tan")
            .build();

    static ImmutableBiMap<String, Double> specialStringMap
            = new ImmutableBiMap.Builder<String, Double>()
            .put("pi", Math.PI)
            .put("e", Math.E)
            .build();

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
}
