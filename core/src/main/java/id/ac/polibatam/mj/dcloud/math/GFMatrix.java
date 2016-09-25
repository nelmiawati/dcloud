/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.math;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;

/**
 *
 * @author mia
 */
public class GFMatrix {

	private GFMath gfmath = null;

	public GFMatrix(final int fieldSize, final int primitivePolynomial) {
		this.gfmath = GFMath.getInstance(fieldSize, primitivePolynomial);
	}

	public GFMatrix() {
		this.gfmath = GFMath.getInstance();
	}

	public boolean isValidDimension(final int[][] m) {

		if (null == m || m.length == 0 || null == m[0] || m[0].length == 0) {
			return false;
		}
		for (int[] row : m) {
			if (null == row) {
				return false;
			}
			if (row.length != m[0].length) {
				return false;
			}
		}

		return true;
	}

	public int[] getRow(final int[][] m, final int i) {

		if (!this.isValidDimension(m)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		if (i < 0 || i >= m.length) {
			throw new DcloudInvalidDataRuntimeException("index i=[" + i + "] out of range 0 <= i < " + m.length);
		}

		return m[i].clone();
	}

	public void setRow(final int[][] m, final int[] row, final int i) {

		if (!this.isValidDimension(m)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		if (i < 0 || i >= m.length) {
			throw new DcloudInvalidDataRuntimeException("index i=[" + i + "] out of range 0 <= i < " + m.length);
		}

		if (null == row || row.length != m[i].length) {
			throw new DcloudInvalidDataRuntimeException("Unmatch row length=[" + (null == row ? "null" : row.length)
					+ "] with matrix m column length=[" + m[i].length + "]");
		}
		System.arraycopy(row, 0, m[i], 0, m[i].length);

	}

	public int[] getColumn(final int[][] m, final int j) {

		if (!this.isValidDimension(m)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		if (j < 0 || j >= m[0].length) {
			throw new DcloudInvalidDataRuntimeException("index i=[" + j + "] out of range 0 <= j < " + m[0].length);
		}

		final int[] column = new int[m.length];
		for (int i = 0; i < m.length; i++) {
			column[i] = m[i][j];
		}

		return column;
	}

	public void setColumn(final int[][] m, final int[] column, final int j) {

		if (!this.isValidDimension(m)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		if (j < 0 || j >= m[0].length) {
			throw new DcloudInvalidDataRuntimeException("index j=[" + j + "] out of range 0 <= j < " + m[0].length);
		}

		if (null == column || column.length != m.length) {
			throw new DcloudInvalidDataRuntimeException("Unmatch row length=["
					+ (null == column ? "null" : column.length) + "] with matrix m row length=[" + m.length + "]");
		}

		for (int i = 0; i < m.length; i++) {
			m[i][j] = column[i];
		}

	}

	public int[] toArrayRowColumn(final int[][] m) {

		if (!this.isValidDimension(m)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		final int[] arr = new int[m.length * m[0].length];
		for (int i = 0; i < m.length; i++) {

			for (int j = 0; j < m[i].length; j++) {
				arr[i * m[0].length + j] = m[i][j];
			}

		}

		return arr;

	}

	public int[] toArrayColumnRow(final int[][] m) {

		if (!this.isValidDimension(m)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		final int[] arr = new int[m.length * m[0].length];
		for (int j = 0; j < m[j].length; j++) {

			for (int i = 0; i < m.length; i++) {
				arr[j * m.length + i] = m[i][j];
			}

		}

		return arr;

	}

	public int[][] toMatrixRowColumn(final int[] arr, final int i, final int j) {

		if (null == arr) {
			throw new DcloudInvalidDataRuntimeException("Invalid parameter input: arr is null");
		}

		if (arr.length != (i * j)) {
			throw new DcloudInvalidDataRuntimeException("Unmatch matrix dimension");
		}

		final int[][] m = new int[i][j];
		for (int k = 0; k < m.length; k++) {
			for (int l = 0; l < m[k].length; l++) {
				m[k][l] = arr[k * m[0].length + l];
			}
		}

		return m;
	}

	public int[][] toMatrixColumnRow(final int[] arr, final int i, final int j) {

		if (null == arr) {
			throw new DcloudInvalidDataRuntimeException("Invalid parameter input: arr is null");
		}

		if (arr.length != (i * j)) {
			throw new DcloudInvalidDataRuntimeException("Unmatch matrix dimension");
		}

		final int[][] m = new int[i][j];
		for (int l = 0; l < m[0].length; l++) {
			for (int k = 0; k < m.length; k++) {
				m[k][l] = arr[l * m.length + k];
			}
		}

		return m;
	}

	public int[][] transpose(final int[][] m) {

		if (!this.isValidDimension(m)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		final int n[][] = new int[m[0].length][m.length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				n[j][i] = m[i][j];
			}
		}

		return n;

	}

	public int[][] add(final int[][] a, final int[][] b) {

		if (!this.isValidDimension(a) || !this.isValidDimension(b)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		if (a.length != b.length || a[0].length != b[0].length) {
			throw new DcloudInvalidDataRuntimeException("Unmatch matrix dimension");
		}

		final int[][] c = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				c[i][j] = this.gfmath.add(a[i][j], b[i][j]);
			}
		}

		return c;
	}

	public int[][] substract(final int[][] a, final int[][] b) {

		if (!this.isValidDimension(a) || !this.isValidDimension(b)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		if (a.length != b.length || a[0].length != b[0].length) {
			throw new DcloudInvalidDataRuntimeException("Unmatch matrix dimension");
		}

		final int[][] c = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				c[i][j] = this.gfmath.substract(a[i][j], b[i][j]);
			}
		}

		return c;
	}

	public int[][] multiply(final int[][] a, final int[][] b) {

		if (!this.isValidDimension(a) || !this.isValidDimension(b)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		if (a[0].length != b.length) {
			throw new DcloudInvalidDataRuntimeException("Unmatch matrix dimension");
		}

		final int[][] c = new int[a.length][b[0].length];
		for (int j = 0; j < b[0].length; j++) {

			for (int i = 0; i < a.length; i++) {

				for (int k = 0; k < a[0].length; k++) {
					c[i][j] = this.gfmath.add(c[i][j], this.gfmath.multiply(a[i][k], b[k][j]));
				}

			}

		}

		return c;
	}

	/**
	 * Given a Vandermonde matrix V[i][j]=v[j]^i and vector b, solve for x such
	 * that Vx=b. Adopted from: Bjorek Ake and Pereyra Victor,Solution of
	 * Vandermonde Systems of Equations, Mathematics of Computation, Vol 24, No.
	 * 112, pp.893-903, 1970.
	 *
	 * @param v
	 *            the vector which describe the Vandermonde matrix.
	 * @param b
	 *            right-hand side of the Vandermonde system equation.
	 * @return x.
	 * @throws DcloudInvalidDataRuntimeException
	 *             if v or b is invalid.
	 */
	public int[] solveVandermondeSystem(final int[] v, final int[] b) {

		if (null == v || null == b) {
			throw new DcloudInvalidDataRuntimeException("Invalid parameter input: v or b is null");
		}
		if (v.length < 1 || b.length < 1) {
			throw new DcloudInvalidDataRuntimeException(
					"Invalid parameter input: v.length=[" + v.length + "] != b.length=[" + b.length + "]");
		}
		if (v.length != b.length) {
			throw new DcloudInvalidDataRuntimeException(
					"Invalid parameter input: v.length=[" + v.length + "] != b.length=[" + b.length + "]");
		}

		final int[] x = new int[b.length];
		// for (int i = 0; i < b.length; i++) {
		// x[i] = b[i];
		// }
		System.arraycopy(b, 0, x, 0, b.length);

		for (int i = 0; i < (b.length - 1); i++) {
			for (int j = (b.length - 1); j > i; j--) {
				x[j] = this.gfmath.add(x[j], this.gfmath.addInverse(this.gfmath.multiply(v[i], x[j - 1])));
			}
		}

		for (int i = b.length - 2; i >= 0; i--) {
			for (int j = i + 1; j < b.length; j++) {
				x[j] = this.gfmath.multiply(x[j],
						this.gfmath.multiplyInverse(this.gfmath.add(v[j], this.gfmath.addInverse(v[j - i - 1]))));
			}
			for (int j = i; j < (b.length - 1); j++) {
				x[j] = this.gfmath.add(x[j], this.gfmath.addInverse(x[j + 1]));
			}
		}

		return x;
	}

	/**
	 * Given a Vandermonde matrix V[i][j]=v[i]^j and vector f, solve for a such
	 * that Va=f. Adopted from: Bjorek Ake and Pereyra Victor,Solution of
	 * Vandermonde Systems of Equations, Mathematics of Computation, Vol 24, No.
	 * 112, pp.893-903, 1970.
	 *
	 * @param v
	 *            the vector which describe the Vandermonde matrix.
	 * @param f
	 *            right-hand side of the Vandermonde system equation.
	 * @return a.
	 * @throws DcloudInvalidDataRuntimeException
	 *             if v or f is invalid.
	 */
	public int[] solveVandermondeSystemT(final int[] v, final int[] f) {

		if (null == v || null == f) {
			throw new DcloudInvalidDataRuntimeException("Invalid parameter input: v or f is null");
		}
		if (v.length < 1 || f.length < 1) {
			throw new DcloudInvalidDataRuntimeException(
					"Invalid parameter input: v.length=[" + v.length + "] != f.length=[" + f.length + "]");
		}
		if (v.length != f.length) {
			throw new DcloudInvalidDataRuntimeException(
					"Invalid parameter input: v.length=[" + v.length + "] != f.length=[" + f.length + "]");
		}

		final int[] a = new int[f.length];
		// for (int i = 0; i < f.length; i++) {
		// a[i] = f[i];
		// }
		System.arraycopy(f, 0, a, 0, f.length);

		for (int i = 0; i < (f.length - 1); i++) {
			for (int j = (f.length - 1); j > i; j--) {
				a[j] = this.gfmath.add(a[j], this.gfmath.addInverse(a[j - 1]));
				a[j] = this.gfmath.multiply(a[j],
						this.gfmath.multiplyInverse(this.gfmath.add(v[j], this.gfmath.addInverse(v[j - i - 1]))));
			}
		}

		for (int i = f.length - 2; i >= 0; i--) {
			for (int j = i; j < (v.length - 1); j++) {
				a[j] = this.gfmath.add(a[j], this.gfmath.addInverse(this.gfmath.multiply(v[i], a[j + 1])));
			}
		}

		return a;

	}

	public int[][] solveVandermondeSystem(final int[] v, final int[][] b) {
		return null;
	}

	public int[][] solveVandermondeSystemT(final int[] v, final int[][] f) {

		if (null == v || null == f) {
			throw new DcloudInvalidDataRuntimeException("Invalid parameter input: v or b is null");
		}
		if (!this.isValidDimension(f)) {
			throw new DcloudInvalidDataRuntimeException("Invalid matrix dimension");
		}

		final int[][] result = new int[v.length][f[0].length];
		for (int j = 0; j < f[0].length; j++) {

			this.setColumn(result, this.solveVandermondeSystemT(v, this.getColumn(f, j)), j);

		}

		return result;
	}

}
