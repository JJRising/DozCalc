package com.jjrising.android.dozcalc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpressionTests {
    @Test
    public void simpleExpressions() throws CalculationError {
        Expression add = new Expression();
        add.add(new Numeral(3));
        add.add(new Operator(Characters.OPERATOR_ADD));
        add.add(new Numeral(2));
        assertEquals(5, add.calc().getValue(), 0);
        Expression sub = new Expression();
        sub.add(new Numeral(3));
        sub.add(new Operator(Characters.OPERATOR_SUBTRACT));
        sub.add(new Numeral(2));
        assertEquals(1, sub.calc().getValue(), 0);
        Expression mult = new Expression();
        mult.add(new Numeral(3));
        mult.add(new Operator(Characters.OPERATOR_MULTIPLY));
        mult.add(new Numeral(2));
        assertEquals(6, mult.calc().getValue(), 0);
        Expression divide = new Expression();
        divide.add(new Numeral(3));
        divide.add(new Operator(Characters.OPERATOR_DIVIDE));
        divide.add(new Numeral(2));
        assertEquals(1.5, divide.calc().getValue(), 0);
    }

    @Test
    public void level2Expressions() throws CalculationError {
        Expression ex = new Expression();
        ex.add(new Numeral(12));
        ex.add(new Operator(Characters.OPERATOR_SUBTRACT));
        ex.add(new Numeral(3));
        ex.add(new Operator(Characters.OPERATOR_MULTIPLY));
        ex.add(new Numeral(8));
        ex.add(new Operator(Characters.OPERATOR_DIVIDE));
        ex.add(new Numeral(2));
        ex.add(new Operator(Characters.OPERATOR_ADD));
        ex.add(new Numeral(1));
        assertEquals(1, ex.calc().getValue(), 0);
    }

    @Test
    public void inputExpressionTest() throws CalculationError {
        InputString in = new InputString("23+5");
        Expression express = in.createExpression();
        assertEquals("28", express.calc().toString());

        InputString in2 = new InputString("5.6-2*2");
        Expression express2 = in2.createExpression();
        assertEquals("1.6", express2.calc().toString());
    }

    @Test
    public void AdvancedExpressions() throws CalculationError {
        InputString in1 = new InputString("12^2");
        Expression exp = in1.createExpression();
        assertEquals("144", exp.calc().toString());

        InputString in2 = new InputString("sqrt14");
        Expression sqrt = in2.createExpression();
        assertEquals("4", sqrt.calc().toString());
    }
}
