/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.algo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.io.DcloudFileOutputStream;
import id.ac.polibatam.mj.dcloud.io.DcloudHeader;
import id.ac.polibatam.mj.dcloud.math.GFMath;
import id.ac.polibatam.mj.dcloud.math.GFMatrix;
import id.ac.polibatam.mj.dcloud.math.IDAMath;
import id.ac.polibatam.mj.dcloud.util.ArrayUtils;
import id.ac.polibatam.mj.dcloud.util.Converter;

/**
 *
 * @author mia
 */
public class FileDispersal {

	private static final Logger LOG = Logger.getLogger(FileDispersal.class);

	private final GFMatrix gfMatrix = new GFMatrix();
	private final GFMath gfMath = GFMath.getInstance();
	private int m = 0;
	private int n = 0;
	private int[][] mSecretKey;
	private int randomSeed = 0;

	public FileDispersal(final byte[] vSecretKey, final int threshold) throws DcloudInvalidDataException {

		if (null == vSecretKey || vSecretKey.length < 2) {
			throw new DcloudInvalidDataException("INVALID vSecretKey, length has to be > 1");
		}

		if (!ArrayUtils.isUnique(vSecretKey)) {
			throw new DcloudInvalidDataException("INVALID vSecretKey, elements have to be unique");
		}

		if (threshold < 1 || threshold > vSecretKey.length) {
			throw new DcloudInvalidDataException(
					"INVALID threshold, has to be in the range of 1 < threshold <= vSecrentKey.length");
		}

		this.m = threshold;
		this.n = vSecretKey.length;
		final int[] unsignedVSecretKey = Converter.convertSignedByteToUnsignedByte(vSecretKey);

		this.randomSeed = IDAMath.generateRandomSeed(this.gfMath, unsignedVSecretKey);
		if (LOG.isTraceEnabled()) {
			LOG.trace("randomSeed=[" + randomSeed + "]");
		}

		this.generateSecretKey(unsignedVSecretKey);
		if (LOG.isTraceEnabled()) {
			LOG.trace("mSecretKey=[" + Arrays.deepToString(this.mSecretKey) + "]");
		}

	}

	public File[] disperse(final File origFile, File outputDir, final boolean useSalt)
			throws DcloudInvalidDataException, DcloudSystemInternalException {

		if (null == origFile || null == outputDir) {
			throw new DcloudInvalidDataException("INVALID null origFile or null outputDir");
		}

		if (!origFile.exists()) {
			throw new DcloudInvalidDataException(
					"INVALID origFile, NONEXISTING origFile=[" + origFile.getAbsolutePath() + "]");
		}

		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		FileInputStream fis = null;
		final File[] dispersedFiles = new File[this.n];
		final DcloudFileOutputStream[] arDos = new DcloudFileOutputStream[this.n];

		try {

			// Prepare reader
			int paddLen = (int) (origFile.length() % (long) this.m);
			if (0 != paddLen) {
				paddLen = this.m - paddLen;
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("origFile.length=[" + origFile.length() + "]");
				LOG.trace("n=[" + m + "]");
				LOG.trace("origFile.length % m=[" + paddLen + "]");
			}
			fis = new FileInputStream(origFile);

			// Prepare writer
			final int[][] unsignedSecretShares = this.generateShare(this.n, this.m,
					this.gfMatrix.getColumn(this.mSecretKey, 1));

			for (int i = 0; i < arDos.length; i++) {
				final DcloudHeader header = new DcloudHeader();
				header.setDispersalIdx(i);
				header.setPaddLen(paddLen);
				header.setThreshold(this.m);
				header.setVSecretShare(Converter.convertUnsignedByteToSignedByte(unsignedSecretShares[i]));

				dispersedFiles[i] = new File(outputDir.getAbsolutePath().concat(File.separator)
						.concat(origFile.getName()).concat(".").concat(Integer.toString(i)).concat(".dc"));

				arDos[i] = new DcloudFileOutputStream(dispersedFiles[i], header);
			}

			// Execute now
			final Random random = new Random(this.randomSeed);
			final int[][] buffRead = new int[this.m][1];
			final byte[] buffReadByteColumn = new byte[this.m];
			while (fis.read(buffReadByteColumn) > -1) {

				this.gfMatrix.setColumn(buffRead, Converter.convertSignedByteToUnsignedByte(buffReadByteColumn), 0);
				if (LOG.isTraceEnabled()) {
					LOG.trace("buffRead=[" + Arrays.deepToString(buffRead) + "]");
				}

				int[][] buffWriteInt = null;
				if (useSalt) {
					final int[][] salt = IDAMath.generateSalt(random, this.m, 1);
					if (LOG.isTraceEnabled()) {
						LOG.trace("salt=[" + Arrays.deepToString(salt) + "]");
					}
					buffWriteInt = this.gfMatrix.multiply(this.mSecretKey, this.gfMatrix.add(buffRead, salt));
				} else {
					buffWriteInt = this.gfMatrix.multiply(this.mSecretKey, buffRead);
				}

				for (int i = 0; i < this.n; i++) {
					arDos[i].write(Converter.convertUnsignedByteToSignedByte(this.gfMatrix.getRow(buffWriteInt, i)));
				}
			}

		} catch (FileNotFoundException e) {
			throw new DcloudInvalidDataException(e.getMessage(), e);
		} catch (IOException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					if (LOG.isEnabledFor(Level.WARN)) {
						LOG.warn(e.getMessage());
					}
				}

				for (DcloudFileOutputStream dos : arDos) {
					if (null != dos) {
						try {
							dos.close();
						} catch (IOException e) {
							if (LOG.isEnabledFor(Level.WARN)) {
								LOG.warn(e.getMessage());
							}
						}
					}
				}
			}
		}

		return dispersedFiles;
	}

	private void generateSecretKey(final int[] unsignedVSecretKey) {

		this.mSecretKey = new int[this.n][this.m];
		for (int i = 0; i < this.mSecretKey[0].length; i++) {

			final int[] columnUnsignedVSecretKey = new int[unsignedVSecretKey.length];
			for (int j = 0; j < columnUnsignedVSecretKey.length; j++) {
				columnUnsignedVSecretKey[j] = this.gfMath.power(unsignedVSecretKey[j], i);
			}

			this.gfMatrix.setColumn(this.mSecretKey, columnUnsignedVSecretKey, i);
		}

	}

	private int[][] generateShare(final int nbShares, final int th, final int[] secret) {

		final SecretSharing ss = new SecretSharing();
		return ss.split(nbShares, th, secret);

	}

}
