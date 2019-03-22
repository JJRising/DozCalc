package com.jjrising.android.dozcalc;

public class Characters {
    static final int FUNCTION_ADD = 20;
    static final int FUNCTION_SUBTRACT = 21;
    static final int FUNCTION_MULTIPLY = 22;
    static final int FUNCTION_DIVIDE = 23;

    static char getCharacter(int num) {
        if (num < 0)
            return '~';
        if (num < 10)
            return (char) (num + '0');
        else if (num == 10)
            return 'X';
        else if (num == 11)
            return 'E';
        else if (num == FUNCTION_ADD)
            return '+';
        else if (num == FUNCTION_SUBTRACT)
            return '-';
        else if (num == FUNCTION_MULTIPLY)
            return '*';
        else if (num == FUNCTION_DIVIDE)
            return '/';
        return '`';
    }
}
