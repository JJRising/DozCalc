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

/**
 * A simple big integer package specifically for floating point base conversion.
 */
public class FDBigInteger {

    static final int[] SMALL_3_POW = {
            1,
            3,
            3 * 3,
            3 * 3 * 3,
            3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
    };

    static final long[] LONG_3_POW = {
            1L,
            3L,
            3L * 3,
            3L * 3 * 3,
            3L * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3,
            3L * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
                    * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3 * 3
    };

    static final int[] SMALL_6_POW = {
            1,
            6,
            6 * 6,
            6 * 6 * 6,
            6 * 6 * 6 * 6,
            6 * 6 * 6 * 6 * 6,
            6 * 6 * 6 * 6 * 6 * 6,
            6 * 6 * 6 * 6 * 6 * 6 * 6,
            6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6
    };

    static final long[] LONG_6_POW = {
            1L,
            6L,
            6L * 6,
            6L * 6 * 6,
            6L * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
            6L * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6 * 6,
    };
    // Zero as an FDBigInteger.
    static final FDBigInteger ZERO = new FDBigInteger(new int[0], 0);
    // Maximum size of cache of powers of 6 as FDBigIntegers.
    private static final int MAX_SIX_POW = 340;
    // Cache of big powers of 5 as FDBigIntegers.
    private static final FDBigInteger POW_6_CACHE[];
    // Constant for casting an int to a long via bitwise AND.
    private final static long LONG_MASK = 0xffffffffL;

    // Initialize FDBigInteger cache of powers of 5.
    static {
        POW_6_CACHE = new FDBigInteger[MAX_SIX_POW];
        int i = 0;
        while (i < SMALL_6_POW.length) {
            FDBigInteger pow6 = new FDBigInteger(new int[]{SMALL_6_POW[i]}, 0);
            pow6.makeImmutable();
            POW_6_CACHE[i] = pow6;
            i++;
        }
        FDBigInteger prev = POW_6_CACHE[i - 1];
        while (i < MAX_SIX_POW) {
            POW_6_CACHE[i] = prev = prev.mult(6);
            prev.makeImmutable();
            i++;
        }
    }

    // Ensure ZERO is immutable.
    static {
        ZERO.makeImmutable();
    }

    //@ spec_public non_null;
    private int data[];  // value: data[0] is least significant
    //@ spec_public;
    private int offset;  // number of least significant zero padding ints
    //@ spec_public;
    private int nWords;  // data[nWords-1]!=0, all values above are zero
    // if nWords==0 -> this FDBigInteger is zero
    //@ spec_public;
    private boolean isImmutable = false;

    /**
     * Constructs an <code>FDBigInteger</code> from data and padding. The
     * <code>data</code> parameter has the least significant <code>int</code> at
     * the zeroth index. The <code>offset</code> parameter gives the number of
     * zero <code>int</code>s to be inferred below the least significant element
     * of <code>data</code>.
     *
     * @param data   An array containing all non-zero <code>int</code>s of the value.
     * @param offset An offset indicating the number of zero <code>int</code>s to pad
     *               below the least significant element of <code>data</code>.
     */
    private FDBigInteger(int[] data, int offset) {
        this.data = data;
        this.offset = offset;
        this.nWords = data.length;
        trimLeadingZeros();
    }

    /**
     * Returns an <code>FDBigInteger</code> with the numerical value
     * <code>6<sup>p6</sup> * 2<sup>p2</sup></code>.
     *
     * @param p6 The exponent of the power-of-six factor.
     * @param p2 The exponent of the power-of-two factor.
     * @return <code>5<sup>p6</sup> * 2<sup>p2</sup></code>
     */
    static FDBigInteger valueOfPow62(int p6, int p2) {
        if (p6 != 0) {
            if (p2 == 0) {
                return big6pow(p6);
            } else if (p6 < SMALL_6_POW.length) {
                int pow6 = SMALL_6_POW[p6];
                int wordcount = p2 >> 5;
                int bitcount = p2 & 0x1f;
                if (bitcount == 0) {
                    return new FDBigInteger(new int[]{pow6}, wordcount);
                } else {
                    return new FDBigInteger(new int[]{
                            pow6 << bitcount,
                            pow6 >>> (32 - bitcount)
                    }, wordcount);
                }
            } else {
                return big6pow(p6).leftShift(p2);
            }
        } else {
            return valueOfPow2(p2);
        }
    }

