/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package id.ac.polibatam.mj.dcloud.math;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 *
 * @author mia (Math operation for Galois Field. Adopted from apache hadoop.)
 */
public class GFMath {

    /**
     * Default fields size. Field size 256 is good for byte based system.
     */
    public static final int DEFAULT_FIELD_SIZE = 256;

    /**
     * Default primitive polynomial. Primitive polynomial 1 + X^2 + X^3 + X^4 +
     * X^8
     */
    public static final int DEFAULT_PRIMITIVE_POLYNOMIAL = 285;
    /**
     * Cache for keeping generated GaloisField instances.
     */
    private static final Map<Integer, GFMath> INSTANCES = new HashMap<>();
    /**
     * Random generator.
     */
    private static final Random RANDOM = new SecureRandom();
    /**
     * Field size.
     */
    private final int fieldSize;
    /**
     * Primitive Period.
     */
    private final int primitivePeriod;
    /**
     * Primitive Polynomial.
     */
    private final int primitivePolynomial;
    /**
     * Addition Table.
     */
    private final int[][] addTable;
    /**
     * Addition Inverse Table.
     */
    private final int[] addInvTable;
    /**
     * Multiplication Table.
     */
    private final int[][] mulTable;
    /**
     * Multiplication Inverse Table.
     */
    private final int[] mulInvTable;
    /**
     * Power Table.
     */
    private final int[] powTable;
    /**
     * Logarithmic Table.
     */
    private final int[] logTable;

    /**
     * Constructor.
     *
     * @param fieldSize
     *            size of the field, has to be greater than 0.
     * @param primitivePolynomial
     *            a primitive polynomial corresponds to the size, has to be
     *            greater than 0.
     * @throws DcloudInvalidDataRuntimeException
     *             if any of fieldSize or primitivePolynomial are invalid.
     */
    private GFMath(final int fieldSize, final int primitivePolynomial) {

        if (fieldSize <= 0) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID fieldSize=[" + fieldSize + "]. Allowed value is fieldSize > 0");
        }
        if (primitivePolynomial <= 0) {
            throw new DcloudInvalidDataRuntimeException("INVALID primitivePolynomial=[" + primitivePolynomial
                    + "]. Allowed value is primitivePolynomial > 0");
        }

        this.fieldSize = fieldSize;
        this.primitivePeriod = fieldSize - 1;
        this.primitivePolynomial = primitivePolynomial;
        this.addTable = new int[fieldSize][fieldSize];
        this.addInvTable = new int[fieldSize];
        this.mulTable = new int[fieldSize][fieldSize];
        this.mulInvTable = new int[fieldSize];
        this.powTable = new int[fieldSize];
        this.logTable = new int[fieldSize];

