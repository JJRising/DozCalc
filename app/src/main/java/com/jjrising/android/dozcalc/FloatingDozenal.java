/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This code has been heavily modified as of March 25th, 2019 for the purpose
 * of its use in calculating floating-point values to base12 numerals. Much
 * of the original structure remains the same though many hard-coded values
 * were modified for the purposes of runtime efficiency as well as required
 * mathematical changes. Code that was unnecessary to the DozCalc project was
 * removed. I have NOT noted the inline changes due to the frequency at which
 * they occur.
 *
 * The code contained within this file is still under the terms of the GNU
 * General Public License.
 */
package com.jjrising.android.dozcalc;

import java.util.Arrays;

class FloatingDozenal {
    private static final int EXP_SHIFT = DoubleConstants.EXP_SHIFT;
    private static final long HIGH_ORDER_BIT = DoubleConstants.MANTISSA_HOB;
    private static final long EXP_ONE = 0x3FF0000000000000L;
    private static final int MAX_SMALL_EXP = 62;
    private static final int MIN_SMALL_EXP = -21; // -(63/3)
    private static final long MANTISSA_MASK = DoubleConstants.MANTISSA_MASK;
    private static final long EXP_MASK = DoubleConstants.EXPONENT_MASK;
    private static final int EXP_BIAS = DoubleConstants.EXP_BIAS;
    private static final long SIGN_MASK = DoubleConstants.SIGN_MASK;

    interface BinaryToDozConverter {

    }

    static class BinaryToDozBuffer implements BinaryToDozConverter {
        // Approximately ceil( log2( long6pow[i]))
        private static final int[] N_3_BITS = {
                0,
                2,
                4,
                5,
                7,
                8,
                10,
                12,
                13,
                15,
                16,
                18,
                20,
                21,
                23,
                24,
                26,
                27,
                29,
                31,
                32,
                34,
                35,
                37,
                39,
                40,
                42,
                43,
                45,
                46,
                48,
                50,
                51,
                53,
                54,
                56,
                58,
                59,
                61,
                62
        };
        // Approximately ceil( log2( long6pow[i]))
        private static final int[] N_6_BITS = {
                0,
                3,
                5,
                8,
                11,
                13,
                16,
                19,
                21,
                24,
                26,
                29,
                32,
                34,
                37,
                39,
                42,
                44,
                47,
                50,
                52,
                55,
                57,
                60,
                63
        };
        //TODO: How were these calculated?
        private static int[] insignificantDigitsNumber = {
                0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3,
                4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7,
                8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11,
                12, 12, 12, 12, 13, 13, 13, 14, 14, 14,
                15, 15, 15, 15, 16, 16, 16, 17, 17, 17,
                18, 18, 18, 19
        };
        private final char[] digits;
        private final char[] buffer = new char[26];
        private boolean isNegative;
        private int dozExponent;
        private int firstDigitIndex;
        private int nDigits;
        private boolean exactDecimalConversion = false;
        private boolean decimalDigitsRoundedUp = false;

        BinaryToDozBuffer() {
            this.digits = new char[26];
        }

        private static int insignificantDigitsForPow2(int p2) {
            if (p2 > 1 && p2 < insignificantDigitsNumber.length) {
                return insignificantDigitsNumber[p2];
            }
            return 0; // Meaning everything is significant? (Question)
        }

        String toJavaFormatString() {
            int len = getChars(buffer);
            return new String(buffer, 0, len);
        }

        void setSign(boolean isNegative) {
            this.isNegative = isNegative;
        }