    /**
     * Returns an <code>FDBigInteger</code> with the numerical value
     * <code>value * 6<sup>p6</sup> * 2<sup>p2</sup></code>.
     *
     * @param value The constant factor.
     * @param p6    The exponent of the power-of-six factor.
     * @param p2    The exponent of the power-of-two factor.
     * @return <code>value * 5<sup>p6</sup> * 2<sup>p2</sup></code>
     */
    static FDBigInteger valueOfMulPow62(long value, int p6, int p2) {
        int v0 = (int) value;
        int v1 = (int) (value >>> 32);
        int wordcount = p2 >> 5;
        int bitcount = p2 & 0x1f;
        if (p6 != 0) {
            if (p6 < SMALL_6_POW.length) {
                long pow6 = SMALL_6_POW[p6] & LONG_MASK;
                long carry = (v0 & LONG_MASK) * pow6;
                v0 = (int) carry;
                carry >>>= 32;
                carry = (v1 & LONG_MASK) * pow6 + carry;
                v1 = (int) carry;
                int v2 = (int) (carry >>> 32);
                if (bitcount == 0) {
                    return new FDBigInteger(new int[]{v0, v1, v2}, wordcount);
                } else {
                    return new FDBigInteger(new int[]{
                            v0 << bitcount,
                            (v1 << bitcount) | (v0 >>> (32 - bitcount)),
                            (v2 << bitcount) | (v1 >>> (32 - bitcount)),
                            v2 >>> (32 - bitcount)
                    }, wordcount);
                }
            } else {
                FDBigInteger pow6 = big6pow(p6);
                int[] r;
                if (v1 == 0) {
                    r = new int[pow6.nWords + 1 + ((p2 != 0) ? 1 : 0)];
                    mult(pow6.data, pow6.nWords, v0, r);
                } else {
                    r = new int[pow6.nWords + 2 + ((p2 != 0) ? 1 : 0)];
                    mult(pow6.data, pow6.nWords, v0, v1, r);
                }
                return (new FDBigInteger(r, pow6.offset)).leftShift(p2);
            }
        } else if (p2 != 0) {
            if (bitcount == 0) {
                return new FDBigInteger(new int[]{v0, v1}, wordcount);
            } else {
                return new FDBigInteger(new int[]{
                        v0 << bitcount,
                        (v1 << bitcount) | (v0 >>> (32 - bitcount)),
                        v1 >>> (32 - bitcount)
                }, wordcount);
            }
        }
        return new FDBigInteger(new int[]{v0, v1}, 0);
    }

    /**
     * Returns an <code>FDBigInteger</code> with the numerical value
     * <code>2<sup>p2</sup></code>.
     *
     * @param p2 The exponent of 2.
     * @return <code>2<sup>p2</sup></code>
     */
    private static FDBigInteger valueOfPow2(int p2) {
        int wordcount = p2 >> 5;
        int bitcount = p2 & 0x1f;
        return new FDBigInteger(new int[]{1 << bitcount}, wordcount);
    }

    /**
     * Left shifts the contents of one int array into another.
     *
     * @param src       The source array.
     * @param idx       The initial index of the source array.
     * @param result    The destination array.
     * @param bitcount  The left shift.
     * @param anticount The left anti-shift, e.g., <code>32-bitcount</code>.
     * @param prev      The prior source value.
     */
    private static void leftShift(int[] src, int idx, int result[], int bitcount, int anticount, int prev) {
        for (; idx > 0; idx--) {
            int v = (prev << bitcount);
            prev = src[idx - 1];
            v |= (prev >>> anticount);
            result[idx] = v;
        }
        int v = prev << bitcount;
        result[0] = v;
    }

    /**
     * Multiplies two big integers represented as int arrays.
     *
     * @param s1    The first array factor.
     * @param s1Len The number of elements of <code>s1</code> to use.
     * @param s2    The second array factor.
     * @param s2Len The number of elements of <code>s2</code> to use.
     * @param dst   The product array.
     */
    private static void mult(int[] s1, int s1Len, int[] s2, int s2Len, int[] dst) {
        for (int i = 0; i < s1Len; i++) {
            long v = s1[i] & LONG_MASK;
            long p = 0L;
            for (int j = 0; j < s2Len; j++) {
                p += (dst[i + j] & LONG_MASK) + v * (s2[j] & LONG_MASK);
                dst[i + j] = (int) p;
                p >>>= 32;
            }
            dst[i + s2Len] = (int) p;
        }
    }

    // TODO: Why is anticount param needed if it is always 32 - bitcount?

