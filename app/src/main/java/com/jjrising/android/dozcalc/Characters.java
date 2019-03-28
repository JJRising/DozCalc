package com.jjrising.android.dozcalc;

class Characters {
    static final int DOT = 12;
    static final int OPERATOR_LOWER_LIMIT = 20;
    static final int OPERATOR_ADD = 20;
    static final int OPERATOR_SUBTRACT = 21;
    static final int OPERATOR_MULTIPLY = 22;
    static final int OPERATOR_DIVIDE = 23;

    static char getCharacter(int num) {
        if (num < 0)
            return '~';
        if (num < 10)
            return (char) (num + '0');
        else if (num == 10)
            return 'X';
        else if (num == 11)
            return 'E';
        else if (num == DOT)
            return '.';
        else if (num == OPERATOR_ADD)
            return '+';
        else if (num == OPERATOR_SUBTRACT)
            return '-';
        else if (num == OPERATOR_MULTIPLY)
            return '*';
        else if (num == OPERATOR_DIVIDE)
            return '/';
        return '`';
    }
}