        /**
         * @param dozExponent         - expected exponent in dozenal
         * @param lValue              - long value
         * @param insignificantDigits - number of insignificant digits
         */
        private void developLongDigits(int dozExponent, long lValue, int insignificantDigits) {
            if (insignificantDigits != 0) { // insignificantDigits is the number of binary digits we are going to ignore.
                // Discard non-significant low-order bits, while rounding,
                // up to insignificant value.
                long pow12 = FDBigInteger.LONG_3_POW[insignificantDigits] << (2 * insignificantDigits); // 12^i == 6^i * 2^i;
                long residue = lValue % pow12;
                lValue /= pow12;
                dozExponent += insignificantDigits;
                if (residue >= (pow12 >> 1)) {
                    // round up based on the low-order bits we're discarding
                    lValue++;
                }
            }
            int nDigit = digits.length - 1; // Pointer for where the next digit goes in the array
            int c; // Place holder value for the calculation of the current digit
            if (lValue <= Integer.MAX_VALUE) {
                // even easier sub-case!
                // can do int arithmetic rather than long!
                int iValue = (int) lValue;
                c = iValue % 12;
                iValue /= 12;
                while (c == 0) { // Ignoring the trailing zeros from the solution.
                    dozExponent++;
                    c = iValue % 12;
                    iValue /= 12;
                }
                while (iValue != 0) {
                    digits[nDigit--] = Characters.getCharacter(c);
                    dozExponent++;
                    c = iValue % 12;
                    iValue /= 12;
                }
                digits[nDigit] = Characters.getCharacter(c);
            } else {
                // same algorithm as above (same bugs, too )
                // but using long arithmetic.
                c = (int) (lValue % 12L);
                lValue /= 12L;
                while (c == 0) { // Ignoring the trailing zeros from the solution.
                    dozExponent++;
                    c = (int) (lValue % 12L);
                    lValue /= 12L;
                }
                while (lValue != 0L) {
                    digits[nDigit--] = Characters.getCharacter(c);
                    dozExponent++;
                    c = (int) (lValue % 12L);
                    lValue /= 12;
                }
                digits[nDigit] = Characters.getCharacter(c);
            }
            this.dozExponent = dozExponent + 1; // Variable is being re-purposed as a counter of non fractional numbers...
            this.firstDigitIndex = nDigit;
            this.nDigits = this.digits.length - nDigit;
        }

