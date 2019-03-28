package com.jjrising.android.dozcalc;


import java.util.ArrayList;
import java.util.ListIterator;

public class inputString {
    private ArrayList<Integer> list;

    inputString() {
        list = new ArrayList<>();
    }

    inputString(String str) {
        list = new ArrayList<>();
        for (char ch : str.toCharArray()) {
            if (ch >= '0' && ch <= '9') {
                list.add(ch - 48);
            } else if (ch == 'X') {
                list.add(10);
            } else if (ch == 'E') {
                list.add(11);
            } else if (ch == '.') {
                list.add(Characters.DOT);
            } else if (ch == '+') {
                list.add(Characters.OPERATOR_ADD);
            } else if (ch == '-') {
                list.add(Characters.OPERATOR_SUBTRACT);
            } else if (ch == '*') {
                list.add(Characters.OPERATOR_MULTIPLY);
            } else if (ch == '/') {
                list.add(Characters.OPERATOR_DIVIDE);
            }
        }
    }

    void add(int in) {
        list.add(in);
    }

    Expression createExpression() {
        ArrayList<ExpressionElement> EEList = new ArrayList<>();
        ListIterator<Integer> iterator = list.listIterator();
        ArrayList<Integer> numBuilder = new ArrayList<>();
        while (iterator.hasNext()) {
            int el = iterator.next();
            if (el >= Characters.OPERATOR_LOWER_LIMIT && !numBuilder.isEmpty()) {
                EEList.add(new Numeral(numBuilder));
                numBuilder.clear();
            }
            if (el >= Characters.OPERATOR_LOWER_LIMIT)
                EEList.add(new Operator(el));
            else
                numBuilder.add(el);
        }
        if (!numBuilder.isEmpty())
            EEList.add(new Numeral(numBuilder));
        return new Expression(EEList);
    }
}
