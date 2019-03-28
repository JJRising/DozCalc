package com.jjrising.android.dozcalc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpressionTests {
    @Test
    public void simpleExpressions() {
        Expression add = new Expression();
        add.add(new Numeral(3));
        add.add(new Operator(Operator.ADD));
        add.add(new Numeral(2));
        assertEquals(5, add.calc().getValue(), 0);
        Expression sub = new Expression();
        sub.add(new Numeral(3));
        sub.add(new Operator(Operator.SUBTRACT));
        sub.add(new Numeral(2));
        assertEquals(1, sub.calc().getValue(), 0);
        Expression mult = new Expression();
        mult.add(new Numeral(3));
        mult.add(new Operator(Operator.MULTIPLY));
        mult.add(new Numeral(2));
        assertEquals(6, mult.calc().getValue(), 0);
        Expression divide = new Expression();
        divide.add(new Numeral(3));
        divide.add(new Operator(Operator.DIVIDE));
        divide.add(new Numeral(2));
        assertEquals(1.5, divide.calc().getValue(), 0);
    }

    @Test
    public void level2Expressions() {
        Expression ex = new Expression();
        ex.add(new Numeral(12));
        ex.add(new Operator(Operator.SUBTRACT));
        ex.add(new Numeral(3));
        ex.add(new Operator(Operator.MULTIPLY));
        ex.add(new Numeral(8));
        ex.add(new Operator(Operator.DIVIDE));
        ex.add(new Numeral(2));
        ex.add(new Operator(Operator.ADD));
        ex.add(new Numeral(1));
        assertEquals(1, ex.calc().getValue(), 0);
    }
}