        void doubleToDoz(int exp, long mantissa, int numOfSignificantBits) {
            final int tailZeros = Long.numberOfTrailingZeros(mantissa);
            final int numOfMantissaBits = DoubleConstants.EXP_SHIFT + 1 - tailZeros;

            boolean decimalDigitsRoundedUp = false;
            boolean exactDecimalConversion = false;

            // The number of bits in the mantissa that represent fractional values. (>0 but <1)
            int fractionalBits = Math.max(0, numOfMantissaBits - exp - 1);

            // Can it be represented in a long?
            if (exp <= MAX_SMALL_EXP && exp >= MIN_SMALL_EXP) {
                // If it is a whole number:
                if (fractionalBits == 0) {
                    int insignificant;
                    if (exp > numOfSignificantBits) { // numOfSignificantBits will be 53
                        insignificant = insignificantDigitsForPow2(exp - numOfSignificantBits - 1);
                    } else {
                        insignificant = 0;
                    }
                    if (exp >= EXP_SHIFT) {
                        mantissa <<= (exp - EXP_SHIFT);
                    } else {
                        mantissa >>>= (EXP_SHIFT - exp);
                    }
                    developLongDigits(0, mantissa, insignificant);
                    return;
                }
                // So its not a whole number, time to do some hard work.
            }
            // We need to calculate the large positive integers B,S, and dozExp
            // such that:
            //      d = (B / S) * 12^dozExp
            //      1 <= B / S < 12
            // To do this:
            //      dozExp = floor( log12(d) )
            //      B = d * 2^numOfFractionalBits * 12^max(0, -dozExp)
            //          = d * 2^numOfFractionalBits * 2^max(0, -dozExp) * 6^max(0, -dozExp)
            //          = d * 2^(numberOfFractionalBits + max(0, -dozExp)) * 6^max(0, -dozExp)
            //      S = 12^max(0, dozExp) * 2^numberOfFractionalBits
            //          = 2^(numberOfFractionalBits + max(0, dozExp)) * 6^max(0, dozExp)

            int dozExp = estimateDozExp(mantissa, exp);
            int B2, B6;
            int S2, S6;
            int M2, M6;

            B6 = Math.max(0, -dozExp);
            B2 = B6 + fractionalBits + exp;

            S6 = Math.max(0, dozExp);
            S2 = S6 + fractionalBits;

            M6 = B6;
            //M2 = M6 - numOfSignificantBits;
            M2 = B2;

            //

            mantissa >>>= tailZeros;
            B2 -= numOfMantissaBits - 1;
            int common2Factor = Math.min(B2, S2);
            B2 -= common2Factor;
            S2 -= common2Factor;
            M2 -= common2Factor;

            //

            if (numOfMantissaBits == 1) {
                M2 -= 1;
            }

            if (M2 < 0) {
                B2 -= M2;
                S2 -= M2;
                M2 = 0;
            }

            //

            int nDigit = 0;
            boolean low, high;
            long lowDigitDifference;
            int q;

            int BBits = numOfMantissaBits + B2 + ((B6 < N_6_BITS.length) ? N_6_BITS[B6] : B6 * 3);

            int twelveSBits = S2 + 1 + ((S6 + 1 < N_6_BITS.length) ? N_6_BITS[S6 + 1] : (S6 + 1) * 3);
            if (BBits < 64 && twelveSBits < 64) {
                if (BBits < 32 && twelveSBits < 32) {
                    // Can use ints
                    int b = ((int) mantissa * FDBigInteger.SMALL_6_POW[B6]) << B2;
                    int s = FDBigInteger.SMALL_6_POW[S6] << S2;
                    int m = FDBigInteger.SMALL_6_POW[M6] << M2;
                    int twelves = s * 12;

                    //

                    q = b / s;
                    b = 12 * (b % s);
                    m *= 12;
                    low = b < m;
                    high = (b + m) > twelves;
                    if (q == 0 && !high) {
                        dozExp--;
                    } else {
                        digits[nDigit++] = Characters.getCharacter(q);
                    }

                    //

                    if (dozExp < -3 || dozExp > 8) {
                        high = low = false;
                    }
                    while (!low && !high) {
                        q = b / s;
                        b = 12 * (b % s);
                        m *= 12;
                        if (m > 0L) {
                            low = (b < m);
                            high = (b + m > twelves);
                        } else {
                            // hack -- m might overflow!
                            // in this case, it is certainly > b,
                            // which won't
                            // and b+m > tens, too, since that has overflowed
                            // either!
                            low = true;
                            high = true;
                        }
                        digits[nDigit++] = Characters.getCharacter(q);
                    }
                    lowDigitDifference = (b << 1) - twelves;
                    exactDecimalConversion = (b == 0);
                } else {
                    // Can use longs
                    long b = (mantissa * FDBigInteger.LONG_6_POW[B6]) << B2;
                    long s = FDBigInteger.LONG_6_POW[S6] << S2;
                    long m = FDBigInteger.LONG_6_POW[M6] << M2;
                    long twelves = s * 12L;

                    //

                    q = (int) (b / s);
                    b = 12L * (b % s);
                    m *= 12L;
                    low = b < m;
                    high = (b + m) > twelves;
                    if (q == 0 && !high) {
                        dozExp--;
                    } else {
                        digits[nDigit++] = Characters.getCharacter(q);
                    }

                    //

                    if (dozExp < -3 || dozExp > 8) {
                        high = low = false;
                    }
                    while (!low && !high) {
                        q = (int) (b / s);
                        b = 12L * (b % s);
                        m *= 12L;
                        if (m > 0L) {
                            low = (b < m);
                            high = (b + m > twelves);
                        } else {
                            // hack -- m might overflow!
                            // in this case, it is certainly > b,
                            // which won't
                            // and b+m > tens, too, since that has overflowed
                            // either!
                            low = true;
                            high = true;
                        }
                        digits[nDigit++] = Characters.getCharacter(q);
                    }
                    lowDigitDifference = (b << 1) - twelves;
                    exactDecimalConversion = (b == 0);
                }
            } else {
                FDBigInteger SVal = FDBigInteger.valueOfPow62(S6, S2);
                int shiftBias = SVal.getNormalizationBias();
                SVal = SVal.leftShift(shiftBias);

                FDBigInteger BVal = FDBigInteger.valueOfMulPow62(mantissa, B6, B2 + shiftBias);
                FDBigInteger MVal = FDBigInteger.valueOfPow62(M6 + 1, M2 + shiftBias + 1);
                FDBigInteger twelveSVal = FDBigInteger.valueOfPow62(S6 + 1, S2 + shiftBias + 1); //SVal.mult( 12 );

                q = BVal.quoRemIteration(SVal);
                low = (BVal.cmp(MVal) < 0);
                high = twelveSVal.addAndCmp(BVal, MVal) <= 0;

                if ((q == 0) && !high) {
                    // oops. Usually ignore leading zero.
                    dozExp--;
                } else {
                    digits[nDigit++] = (char) ('0' + q);
                }
                //
                // HACK! Java spec sez that we always have at least
                // one digit after the . in either F- or E-form output.
                // Thus we will need more than one digit if we're using
                // E-form
                //
                if (dozExp < -3 || dozExp >= 8) {
                    high = low = false;
                }
                while (!low && !high) {
                    q = BVal.quoRemIteration(SVal);
                    MVal = MVal.multBy12(); //MVal = MVal.mult( 10 );
                    low = (BVal.cmp(MVal) < 0);
                    high = twelveSVal.addAndCmp(BVal, MVal) <= 0;
                    digits[nDigit++] = Characters.getCharacter(q);
                }
                if (high && low) {
                    BVal = BVal.leftShift(1);
                    lowDigitDifference = BVal.cmp(twelveSVal);
                } else {
                    lowDigitDifference = 0L; // this here only for flow analysis!
                }
                exactDecimalConversion = (BVal.cmp(FDBigInteger.ZERO) == 0);
            }
            this.dozExponent = dozExp + 1;
            this.firstDigitIndex = 0;
            this.nDigits = nDigit;

            if (high) {
                if (low) {
                    if (lowDigitDifference == 0L) {
                        // it's a tie!
                        // choose based on which digits we like.
                        if ((digits[firstDigitIndex + nDigits - 1] & 1) != 0) {
                            roundup();
                        }
                    } else if (lowDigitDifference > 0) {
                        roundup();
                    }
                } else {
                    roundup();
                }
            }
        }

