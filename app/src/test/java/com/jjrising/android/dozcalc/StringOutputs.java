package com.jjrising.android.dozcalc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringOutputs {
    @Test
    public void numberSpecialValues() {
        //Zero (0)
        Numeral zero = new Numeral(0);
        assertEquals("0", zero.toString());

        // Negative Zero (0)
        long longNegZero = 0x8000_0000_0000_0000L;
        Numeral negZero = new Numeral(Double.longBitsToDouble(longNegZero));
        assertEquals("-0", negZero.toString());

        // Infinity (INF)
        long longInf = 0x7FF0_0000_0000_0000L;
        Numeral inf = new Numeral(Double.longBitsToDouble(longInf));
        assertEquals("INF", inf.toString());

        // Infinity (-INF)
        long longNegInf = 0xFFF0_0000_0000_0000L;
        Numeral negInf = new Numeral(Double.longBitsToDouble(longNegInf));
        assertEquals("-INF", negInf.toString());

        // Not A Numeral (NaN)
        long longNaN1 = 0x7FF0_0000_0000_0001L;
        Numeral NaN1 = new Numeral(Double.longBitsToDouble(longNaN1));
        assertEquals("NaN", NaN1.toString());
        long longNaN2 = 0x7FF8_0000_0000_0001L;
        Numeral NaN2 = new Numeral(Double.longBitsToDouble(longNaN2));
        assertEquals("NaN", NaN2.toString());
        long longNaN3 = 0x7FFF_FFFF_FFFF_FFFFL;
        Numeral NaN3 = new Numeral(Double.longBitsToDouble(longNaN3));
        assertEquals("NaN", NaN3.toString());
        long longNaN4 = 0xFFF0_0000_0000_0001L;
        Numeral NaN4 = new Numeral(Double.longBitsToDouble(longNaN4));
        assertEquals("NaN", NaN4.toString());
        long longNaN5 = 0xFFF8_0000_0000_0001L;
        Numeral NaN5 = new Numeral(Double.longBitsToDouble(longNaN5));
        assertEquals("NaN", NaN5.toString());
        long longNaN6 = 0xFFFF_FFFF_FFFF_FFFFL;
        Numeral NaN6 = new Numeral(Double.longBitsToDouble(longNaN6));
        assertEquals("NaN", NaN6.toString());
    }

    @Test
    public void lowIntegers() {
        Numeral one = new Numeral(1);
        assertEquals("1", one.toString());
        Numeral eight = new Numeral(8);
        assertEquals("8", eight.toString());
        Numeral six = new Numeral(6);
        assertEquals("6", six.toString());
        Numeral el = new Numeral(11);
        assertEquals("E", el.toString()); // Dec(11)
        Numeral nDo = new Numeral(12);
        assertEquals("10", nDo.toString()); // Dec(12)
        Numeral gro = new Numeral(144);
        assertEquals("100", gro.toString()); // Dec(144)
        Numeral mo = new Numeral(1728);
        assertEquals("1000", mo.toString()); // Dec(1728)
        Numeral twoMoElGroSevenDoDec = new Numeral(5134); // Doz(2*1000 + E*100 + 7*10 + X)
        assertEquals("2E7X", twoMoElGroSevenDoDec.toString());
        Numeral ElDoDecBiMoSevenGroElDoFiveMoNineDoFive = new Numeral(425988401);
        assertEquals("EX7E5095", ElDoDecBiMoSevenGroElDoFiveMoNineDoFive.toString());
    }

    @Test
    public void basicFractions() {
        Numeral half = new Numeral(0.5);
        assertEquals("0.6", half.toString());
        Numeral fiveEdoDecEgroEmo = new Numeral(841.0 / 1728);
        assertEquals("0.5X1", fiveEdoDecEgroEmo.toString());
    }

    @Test
    public void largeNumbers() {
        Numeral tenToTheTwelve = new Numeral(1000000000000.0);
        assertEquals("1.41981E88eE", tenToTheTwelve.toString());
        Numeral tenToTheTwenty = new Numeral(100000000000000000000.0);
        assertEquals("3.90X66958X9465e16", tenToTheTwenty.toString());
        long veryBigNumberLong = 0x7FE0_0000_0000_0000L;
        Numeral veryBigNumber = new Numeral(Double.longBitsToDouble(veryBigNumberLong));
        assertEquals("2.5e1E9", veryBigNumber.toString());
    }

    @Test
    public void hardFractions() {
        Numeral hardFraction1 = new Numeral(94. + (15158. / 20736.));
        assertEquals("7X.8932", hardFraction1.toString());
        long hardFraction2Long = 0x0010_0000_0000_0100L;
        Numeral hardFraction2 = new Numeral(Double.longBitsToDouble(hardFraction2Long));
        assertEquals("9.X1696e-1EX", hardFraction2.toString()); // TODO: Fails Testcase
    }
}