        // building addition and addition inverse table
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                this.addTable[i][j] = i ^ j;
                if (this.addTable[i][j] == 0) {
                    this.addInvTable[i] = j;
                    this.addInvTable[j] = i;
                }
            }
        }

        // building power and logarithm table
        int value = 1;
        for (int pow = 0; pow < fieldSize - 1; pow++) {
            this.powTable[pow] = value;
            this.logTable[value] = pow;
            value = value * 2;
            if (value >= fieldSize) {
                value = value ^ primitivePolynomial;
            }
        }

        // building multiplication and multiplication inverse table
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (i == 0 || j == 0) {
                    this.mulTable[i][j] = 0;
                    continue;
                }
                int z = this.logTable[i] + this.logTable[j];
                z = z >= this.primitivePeriod ? z - this.primitivePeriod : z;
                z = this.powTable[z];
                this.mulTable[i][j] = z;

                if (z == 1) {
                    this.mulInvTable[i] = j;
                    this.mulInvTable[j] = i;
                }
            }
        }

    }

    /**
     * Get the object performs Galois field arithmetics.
     *
     * @param fieldSize
     *            size of the field, has to be greater than 0.
     * @param primitivePolynomial
     *            a primitive polynomial corresponds to the size, has to be
     *            greater than 0.
     * @return an object performs Galois field arithmetics.
     * @throws DcloudInvalidDataRuntimeException
     *             if any of fieldSize or primitivePolynomial is invalid.
     */
    public static GFMath getInstance(final int fieldSize, final int primitivePolynomial) {

        final int key = ((fieldSize << 16) & 0xFFFF0000) + (primitivePolynomial & 0x0000FFFF);
        GFMath gf = INSTANCES.get(key);
        if (null == gf) {
            synchronized (INSTANCES) {
                gf = INSTANCES.get(key);
                if (gf == null) {
                    gf = new GFMath(fieldSize, primitivePolynomial);
                    INSTANCES.put(key, gf);
                }
            }
        }
        return gf;
    }

    /**
     * Get the object performs Galois field arithmetics with default setting.
     *
     * @return an object performs Galois field arithmetics.
     */
    public static GFMath getInstance() {
        return GFMath.getInstance(DEFAULT_FIELD_SIZE, DEFAULT_PRIMITIVE_POLYNOMIAL);
    }

    /**
     * Return number of elements in the field.
     *
     * @return number of elements in the field.
     */
    public int getFieldSize() {
        return this.fieldSize;
    }

    /**
     * Return the primitive polynomial in GF(2).
     *
     * @return primitive polynomial as a integer.
     */
    public int getPrimitivePolynomial() {
        return this.primitivePolynomial;
    }

    /**
     * Compute the sum of two fields.
     *
     * @param x
     *            input field.
     * @param y
     *            input field.
     * @return result of addition.
     * @throws DcloudInvalidDataRuntimeException
     *             if any of x or y is invalid.
     */
    public int add(final int x, final int y) {

        if (x < 0 || x >= this.getFieldSize() || y < 0 || y >= this.getFieldSize()) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID x=[" + x + "], y=[" + y + "]. Allowed value is 0 < x < (fieldSize=" + this.getFieldSize()
                            + ") and  0 < y < (fieldSize=" + this.getFieldSize() + ")");
        }

        return this.addTable[x][y];
    }

    /**
     * Get inverse addition of a field.
     *
     * @param x
     *            input field.
     * @return inverse addition of input.
     * @throws DcloudInvalidDataRuntimeException
     *             if x is invalid.
     */
    public int addInverse(final int x) {

        if (x < 0 || x >= this.getFieldSize()) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID x=[" + x + "]. Allowed value is 0 < x < (fieldSize=" + this.getFieldSize() + ")");
        }

        return this.addInvTable[x];
    }

    /**
     * Compute the subtraction of two fields.
     *
     * @param x
     *            input field.
     * @param y
     *            input field.
     * @return result of subtraction.
     * @throws DcloudInvalidDataRuntimeException
     *             if any of x or y is invalid.
     */
    public int substract(final int x, final int y) {
        return this.add(x, this.addInverse(y));
    }

    /**
     * Compute the multiplication of two fields.
     *
     * @param x
     *            input field.
     * @param y
     *            input field.
     * @return result of multiplication.
     * @throws DcloudInvalidDataRuntimeException
     *             if any of x or y is invalid.
     */
    public int multiply(final int x, final int y) {
        if (x < 0 || x >= this.getFieldSize() || y < 0 || y >= this.getFieldSize()) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID x=[" + x + "], y=[" + y + "]. Allowed value is 0 < x < (fieldSize=" + this.getFieldSize()
                            + ") and  0 < y < (fieldSize=" + this.getFieldSize() + ")");
        }

        return this.mulTable[x][y];
    }

    /**
     * Get inverse multiplication of a field.
     *
     * @param x
     *            input field.
     * @return inverse multiplication of input.
     * @throws DcloudInvalidDataRuntimeException
     *             if x is invalid.
     */
    public int multiplyInverse(final int x) {
        if (x < 0 || x >= this.getFieldSize()) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID x=[" + x + "]. Allowed value is 0 < x < (fieldSize=" + this.getFieldSize() + ")");
        }
        return this.mulInvTable[x];
    }

    /**
     * Compute the division of two fields.
     *
     * @param x
     *            input field.
     * @param y
     *            input field.
     * @return x/y
     * @throws DcloudInvalidDataRuntimeException
     *             if any of x or y is invalid.
     */
    public int divide(final int x, final int y) {

        return this.multiply(x, this.multiplyInverse(y));
    }

    /**
     * Compute power n of a field.
     *
     * @param x
     *            input field.
     * @param n
     *            power.
     * @return x^n
     * @throws DcloudInvalidDataRuntimeException
     *             if x is invalid.
     */
    public int power(final int x, final int n) {
        if (x < 0 || x >= this.getFieldSize()) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID x=[" + x + "]. Allowed value is 0 < x < (fieldSize=" + this.getFieldSize() + ")");
        }

        if (n == 0) {
            return 1;
        }
        if (x == 0) {
            return 0;
        }
        int result = this.logTable[x] * n;
        if (result >= this.primitivePeriod) {
            result = result % this.primitivePeriod;
        }
        return this.powTable[result];
    }

    public int exp(final int x) {
        if (x < 0 || x >= this.getFieldSize()) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID x=[" + x + "]. Allowed value is 0 < x < (fieldSize=" + this.getFieldSize() + ")");
        }
        return this.powTable[x];
    }

    public int log(final int x) {
        if (x < 0 || x >= this.getFieldSize()) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID x=[" + x + "]. Allowed value is 0 < x < (fieldSize=" + this.getFieldSize() + ")");
        }
        return this.logTable[x];
    }

    /**
     * Compute the sum of two polynomials. The index in the array corresponds to
     * the power of the entry. For example p[0] is the constant term of the
     * polynomial p.
     *
     * @param p
     *            input polynomial.
     * @param q
     *            input polynomial.
     * @return polynomial represents p+q
     * @throws DcloudInvalidDataRuntimeException
     *             if p or q is invalid.
     */
    public int[] add(final int[] p, final int[] q) {

        if (null == p || null == q) {
            throw new DcloudInvalidDataRuntimeException("INVALID value p=[" + Arrays.toString(p) + "] or q=["
                    + Arrays.toString(q) + "]. Null value is not allowed.");
        }

        final int len = Math.max(p.length, q.length);
        final int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            if (i < p.length && i < q.length) {
                result[i] = this.add(p[i], q[i]);
            } else if (i < p.length) {
                result[i] = p[i];
            } else {
                result[i] = q[i];
            }
        }
        return result;
    }

    /**
     * Compute the multiplication of two polynomials. The index in the array
     * corresponds to the power of the entry. For example p[0] is the constant
     * term of the polynomial p.
     *
     * @param p
     *            input polynomial.
     * @param q
     *            input polynomial.
     * @return polynomial represents p*q
     * @throws DcloudInvalidDataRuntimeException
     *             if p or q is invalid.
     */
    public int[] multiply(final int[] p, final int[] q) {

        if (null == p || null == q) {
            throw new DcloudInvalidDataRuntimeException("INVALID value p=[" + Arrays.toString(p) + "] or q=["
                    + Arrays.toString(q) + "]. Null value is not allowed.");
        }

        final int len = p.length + q.length - 1;
        final int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            result[i] = 0;
        }
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < q.length; j++) {
                result[i + j] = add(result[i + j], multiply(p[i], q[j]));
            }
        }
        return result;
    }

    /**
     * Compute the remainder of a dividend and divisor pair. The index in the
     * array corresponds to the power of the entry. For example p[0] is the
     * constant term of the polynomial p.
     *
     * @param dividend
     *            dividend polynomial, the remainder will be placed here when
     *            return.
     * @param divisor
     *            divisor polynomial.
     * @throws DcloudInvalidDataRuntimeException
     *             if dividend or divisor is invalid.
     */
    public void remainder(final int[] dividend, final int[] divisor) {

        if (null == dividend || null == divisor) {
            throw new DcloudInvalidDataRuntimeException("INVALID value dividend=[" + Arrays.toString(dividend)
                    + "] or divisor=[" + Arrays.toString(divisor) + "]. Null value is not allowed.");
        }

        for (int i = dividend.length - divisor.length; i >= 0; i--) {
            int ratio = this.divide(dividend[i + divisor.length - 1], divisor[divisor.length - 1]);
            for (int j = 0; j < divisor.length; j++) {
                int k = j + i;
                dividend[k] = dividend[k] ^ this.mulTable[ratio][divisor[j]];
            }
        }
    }

    /**
     * Substitute x into polynomial p(x).
     *
     * @param p
     *            input polynomial.
     * @param x
     *            input field.
     * @return p(x)
     * @throws DcloudInvalidDataRuntimeException
     *             if p invalid.
     */
    public int substitute(final int[] p, final int x) {

        if (null == p) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID value p=[" + Arrays.toString(p) + "]. Null value is not allowed.");
        }

        int result = 0;
        int y = 1;
        for (int i = 0; i < p.length; i++) {
            result = result ^ this.mulTable[p[i]][y];
            y = this.mulTable[x][y];
        }
        return result;
    }

    /**
     * Generate random in the GF
     *
     * @return random
     */
    public int getRandom() {
        return RANDOM.nextInt(this.fieldSize);
    }

    public Random getRandomGenerator() {
        return RANDOM;
    }

}