        private void roundup() {
            int i = (firstDigitIndex + nDigits - 1);
            int q = digits[i];
            if (q == Characters.getCharacter(11)) {
                while (q == Characters.getCharacter(11) && i > firstDigitIndex) {
                    digits[i] = Characters.getCharacter(0);
                    q = digits[--i];
                }
                if (q == Characters.getCharacter(11)) {
                    // carryout! High-order 1, rest 0s, larger exp.
                    dozExponent += 1;
                    digits[firstDigitIndex] = Characters.getCharacter(1);
                    return;
                }
                // else fall through.
            }
            digits[i] = Characters.getCharacter(q + 1);
            decimalDigitsRoundedUp = true;
        }

        /**
         * So the java source code in FloatingDecimal.java explains the estimation
         * method using a formula and then changes the numbers in the actual code
         * without any explanation. 1/1.5 = 0.666, not 0.289529654.
         *
         * <Note> Found where they got that number from. It's not included in the
         * comments but it comes from "How to Print Floating-Point Numbers Accurate"
         * by Steel and White (1990). The actual formula used is:
         * log10(d2) = log10(1.5) + (d2 - 1.5)/(1.5 * log10(1.5))
         * </Note>
         * <p>
         * I am going to estimate the exponent by taking the function:
         * y = log12(x)
         * and draw a linear line passing through the points at the x values
         * 1.5(1.3 in base 12) and 2.1(1.9 in base 12). (25% and 75% across 1.2 and
         * 2.4). It should be a much better approximation than what Steel and White
         * were using.
         * <p>
         * Scale the mantissa bits such that 1.2 <= d2 <= 2.4.
         * Then estimate that:
         * log12(d2) ~=~ 0.2256773151*d2 - 0.1753448096
         * and so we can estimate:
         * log12(d) ~=~ log12(d2) + exp * log12(2)
         *
         * @param mantissa - double float mantissa
         * @param exp      - binary exponent
         * @return - the floor of d (represents the doz exponent)
         */
        private int estimateDozExp(long mantissa, int exp) {
            double d2 = Double.longBitsToDouble(EXP_ONE | (mantissa & MANTISSA_MASK));
            // turns the mantissa into a float with the exponent of one.
            double d = 0.2256773151 * d2 - 0.1753448096 + (double) exp * 0.2789429457;
            long dBits = Double.doubleToRawLongBits(d);
            int exponent = (int) ((dBits & EXP_MASK) >> EXP_SHIFT) - EXP_BIAS;
            boolean isNegative = (dBits & SIGN_MASK) != 0;
            if (exponent >= 0 && exponent < 52) {
                long mask = MANTISSA_MASK >> exponent;
                int r = (int) (((dBits & MANTISSA_MASK) | HIGH_ORDER_BIT) >> (EXP_SHIFT - exponent));
                return isNegative ? (((mask & dBits) == 0L) ? -r : -r - 1) : r;
            } else if (exponent < 0) {
                return (((dBits & ~SIGN_MASK) == 0) ? 0 : (isNegative ? -1 : 0));
            } else { //exponent >= 52
                return (int) d;
            }
        }

