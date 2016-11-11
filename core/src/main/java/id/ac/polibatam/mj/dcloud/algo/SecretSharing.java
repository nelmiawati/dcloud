/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.algo;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import id.ac.polibatam.mj.dcloud.math.GFMath;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * Adopted from:
 * http://dl.dropboxusercontent.com/u/4971686/100617/SecretSharing.java.txt
 *
 * @author mia
 */
public class SecretSharing {

	private static final Logger LOG = Logger.getLogger(SecretSharing.class);

	private GFMath gfMath = null;

	public SecretSharing(final int fieldSize, final int primitivePolynomial) {
		this.gfMath = GFMath.getInstance(fieldSize, primitivePolynomial);
	}

	public SecretSharing() {
		this.gfMath = GFMath.getInstance();
	}

	public int[][] split(final int n, final int t, final int secret) {
		return this.split(n, t, new int[] { secret });
	}

	public int[][] split(final int n, final int t, final int[] secret) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("n=[" + n + "], t=[" + t + "], secret=[" + Arrays.toString(secret) + "]");
		}

		if (n <= 0) {
			throw new DcloudInvalidDataRuntimeException("INVALID n=[" + n + "], out of range n > 0");
		}
		if (t > n) {
			throw new DcloudInvalidDataRuntimeException(
					"INVALID t=[" + t + "] and n=[" + n + "], doesn't satisfy t < n");
		}
		for (int i = 0; i < secret.length; i++) {
			if (secret[i] < 0 || secret[i] >= this.gfMath.getFieldSize()) {
				throw new DcloudInvalidDataRuntimeException("INVALID secret[" + i + "]=[" + secret[i]
						+ "], out of range 0 <= secret < " + this.gfMath.getFieldSize());
			}
		}

		final int[][] secretPolynomial = this.computeSecretPolynomial(secret, t);
		if (LOG.isTraceEnabled()) {
			LOG.trace(Arrays.deepToString(secretPolynomial));
		}
		return this.evalPolynomial(secretPolynomial, n);
	}

	private int[][] computeSecretPolynomial(final int[] secret, final int t) {

		final int[][] result = new int[secret.length][t];
		for (int i = 0; i < result.length; i++) {
			final int[] p = result[i];
			for (int j = 0; j < p.length; j++) {
				p[j] = this.gfMath.getRandom();
			}
			p[p.length - 1] = secret[i];
		}

		return result;
	}

	private int[][] evalPolynomial(final int[][] secretPolynomial, final int n) {

		final int[][] result = new int[n][secretPolynomial.length + 1];
		for (int i = 0; i < result.length; i++) {
			final int[] r = result[i];
			r[r.length - 1] = i + 1;
			for (int j = 0; j < secretPolynomial.length; j++) {
				final int[] p = secretPolynomial[j];
				int x = 0;
				for (int k = 0; k < p.length; k++) {
					x = this.gfMath.add(this.gfMath.multiply(x, i + 1), p[k]);
				}
				r[j] = x;
			}
		}

		return result;
	}

	public int[] reconstruct(final int[][] sharedKeys) {

		if (null == sharedKeys || null == sharedKeys[0]) {
			throw new DcloudInvalidDataRuntimeException("INVALID null sharedKeys");
		}

		final int sharedKeyLength = sharedKeys[0].length;
		for (int i = 0; i < sharedKeys.length; i++) {
			if (null == sharedKeys[i] || sharedKeys[i].length != sharedKeyLength) {
				throw new DcloudInvalidDataRuntimeException(
						"INVALID null sharedKeys[" + i + "] or its length != " + sharedKeyLength);
			}
			if (sharedKeys[i][sharedKeys[i].length - 1] < 0) {
				throw new DcloudInvalidDataRuntimeException("INVALID sharedKeysIdx[" + i + "]=["
						+ sharedKeys[i][sharedKeys.length - 1] + "], out of range sharedKeyIdx > 0");
			}

			for (int j = 0; j < sharedKeys[i].length - 1; j++) {
				if (sharedKeys[i][j] < 0 || sharedKeys[i][j] >= this.gfMath.getFieldSize()) {
					throw new DcloudInvalidDataRuntimeException("INVALID sharedKeys[" + i + "][" + j + "]=["
							+ sharedKeys[i][j] + "], out of range 0 <= sharedKey < " + this.gfMath.getFieldSize());
				}
			}
		}

		final int[] sharedKeysIdx = new int[sharedKeys.length];
		for (int i = 0; i < sharedKeys.length; i++) {
			for (int j = i + 1; j < sharedKeys.length; j++) {
				if (sharedKeys[i][sharedKeys[i].length - 1] == sharedKeys[j][sharedKeys[j].length - 1]) {
					throw new DcloudInvalidDataRuntimeException("NON-UNIQUE sharedKeysIdx. sharedKeysIdx[" + i + "]=["
							+ sharedKeys[i][1] + "] equals to sharedKeysIdx[" + j + "]=[" + sharedKeys[j][1] + "]");
				}
			}
			sharedKeysIdx[i] = sharedKeys[i][sharedKeys[i].length - 1];
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace(Arrays.toString(sharedKeysIdx));
		}
		final int[] lagrange = this.computeLagrange(sharedKeysIdx);

		int[] result = new int[sharedKeyLength - 1];
		for (int i = 0; i < result.length; i++) {
			int n = 0;
			for (int j = 0; j < lagrange.length; j++) {
				n = this.gfMath.add(n, this.gfMath.multiply(sharedKeys[j][i], lagrange[j]));
			}
			result[i] = n;
		}

		return result;
	}

	private int[] computeLagrange(final int[] sharedKeysIdx) {
		final int[] result = new int[sharedKeysIdx.length];
		for (int i = 0; i < result.length; i++) {
			int p = 1;
			int q = 1;
			for (int j = 0; j < sharedKeysIdx.length; j++) {
				if (i != j) {
					p = this.gfMath.multiply(p, sharedKeysIdx[j]);
					q = this.gfMath.multiply(q, this.gfMath.add(sharedKeysIdx[j], sharedKeysIdx[i]));
				}
			}
			result[i] = this.gfMath.divide(p, q);
		}
		return result;
	}

}
