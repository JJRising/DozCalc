package com.jjrising.android.dozcalc;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ExpressionTests {
    @Test
    public void simpleExpressions() throws CalculationError {
        Expression add = new Expression();
        add.add(new Numeral(3));
        add.add(new Operator(Operator.operator.ADD));
        add.add(new Numeral(2));
        assertEquals(5, add.calc().getValue(), 0);
        Expression sub = new Expression();
        sub.add(new Numeral(3));
        sub.add(new Operator(Operator.operator.SUBTRACT));
        sub.add(new Numeral(2));
        assertEquals(1, sub.calc().getValue(), 0);
        Expression mult = new Expression();
        mult.add(new Numeral(3));
        mult.add(new Operator(Operator.operator.MULTIPLY));
        mult.add(new Numeral(2));
        assertEquals(6, mult.calc().getValue(), 0);
        Expression divide = new Expression();
        divide.add(new Numeral(3));
        divide.add(new Operator(Operator.operator.DIVIDE));
        divide.add(new Numeral(2));
        assertEquals(1.5, divide.calc().getValue(), 0);
    }

    @Test
    public void digitTests() throws CalculationError, NumberException {
        Expression add = new Expression();
        Digit d1 = new Digit("4");
        Digit d2 = new Digit(3);
        Digit d3 = new Digit(Digit.values.dot);
        Digit d4 = new Digit("6");
        Digit d5 = new Digit("X");
        Digit d6 = new Digit("1");
        Digit d7 = new Digit(".");
        Digit d8 = new Digit("E");
        ArrayList<Digit> l1 = new ArrayList<>();
        l1.add(d1);
        l1.add(d2);
        l1.add(d3);
        l1.add(d4);
        ArrayList<Digit> l2 = new ArrayList<>();
        l2.add(d5);
        l2.add(d6);
        l2.add(d7);
        l2.add(d8);
        Numeral n1 = new Numeral(l1);
        Numeral n2 = new Numeral(l2);
        add.add(n1);
        add.add(new Operator(Operator.operator.ADD));
        add.add(n2);
        assertEquals("125.5", add.calc().toString());
    }

    @Test
    public void level2Expressions() throws CalculationError {
        Expression ex = new Expression();
        ex.add(new Numeral(12));
        ex.add(new Operator(Operator.operator.SUBTRACT));
        ex.add(new Numeral(3));
        ex.add(new Operator(Operator.operator.MULTIPLY));
        ex.add(new Numeral(8));
        ex.add(new Operator(Operator.operator.DIVIDE));
        ex.add(new Numeral(2));
        ex.add(new Operator(Operator.operator.ADD));
        ex.add(new Numeral(1));
        assertEquals(1, ex.calc().getValue(), 0);
    }

    @Test
    public void inputExpressionTest() throws CalculationError {
        Expression ex1 = new Expression("23+5");
        assertEquals("28", ex1.calc().toString());

        Expression ex2 = new Expression("5.6-2*2");
        assertEquals("1.6", ex2.calc().toString());
    }

    @Test
    public void AdvancedExpressions() throws CalculationError {
        Expression ex1 = new Expression("12^2");
        assertEquals("144", ex1.calc().toString());

        Expression ex2 = new Expression("sqrt14");
        assertEquals("4", ex2.calc().toString());

        Expression ex3 = new Expression("4!");
        assertEquals("20", ex3.calc().toString());
    }
}