        private int getChars(char[] result) {
            int i = 0;
            if (isNegative) {
                result[0] = '-';
                i = 1;
            }
            if (dozExponent > 0 && dozExponent < 8) {
                int charLength = Math.min(nDigits, dozExponent);
                System.arraycopy(digits, firstDigitIndex, result, i, charLength);
                i += charLength;
                if (charLength == dozExponent) {
                    Arrays.fill(result, i, i, '0');
                } else if (charLength < dozExponent) {
                    charLength = dozExponent - charLength;
                    Arrays.fill(result, i, i + charLength, '0');
                    i += charLength;
                } else {
                    result[i++] = '.';
                    if (charLength < nDigits) {
                        int t = nDigits - charLength;
                        System.arraycopy(digits, firstDigitIndex + charLength, result, i, t);
                        i += t;
                    } else {
                        result[i++] = '0';
                    }
                }
            } else if (dozExponent <= 0 && dozExponent > -3) {
                result[i++] = '0';
                result[i++] = '.';
                if (dozExponent != 0) {
                    Arrays.fill(result, i, i - dozExponent, '0');
                    i -= dozExponent;
                }
                System.arraycopy(digits, firstDigitIndex, result, i, nDigits);
                i += nDigits;
            } else {
                result[i++] = digits[firstDigitIndex];
                result[i++] = '.';
                if (nDigits > 1) {
                    System.arraycopy(digits, firstDigitIndex + 1, result, i, nDigits - 1);
                    i += nDigits - 1;
                } else {
                    result[i++] = '0';
                }
                result[i++] = 'E';
                int e;
                if (dozExponent <= 0) {
                    result[i++] = '-';
                    e = -dozExponent + 1;
                } else {
                    e = dozExponent - 1;
                }
                // dozExponent has 1, 2, or 3, digits
                if (e <= 11) {
                    result[i++] = (char) (e + '0');
                } else if (e <= 143) {
                    result[i++] = (char) (e / 12 + '0');
                    result[i++] = (char) (e % 12 + '0');
                } else {
                    result[i++] = (char) (e / 144 + '0');
                    e %= 144;
                    result[i++] = (char) (e / 12 + '0');
                    result[i++] = (char) (e % 12 + '0');
                }
            }
            return i;
        }
    }
}
