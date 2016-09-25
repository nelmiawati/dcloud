/*
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.ac.polibatam.mj.dcloud.math;

/**
 * <p>
 * Represents a polynomial whose coefficients are elements of a GF. Instances of
 * this class are immutable.</p>
 *
 * <p>
 * Much credit is due to William Rucklidge since portions of this code are an
 * indirect port of his C++ Reed-Solomon implementation.</p>
 *
 * @author Sean Owen
 * @author mia (Adopted from ZXing project)
 */
public class GenericGFPoly {

    public static final int[] ZERO = {0};
    public static final int[] ONE = {1};
    protected final GFMath field;
    protected final int[] coefficients;

    public GenericGFPoly(int[] coefficient) {
        this(GFMath.DEFAULT_FIELD_SIZE, GFMath.DEFAULT_PRIMITIVE_POLYNOMIAL, coefficient);
    }

    /**
     * @param field the {@link GenericGF} instance representing the field to use
     * to perform computations
     * @param coefficients coefficients as ints representing elements of
     * GF(size), arranged from most significant (highest-power term) coefficient
     * to least significant
     * @throws IllegalArgumentException if argument is null or empty, or if
     * leading coefficient is 0 and this is not a constant polynomial (that is,
     * it is not the monomial "0")
     */
    public GenericGFPoly(int fieldSize, int primitivePolynomial, int[] coefficients) {
        if (coefficients.length == 0) {
            throw new IllegalArgumentException();
        }
        this.field = GFMath.getInstance(fieldSize, primitivePolynomial);
        int coefficientsLength = coefficients.length;
        if (coefficientsLength > 1 && coefficients[0] == 0) {
            // Leading term must be non-zero for anything except the constant polynomial "0"
            int firstNonZero = 1;
            while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0) {
                firstNonZero++;
            }
            if (firstNonZero == coefficientsLength) {
                this.coefficients = ZERO;
            } else {
                this.coefficients = new int[coefficientsLength - firstNonZero];
                System.arraycopy(coefficients,
                        firstNonZero,
                        this.coefficients,
                        0,
                        this.coefficients.length);
            }
        } else {
            this.coefficients = coefficients;
        }
    }

    public int[] getCoefficients() {
        return coefficients;
    }

    /**
     * @return degree of this polynomial
     */
    public int getDegree() {
        return coefficients.length - 1;
    }

    /**
     * @return true iff this polynomial is the monomial "0"
     */
    public boolean isZero() {
        return coefficients[0] == 0;
    }

    /**
     * @return coefficient of x^degree term in this polynomial
     */
    public int getCoefficient(int degree) {
        return coefficients[coefficients.length - 1 - degree];
    }

    /**
     * @return evaluation of this polynomial at a given point
     */
    public int evaluateAt(int a) {
        if (a == 0) {
            // Just return the x^0 coefficient
            return getCoefficient(0);
        }
        int size = coefficients.length;
        if (a == 1) {
            // Just the sum of the coefficients
            int result = 0;
            for (int coefficient : coefficients) {
                result = field.add(result, coefficient);
            }
            return result;
        }
        int result = coefficients[0];
        for (int i = 1; i < size; i++) {
            result = field.add(field.multiply(a, result), coefficients[i]);
        }
        return result;
    }

    public GenericGFPoly addOrSubtract(GenericGFPoly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
        }
        if (isZero()) {
            return other;
        }
        if (other.isZero()) {
            return this;
        }

        int[] smallerCoefficients = this.coefficients;
        int[] largerCoefficients = other.coefficients;
        if (smallerCoefficients.length > largerCoefficients.length) {
            int[] temp = smallerCoefficients;
            smallerCoefficients = largerCoefficients;
            largerCoefficients = temp;
        }
        int[] sumDiff = new int[largerCoefficients.length];
        int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
        // Copy high-order terms only found in higher-degree polynomial's coefficients
        System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);

        for (int i = lengthDiff; i < largerCoefficients.length; i++) {
            sumDiff[i] = field.add(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
        }

        return new GenericGFPoly(field.getFieldSize(), field.getPrimitivePolynomial(), sumDiff);
    }

    public GenericGFPoly multiply(GenericGFPoly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
        }
        if (isZero() || other.isZero()) {
            return new GenericGFPoly(field.getFieldSize(), field.getPrimitivePolynomial(), ZERO);
        }
        int[] aCoefficients = this.coefficients;
        int aLength = aCoefficients.length;
        int[] bCoefficients = other.coefficients;
        int bLength = bCoefficients.length;
        int[] product = new int[aLength + bLength - 1];
        for (int i = 0; i < aLength; i++) {
            int aCoeff = aCoefficients[i];
            for (int j = 0; j < bLength; j++) {
                product[i + j] = field.add(product[i + j],
                        field.multiply(aCoeff, bCoefficients[j]));
            }
        }
        return new GenericGFPoly(field.getFieldSize(), field.getPrimitivePolynomial(), product);
    }

    public GenericGFPoly multiply(int scalar) {
        if (scalar == 0) {
            return new GenericGFPoly(field.getFieldSize(), field.getPrimitivePolynomial(), ZERO);
        }
        if (scalar == 1) {
            return this;
        }
        int size = coefficients.length;
        int[] product = new int[size];
        for (int i = 0; i < size; i++) {
            product[i] = field.multiply(coefficients[i], scalar);
        }
        return new GenericGFPoly(field.getFieldSize(), field.getPrimitivePolynomial(), product);
    }

    public GenericGFPoly multiplyByMonomial(int degree, int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return new GenericGFPoly(field.getFieldSize(), field.getPrimitivePolynomial(), ZERO);
        }
        int size = coefficients.length;
        int[] product = new int[size + degree];
        for (int i = 0; i < size; i++) {
            product[i] = field.multiply(coefficients[i], coefficient);
        }
        return new GenericGFPoly(field.getFieldSize(), field.getPrimitivePolynomial(), product);
    }

    public GenericGFPoly[] divide(GenericGFPoly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
        }
        if (other.isZero()) {
            throw new IllegalArgumentException("Divide by 0");
        }

        GenericGFPoly quotient = new GenericGFPoly(field.getFieldSize(), field.getPrimitivePolynomial(), ZERO);
        GenericGFPoly remainder = this;

        int denominatorLeadingTerm = other.getCoefficient(other.getDegree());
        int inverseDenominatorLeadingTerm = field.multiplyInverse(denominatorLeadingTerm);

        while (remainder.getDegree() >= other.getDegree() && !remainder.isZero()) {
            int degreeDifference = remainder.getDegree() - other.getDegree();
            int scale = field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
            GenericGFPoly term = other.multiplyByMonomial(degreeDifference, scale);
            GenericGFPoly iterationQuotient = this.buildMonomial(field.getFieldSize(), field.getPrimitivePolynomial(), degreeDifference, scale);
            quotient = quotient.addOrSubtract(iterationQuotient);
            remainder = remainder.addOrSubtract(term);
        }

        return new GenericGFPoly[]{quotient, remainder};
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(8 * getDegree());
        for (int degree = getDegree(); degree >= 0; degree--) {
            int coefficient = getCoefficient(degree);
            if (coefficient != 0) {
                if (coefficient < 0) {
                    result.append(" - ");
                    coefficient = -coefficient;
                } else {
                    if (result.length() > 0) {
                        result.append(" + ");
                    }
                }
                if (degree == 0 || coefficient != 1) {
                    int alphaPower = field.log(coefficient);
                    if (alphaPower == 0) {
                        result.append('1');
                    } else if (alphaPower == 1) {
                        result.append('a');
                    } else {
                        result.append("a^");
                        result.append(alphaPower);
                    }
                }
                if (degree != 0) {
                    if (degree == 1) {
                        result.append('x');
                    } else {
                        result.append("x^");
                        result.append(degree);
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * @return the monomial representing coefficient * x^degree
     */
    private GenericGFPoly buildMonomial(int fieldSize, int primitivePolynomial, int degree, int coefficient) {

        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return new GenericGFPoly(fieldSize, primitivePolynomial, ZERO);
        }
        int[] aCoefficients = new int[degree + 1];
        aCoefficients[0] = coefficient;
        return new GenericGFPoly(fieldSize, primitivePolynomial, aCoefficients);
    }
}
