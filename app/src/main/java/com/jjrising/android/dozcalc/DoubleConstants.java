package com.jjrising.android.dozcalc;

interface DoubleConstants {
    long SIGN_MASK = 0x8000_0000_0000_0000L;
    long EXPONENT_MASK = 0x7FF0_0000_0000_0000L;
    long MANTISSA_MASK = 0x000F_FFFF_FFFF_FFFFL;
    long MANTISSA_HOB = 0x0010_0000_0000_0000L; // The imaginary leading zero
    int EXP_SHIFT = 52;
    int EXP_BIAS = 1023;
}