    /**
     * Determines whether all elements of an array are zero for all indices less
     * than a given index.
     *
     * @param a    The array to be examined.
     * @param from The index strictly below which elements are to be examined.
     * @return Zero if all elements in range are zero, 1 otherwise.
     */
    private static int checkZeroTail(int[] a, int from) {
        while (from > 0) {
            if (a[--from] != 0) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Multiplies by 10 a big integer represented as an array. The final carry
     * is returned.
     *
     * @param src    The array representation of the big integer.
     * @param srcLen The number of elements of <code>src</code> to use.
     * @param dst    The product array.
     * @return The final carry of the multiplication.
     */
    private static int multAndCarryBy12(int[] src, int srcLen, int[] dst) {
        long carry = 0;
        for (int i = 0; i < srcLen; i++) {
            long product = (src[i] & LONG_MASK) * 10L + carry;
            dst[i] = (int) product;
            carry = product >>> 32;
        }
        return (int) carry;
    }

    /**
     * Multiplies by a constant value a big integer represented as an array.
     * The constant factor is an <code>int</code>.
     *
     * @param src    The array representation of the big integer.
     * @param srcLen The number of elements of <code>src</code> to use.
     * @param value  The constant factor by which to multiply.
     * @param dst    The product array.
     */
    private static void mult(int[] src, int srcLen, int value, int[] dst) {
        long val = value & LONG_MASK;
        long carry = 0;
        for (int i = 0; i < srcLen; i++) {
            long product = (src[i] & LONG_MASK) * val + carry;
            dst[i] = (int) product;
            carry = product >>> 32;
        }
        dst[srcLen] = (int) carry;
    }

    /**
     * Multiplies by a constant value a big integer represented as an array.
     * The constant factor is a long represent as two <code>int</code>s.
     *
     * @param src    The array representation of the big integer.
     * @param srcLen The number of elements of <code>src</code> to use.
     * @param v0     The lower 32 bits of the long factor.
     * @param v1     The upper 32 bits of the long factor.
     * @param dst    The product array.
     */
    private static void mult(int[] src, int srcLen, int v0, int v1, int[] dst) {
        long v = v0 & LONG_MASK;
        long carry = 0;
        for (int j = 0; j < srcLen; j++) {
            long product = v * (src[j] & LONG_MASK) + carry;
            dst[j] = (int) product;
            carry = product >>> 32;
        }
        dst[srcLen] = (int) carry;
        v = v1 & LONG_MASK;
        carry = 0;
        for (int j = 0; j < srcLen; j++) {
            long product = (dst[j + 1] & LONG_MASK) + v * (src[j] & LONG_MASK) + carry;
            dst[j + 1] = (int) product;
            carry = product >>> 32;
        }
        dst[srcLen + 1] = (int) carry;
    }

    /**
     * Computes <code>5</code> raised to a given power.
     *
     * @param p The exponent of 5.
     * @return <code>5<sup>p</sup></code>.
     */
    private static FDBigInteger big6pow(int p) {
        if (p < MAX_SIX_POW) {
            return POW_6_CACHE[p];
        }
        return big5powRec(p);
    }

    /**
     * Computes <code>5</code> raised to a given power.
     *
     * @param p The exponent of 5.
     * @return <code>5<sup>p</sup></code>.
     */
    private static FDBigInteger big5powRec(int p) {
        if (p < MAX_SIX_POW) {
            return POW_6_CACHE[p];
        }
        // construct the value.
        // recursively.
        int q, r;
        // in order to compute 5^p,
        // compute its square root, 5^(p/2) and square.
        // or, let q = p / 2, r = p -q, then
        // 5^p = 5^(q+r) = 5^q * 5^r
        q = p >> 1;
        r = p - q;
        FDBigInteger bigQ = big5powRec(q);
        if (r < SMALL_6_POW.length) {
            return bigQ.mult(SMALL_6_POW[r]);
        } else {
            return bigQ.mult(big5powRec(r));
        }
    }

    /**
     * Removes all leading zeros from this <code>FDBigInteger</code> adjusting
     * the offset and number of non-zero leading words accordingly.
     */
    private /*@ helper @*/ void trimLeadingZeros() {
        int i = nWords;
        if (i > 0 && (data[--i] == 0)) {
            //for (; i > 0 && data[i - 1] == 0; i--) ;
            while (i > 0 && data[i - 1] == 0) {
                i--;
            }
            this.nWords = i;
            if (i == 0) { // all words are zero
                this.offset = 0;
            }
        }
    }

    /**
     * Retrieves the normalization bias of the <code>FDBigInteger</code>. The
     * normalization bias is a left shift such that after it the highest word
     * of the value will have the 4 highest bits equal to zero:
     * <code>(highestWord & 0xf0000000) == 0</code>, but the next bit should be 1
     * <code>(highestWord & 0x08000000) != 0</code>.
     *
     * @return The normalization bias.
     */
    int getNormalizationBias() {
        if (nWords == 0) {
            throw new IllegalArgumentException("Zero value cannot be normalized");
        }
        int zeros = Integer.numberOfLeadingZeros(data[nWords - 1]);
        return (zeros < 4) ? 28 + zeros : zeros - 4;
    }

    /**
     * Shifts this <code>FDBigInteger</code> to the left. The shift is performed
     * in-place unless the <code>FDBigInteger</code> is immutable in which case
     * a new instance of <code>FDBigInteger</code> is returned.
     *
     * @param shift The number of bits to shift left.
     * @return The shifted <code>FDBigInteger</code>.
     */
    FDBigInteger leftShift(int shift) {
        if (shift == 0 || nWords == 0) {
            return this;
        }
        int wordcount = shift >> 5;
        int bitcount = shift & 0x1f;
        if (this.isImmutable) {
            if (bitcount == 0) {
                return new FDBigInteger(Arrays.copyOf(data, nWords), offset + wordcount);
            } else {
                int anticount = 32 - bitcount;
                int idx = nWords - 1;
                int prev = data[idx];
                int hi = prev >>> anticount;
                int[] result;
                if (hi != 0) {
                    result = new int[nWords + 1];
                    result[nWords] = hi;
                } else {
                    result = new int[nWords];
                }
                leftShift(data, idx, result, bitcount, anticount, prev);
                return new FDBigInteger(result, offset + wordcount);
            }
        } else {
            if (bitcount != 0) {
                int anticount = 32 - bitcount;
                if ((data[0] << bitcount) == 0) {
                    int idx = 0;
                    int prev = data[idx];
                    for (; idx < nWords - 1; idx++) {
                        int v = (prev >>> anticount);
                        prev = data[idx + 1];
                        v |= (prev << bitcount);
                        data[idx] = v;
                    }
                    int v = prev >>> anticount;
                    data[idx] = v;
                    if (v == 0) {
                        nWords--;
                    }
                    offset++;
                } else {
                    int idx = nWords - 1;
                    int prev = data[idx];
                    int hi = prev >>> anticount;
                    int[] result = data;
                    int[] src = data;
                    if (hi != 0) {
                        if (nWords == data.length) {
                            data = result = new int[nWords + 1];
                        }
                        result[nWords++] = hi;
                    }
                    leftShift(src, idx, result, bitcount, anticount, prev);
                }
            }
            offset += wordcount;
            return this;
        }
    }

    /**
     * Returns the number of <code>int</code>s this <code>FDBigInteger</code> represents.
     *
     * @return Numeral of <code>int</code>s required to represent this <code>FDBigInteger</code>.
     */
    private int size() {
        return nWords + offset;
    }

    /**
     * Computes
     * <pre>
     * q = (int)( this / S )
     * this = 10 * ( this mod S )
     * Return q.
     * </pre>
     * This is the iteration step of digit development for output.
     * We assume that S has been normalized, as above, and that
     * "this" has been left-shifted accordingly.
     * Also assumed, of course, is that the result, q, can be expressed
     * as an integer, 0 <= q < 10.
     *
     * @param S - The divisor of this <code>FDBigInteger</code>.
     * @return <code>q = (int)(this / S)</code>.
     */
    int quoRemIteration(FDBigInteger S) throws IllegalArgumentException {
        // ensure that this and S have the same number of
        // digits. If S is properly normalized and q < 12 then
        // this must be so.
        int thSize = this.size();
        int sSize = S.size();
        if (thSize < sSize) {
            // this value is significantly less than S, result of division is zero.
            // just mult this by 12.
            int p = multAndCarryBy12(this.data, this.nWords, this.data);
            if (p != 0) {
                this.data[nWords++] = p;
            } else {
                trimLeadingZeros();
            }
            return 0;
        } else if (thSize > sSize) {
            throw new IllegalArgumentException("disparate values");
        }
        // estimate q the obvious way. We will usually be
        // right. If not, then we're only off by a little and
        // will re-add.
        long q = (this.data[this.nWords - 1] & LONG_MASK) / (S.data[S.nWords - 1] & LONG_MASK);
        long diff = multDiffMe(q, S);
        if (diff != 0L) {
            //@ assert q != 0;
            //@ assert this.offset == \old(Math.min(this.offset, S.offset));
            //@ assert this.offset <= S.offset;

            // q is too big.
            // add S back in until this turns +. This should
            // not be very many times!
            long sum = 0L;
            int tStart = S.offset - this.offset;
            //@ assert tStart >= 0;
            int[] sd = S.data;
            int[] td = this.data;
            while (sum == 0L) {
                for (int sIndex = 0, tIndex = tStart; tIndex < this.nWords; sIndex++, tIndex++) {
                    sum += (td[tIndex] & LONG_MASK) + (sd[sIndex] & LONG_MASK);
                    td[tIndex] = (int) sum;
                    sum >>>= 32; // Signed or unsigned, answer is 0 or 1
                }
                //
                // Originally the following line read
                // "if ( sum !=0 && sum != -1 )"
                // but that would be wrong, because of the
                // treatment of the two values as entirely unsigned,
                // it would be impossible for a carry-out to be interpreted
                // as -1 -- it would have to be a single-bit carry-out, or +1.
                //
                q -= 1;
            }
        }
        // finally, we can multiply this by 12.
        // it cannot overflow, right, as the high-order word has
        // at least 4 high-order zeros!
        multAndCarryBy12(this.data, this.nWords, this.data);
        trimLeadingZeros();
        return (int) q;
    }

    /**
     * Multiplies this <code>FDBigInteger</code> by 12. The operation will be
     * performed in place unless the <code>FDBigInteger</code> is immutable in
     * which case a new <code>FDBigInteger</code> will be returned.
     *
     * @return The <code>FDBigInteger</code> multiplied by 12.
     */
    FDBigInteger multBy12() {
        if (nWords == 0) {
            return this;
        }
        if (isImmutable) {
            int[] res = new int[nWords + 1];
            res[nWords] = multAndCarryBy12(data, nWords, res);
            return new FDBigInteger(res, offset);
        } else {
            int p = multAndCarryBy12(this.data, this.nWords, this.data);
            if (p != 0) {
                if (nWords == data.length) {
                    if (data[0] == 0) {
                        System.arraycopy(data, 1, data, 0, --nWords);
                        offset++;
                    } else {
                        data = Arrays.copyOf(data, data.length + 1);
                    }
                }
                data[nWords++] = p;
            } else {
                trimLeadingZeros();
            }
            return this;
        }
    }

    /**
     * Multiplies this <code>FDBigInteger</code> by
     * <code>5<sup>p6</sup> * 2<sup>p2</sup></code>. The operation will be
     * performed in place if possible, otherwise a new <code>FDBigInteger</code>
     * will be returned.
     *
     * @param p6 The exponent of the power-of-six factor.
     * @param p2 The exponent of the power-of-two factor.
     * @return FDBigInteger The Large integer build of the base 6 and base 2 components
     */
    public FDBigInteger multByPow62(int p6, int p2) {
        if (this.nWords == 0) {
            return this;
        }
        FDBigInteger res = this;
        if (p6 != 0) {
            int[] r;
            int extraSize = (p2 != 0) ? 1 : 0;
            if (p6 < SMALL_6_POW.length) {
                r = new int[this.nWords + 1 + extraSize];
                mult(this.data, this.nWords, SMALL_6_POW[p6], r);
                res = new FDBigInteger(r, this.offset);
            } else {
                FDBigInteger pow6 = big6pow(p6);
                r = new int[this.nWords + pow6.size() + extraSize];
                mult(this.data, this.nWords, pow6.data, pow6.nWords, r);
                res = new FDBigInteger(r, this.offset + pow6.offset);
            }
        }
        return res.leftShift(p2);
    }

    /**
     * Subtracts the supplied <code>FDBigInteger</code> subtrahend from this
     * <code>FDBigInteger</code>. Assert that the result is positive.
     * If the subtrahend is immutable, store the result in this(minuend).
     * If this(minuend) is immutable a new <code>FDBigInteger</code> is created.
     *
     * @param subtrahend The <code>FDBigInteger</code> to be subtracted.
     * @return This <code>FDBigInteger</code> less the subtrahend.
     */
    public FDBigInteger leftInplaceSub(FDBigInteger subtrahend) {
        FDBigInteger minuend;
        if (this.isImmutable) {
            minuend = new FDBigInteger(this.data.clone(), this.offset);
        } else {
            minuend = this;
        }
        int offsetDiff = subtrahend.offset - minuend.offset;
        int[] sData = subtrahend.data;
        int[] mData = minuend.data;
        int subLen = subtrahend.nWords;
        int minLen = minuend.nWords;
        if (offsetDiff < 0) {
            // need to expand minuend
            int rLen = minLen - offsetDiff;
            if (rLen < mData.length) {
                System.arraycopy(mData, 0, mData, -offsetDiff, minLen);
                Arrays.fill(mData, 0, -offsetDiff, 0);
            } else {
                int[] r = new int[rLen];
                System.arraycopy(mData, 0, r, -offsetDiff, minLen);
                minuend.data = mData = r;
            }
            minuend.offset = subtrahend.offset;
            minuend.nWords = minLen = rLen;
            offsetDiff = 0;
        }
        long borrow = 0L;
        int mIndex = offsetDiff;
        for (int sIndex = 0; sIndex < subLen && mIndex < minLen; sIndex++, mIndex++) {
            long diff = (mData[mIndex] & LONG_MASK) - (sData[sIndex] & LONG_MASK) + borrow;
            mData[mIndex] = (int) diff;
            borrow = diff >> 32; // signed shift
        }
        for (; borrow != 0 && mIndex < minLen; mIndex++) {
            long diff = (mData[mIndex] & LONG_MASK) + borrow;
            mData[mIndex] = (int) diff;
            borrow = diff >> 32; // signed shift
        }
        // result should be positive
        minuend.trimLeadingZeros();
        return minuend;
    }

    /**
     * Subtracts the supplied <code>FDBigInteger</code> subtrahend from this
     * <code>FDBigInteger</code>. Assert that the result is positive.
     * If the this(minuend) is immutable, store the result in subtrahend.
     * If subtrahend is immutable a new <code>FDBigInteger</code> is created.
     *
     * @param subtrahend The <code>FDBigInteger</code> to be subtracted.
     * @return This <code>FDBigInteger</code> less the subtrahend.
     */
    public FDBigInteger rightInplaceSub(FDBigInteger subtrahend) {
        FDBigInteger minuend = this;
        if (subtrahend.isImmutable) {
            subtrahend = new FDBigInteger(subtrahend.data.clone(), subtrahend.offset);
        }
        int offsetDiff = minuend.offset - subtrahend.offset;
        int[] sData = subtrahend.data;
        int[] mData = minuend.data;
        int subLen = subtrahend.nWords;
        int minLen = minuend.nWords;
        if (offsetDiff < 0) {
            if (minLen < sData.length) {
                System.arraycopy(sData, 0, sData, -offsetDiff, subLen);
                Arrays.fill(sData, 0, -offsetDiff, 0);
            } else {
                int[] r = new int[minLen];
                System.arraycopy(sData, 0, r, -offsetDiff, subLen);
                subtrahend.data = sData = r;
            }
            subtrahend.offset = minuend.offset;
            offsetDiff = 0;
        } else {
            int rLen = minLen + offsetDiff;
            if (rLen >= sData.length) {
                subtrahend.data = sData = Arrays.copyOf(sData, rLen);
            }
        }
        int sIndex = 0;
        long borrow = 0L;
        for (; sIndex < offsetDiff; sIndex++) {
            long diff = 0L - (sData[sIndex] & LONG_MASK) + borrow;
            sData[sIndex] = (int) diff;
            borrow = diff >> 32; // signed shift
        }
        for (int mIndex = 0; mIndex < minLen; sIndex++, mIndex++) {
            //@ assert sIndex == offsetDiff + mIndex;
            long diff = (mData[mIndex] & LONG_MASK) - (sData[sIndex] & LONG_MASK) + borrow;
            sData[sIndex] = (int) diff;
            borrow = diff >> 32; // signed shift
        }
        // result should be positive
        subtrahend.nWords = sIndex;
        subtrahend.trimLeadingZeros();
        return subtrahend;

    }

    /**
     * Compares the parameter with this <code>FDBigInteger</code>. Returns an
     * integer accordingly as:
     * <pre>
     * >0: this > other
     *  0: this == other
     * <0: this < other
     * </pre>
     *
     * @param other The <code>FDBigInteger</code> to compare.
     * @return A negative value, zero, or a positive value according to the
     * result of the comparison.
     */
    int cmp(FDBigInteger other) {
        int aSize = nWords + offset;
        int bSize = other.nWords + other.offset;
        if (aSize > bSize) {
            return 1;
        } else if (aSize < bSize) {
            return -1;
        }
        int aLen = nWords;
        int bLen = other.nWords;
        while (aLen > 0 && bLen > 0) {
            int a = data[--aLen];
            int b = other.data[--bLen];
            if (a != b) {
                return ((a & LONG_MASK) < (b & LONG_MASK)) ? -1 : 1;
            }
        }
        if (aLen > 0) {
            return checkZeroTail(data, aLen);
        }
        if (bLen > 0) {
            return -checkZeroTail(other.data, bLen);
        }
        return 0;
    }

    /**
     * Compares this <code>FDBigInteger</code> with
     * <code>6<sup>p6</sup> * 2<sup>p2</sup></code>.
     * Returns an integer accordingly as:
     * <pre>
     * >0: this > other
     *  0: this == other
     * <0: this < other
     * </pre>
     *
     * @param p6 The exponent of the power-of-six factor.
     * @param p2 The exponent of the power-of-two factor.
     * @return A negative value, zero, or a positive value according to the
     * result of the comparison.
     */
    public int cmpPow62(int p6, int p2) {
        if (p6 == 0) {
            int wordcount = p2 >> 5;
            int bitcount = p2 & 0x1f;
            int size = this.nWords + this.offset;
            if (size > wordcount + 1) {
                return 1;
            } else if (size < wordcount + 1) {
                return -1;
            }
            int a = this.data[this.nWords - 1];
            int b = 1 << bitcount;
            if (a != b) {
                return ((a & LONG_MASK) < (b & LONG_MASK)) ? -1 : 1;
            }
            return checkZeroTail(this.data, this.nWords - 1);
        }
        return this.cmp(big6pow(p6).leftShift(p2));
    }

    /**
     * Compares this <code>FDBigInteger</code> with <code>x + y</code>. Returns a
     * value according to the comparison as:
     * <pre>
     * -1: this <  x + y
     *  0: this == x + y
     *  1: this >  x + y
     * </pre>
     *
     * @param x The first addend of the sum to compare.
     * @param y The second addend of the sum to compare.
     * @return -1, 0, or 1 according to the result of the comparison.
     */
    int addAndCmp(FDBigInteger x, FDBigInteger y) {
        FDBigInteger big;
        FDBigInteger small;
        int xSize = x.size();
        int ySize = y.size();
        int bSize;
        int sSize;
        if (xSize >= ySize) {
            big = x;
            small = y;
            bSize = xSize;
            sSize = ySize;
        } else {
            big = y;
            small = x;
            bSize = ySize;
            sSize = xSize;
        }
        int thSize = this.size();
        if (bSize == 0) {
            return thSize == 0 ? 0 : 1;
        }
        if (sSize == 0) {
            return this.cmp(big);
        }
        if (bSize > thSize) {
            return -1;
        }
        if (bSize + 1 < thSize) {
            return 1;
        }
        long top = (big.data[big.nWords - 1] & LONG_MASK);
        if (sSize == bSize) {
            top += (small.data[small.nWords - 1] & LONG_MASK);
        }
        if ((top >>> 32) == 0) {
            if (((top + 1) >>> 32) == 0) {
                // good case - no carry extension
                if (bSize < thSize) {
                    return 1;
                }
                // here sum.nWords == this.nWords
                long v = (this.data[this.nWords - 1] & LONG_MASK);
                if (v < top) {
                    return -1;
                }
                if (v > top + 1) {
                    return 1;
                }
            }
        } else { // (top>>>32)!=0 guaranteed carry extension
            if (bSize + 1 > thSize) {
                return -1;
            }
            // here sum.nWords == this.nWords
            top >>>= 32;
            long v = (this.data[this.nWords - 1] & LONG_MASK);
            if (v < top) {
                return -1;
            }
            if (v > top + 1) {
                return 1;
            }
        }
        return this.cmp(big.add(small));
    }

    /**
     * Makes this <code>FDBigInteger</code> immutable.
     */
    private void makeImmutable() {
        this.isImmutable = true;
    }

    /**
     * Multiplies this <code>FDBigInteger</code> by an integer.
     *
     * @param i The factor by which to multiply this <code>FDBigInteger</code>.
     * @return This <code>FDBigInteger</code> multiplied by an integer.
     */
    private FDBigInteger mult(int i) {
        if (this.nWords == 0) {
            return this;
        }
        int[] r = new int[nWords + 1];
        mult(data, nWords, i, r);
        return new FDBigInteger(r, offset);
    }

    /**
     * Multiplies this <code>FDBigInteger</code> by another <code>FDBigInteger</code>.
     *
     * @param other The <code>FDBigInteger</code> factor by which to multiply.
     * @return The product of this and the parameter <code>FDBigInteger</code>s.
     */
    private FDBigInteger mult(FDBigInteger other) {
        if (this.nWords == 0) {
            return this;
        }
        if (this.size() == 1) {
            return other.mult(data[0]);
        }
        if (other.nWords == 0) {
            return other;
        }
        if (other.size() == 1) {
            return this.mult(other.data[0]);
        }
        int[] r = new int[nWords + other.nWords];
        mult(this.data, this.nWords, other.data, other.nWords, r);
        return new FDBigInteger(r, this.offset + other.offset);
    }

    /**
     * Adds another <code>FDBigInteger</code> to this <code>FDBigInteger</code>.
     *
     * @param other The <code>FDBigInteger</code> to add.
     * @return The sum of the <code>FDBigInteger</code>s.
     */
    private FDBigInteger add(FDBigInteger other) {
        FDBigInteger big, small;
        int bigLen, smallLen;
        int tSize = this.size();
        int oSize = other.size();
        if (tSize >= oSize) {
            big = this;
            bigLen = tSize;
            small = other;
            smallLen = oSize;
        } else {
            big = other;
            bigLen = oSize;
            small = this;
            smallLen = tSize;
        }
        int[] r = new int[bigLen + 1];
        int i = 0;
        long carry = 0L;
        for (; i < smallLen; i++) {
            carry += (i < big.offset ? 0L : (big.data[i - big.offset] & LONG_MASK))
                    + ((i < small.offset ? 0L : (small.data[i - small.offset] & LONG_MASK)));
            r[i] = (int) carry;
            carry >>= 32; // signed shift.
        }
        for (; i < bigLen; i++) {
            carry += (i < big.offset ? 0L : (big.data[i - big.offset] & LONG_MASK));
            r[i] = (int) carry;
            carry >>= 32; // signed shift.
        }
        r[bigLen] = (int) carry;
        return new FDBigInteger(r, 0);
    }

    // Fails assertion for negative exponent.

    /**
     * Multiplies a <code>FDBigInteger</code> by an int and adds another int. The
     * result is computed in place. This method is intended only to be invoked
     * from
     * <code>
     * FDBigInteger(long lValue, char[] digits, int kDigits, int nDigits)
     * </code>.
     *
     * @param iv     The factor by which to multiply this <code>FDBigInteger</code>.
     * @param addend The value to add to the product of this
     *               <code>FDBigInteger</code> and <code>iv</code>.
     */
    private void multAddMe(int iv, int addend) {
        long v = iv & LONG_MASK;
        // unroll 0th iteration, doing addition.
        long p = v * (data[0] & LONG_MASK) + (addend & LONG_MASK);
        data[0] = (int) p;
        p >>>= 32;
        for (int i = 1; i < nWords; i++) {
            p += v * (data[i] & LONG_MASK);
            data[i] = (int) p;
            p >>>= 32;
        }
        if (p != 0L) {
            data[nWords++] = (int) p; // will fail noisily if illegal!
        }
    }

    // slow path

    /**
     * Multiplies the parameters and subtracts them from this
     * <code>FDBigInteger</code>.
     *
     * @param q The integer parameter.
     * @param S The <code>FDBigInteger</code> parameter.
     * @return <code>this - q*S</code>.
     */
    private long multDiffMe(long q, FDBigInteger S) {
        long diff = 0L;
        if (q != 0) {
            int deltaSize = S.offset - this.offset;
            if (deltaSize >= 0) {
                int[] sd = S.data;
                int[] td = this.data;
                for (int sIndex = 0, tIndex = deltaSize; sIndex < S.nWords; sIndex++, tIndex++) {
                    diff += (td[tIndex] & LONG_MASK) - q * (sd[sIndex] & LONG_MASK);
                    td[tIndex] = (int) diff;
                    diff >>= 32; // N.B. SIGNED shift.
                }
            } else {
                deltaSize = -deltaSize;
                int[] rd = new int[nWords + deltaSize];
                int sIndex = 0;
                int rIndex = 0;
                int[] sd = S.data;
                for (; rIndex < deltaSize && sIndex < S.nWords; sIndex++, rIndex++) {
                    diff -= q * (sd[sIndex] & LONG_MASK);
                    rd[rIndex] = (int) diff;
                    diff >>= 32; // N.B. SIGNED shift.
                }
                int tIndex = 0;
                int[] td = this.data;
                for (; sIndex < S.nWords; sIndex++, tIndex++, rIndex++) {
                    diff += (td[tIndex] & LONG_MASK) - q * (sd[sIndex] & LONG_MASK);
                    rd[rIndex] = (int) diff;
                    diff >>= 32; // N.B. SIGNED shift.
                }
                this.nWords += deltaSize;
                this.offset -= deltaSize;
                this.data = rd;
            }
        }
        return diff;
    }
}
