package com.jjrising.android.dozcalc;


import java.util.ArrayList;
import java.util.ListIterator;

class InputString {
    private ArrayList<Integer> list;

    InputString() {
        list = new ArrayList<>();
    }

    InputString(String str) {
        list = new ArrayList<>();
        StringBuilder strBuilder = new StringBuilder();
        String regexPattern = "[XE\\d.+\\-*/^!]|sqrt|sin|cos|tan";
        for (char ch : str.toCharArray()) {
            // Use a regex to see if character makes a symbol
            // ch may only be part of a larger symbol such as "sin"
            strBuilder.append(Character.toString(ch));
            if (strBuilder.toString().matches(regexPattern)) {
                list.add(Characters.getInt(strBuilder.toString()));
                strBuilder = new StringBuilder(); //Clear the string builder
            }
        }
    }

    void add(int in) {
        list.add(in);
    }

    void back() {
        if (list.size() != 0) {
            list.remove(list.size() - 1);
        }
    }

    void clear() {
        list.clear();
    }

    String getText() {
        StringBuilder builder = new StringBuilder();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            builder.append(Characters.getCharacter(list.get(i)));
        }
        return builder.toString();
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
