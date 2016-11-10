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
import java.util.Random;

import org.apache.log4j.Logger;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.io.DcloudFileOutputStream;
import id.ac.polibatam.mj.dcloud.io.DcloudHeader;
import id.ac.polibatam.mj.dcloud.math.GFMath;
import id.ac.polibatam.mj.dcloud.math.GFMatrix;
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

		if (!this.isUnique(vSecretKey)) {
			throw new DcloudInvalidDataException("INVALID vSecretKey, elements have to be unique");
		}

		if (threshold < 1 || threshold > vSecretKey.length) {
			throw new DcloudInvalidDataException(
					"INVALID threshold, value must be in the range of 1 < threshold <= vSecrentKey.length");
		}

		this.n = vSecretKey.length;
		this.m = threshold;
		this.generateSecretKey(vSecretKey);

	}

	private boolean isUnique(final byte[] arr) {

		for (int i = 0; i < arr.length; i++) {
			for (int j = (i + 1); j < arr.length; j++) {
				if (arr[i] == arr[j]) {
					return false;
				}
			}
		}
		return true;
	}

	private void generateSecretKey(final byte[] vSecretKey) {

		final int[] unsignedVSecretKey = Converter.convertSignedByteToUnsignedByte(vSecretKey);
		for (int elm : unsignedVSecretKey) {
			this.randomSeed = this.gfMath.add(this.randomSeed, elm);
		}

		this.mSecretKey = new int[this.n][this.m];
		for (int i = 0; i < this.mSecretKey[0].length; i++) {

			final int[] columnUnsignedVSecretKey = new int[unsignedVSecretKey.length];
			for (int j = 0; j < columnUnsignedVSecretKey.length; j++) {
				columnUnsignedVSecretKey[j] = this.gfMath.power(unsignedVSecretKey[j], i);
			}

			this.gfMatrix.setColumn(this.mSecretKey, columnUnsignedVSecretKey, i);
		}

	}



	public File[] disperse(final File origFile, File outputDir, final boolean useSalt)
			throws DcloudInvalidDataException {

		if (null == origFile) {
			throw new DcloudInvalidDataException("INVALID null origFile");
		}

		if (!origFile.exists()) {
			throw new DcloudInvalidDataException(
					"INVALID origFile, NONEXISTING origFile=[" + origFile.getAbsolutePath() + "]");
		}

		if (!outputDir.exists()) {
			outputDir.mkdir();
		}

		FileInputStream fis = null;
		final int paddLen = (int) (origFile.length() % (long) this.n);

		final File[] dispersedFiles = new File[this.n];
		final DcloudHeader[] arHeader = new DcloudHeader[dispersedFiles.length];
		final SecretSharing ss = new SecretSharing();
		final int[][] unsignedSecretShares = ss.split(this.n, this.m, this.gfMatrix.getColumn(this.mSecretKey, 1));
		int idx = 0;
		for (DcloudHeader header : arHeader) {
			header.setDispersalIdx(idx++);
			header.setPaddLen(paddLen);
			header.setThreshold(this.m);
			header.setVSecretKeyDist(null);
		}

		final DcloudFileOutputStream[] arDos = new DcloudFileOutputStream[dispersedFiles.length];

		try {

			final long origFileSize = origFile.length();
			fis = new FileInputStream(origFile);

		} catch (FileNotFoundException e) {
			throw new DcloudInvalidDataException(e.getMessage(), e);
		} finally {
			if (null == fis) {
				try {
					fis.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage());
				}

				for (DcloudFileOutputStream dos : arDos) {
					if (null != arDos) {
						try {
							dos.close();
						} catch (IOException e) {
							LOG.warn(e.getMessage());
						}
					}
				}
			}
		}

		// final DcloudHeader header = new DcloudHeader();
		// header.set
		//
		// final ImagePixelSequenceWriter[] ipsWriter = new
		// ImagePixelSequenceWriter[this.n];
		// for (int i = 0; i < ipsWriter.length; i++) {
		// final BufferedImage bufferedImage = new BufferedImage(widthOutput,
		// heightOutput, bfInput.getType());
		// ipsWriter[i] = new ImagePixelSequenceWriter(bufferedImage);
		// }
		//
		// int[][] bufferRead = new int[this.m][];
		// final ImagePixelSequenceReader ipsReader = new
		// ImagePixelSequenceReader(bfInput);
		//
		// final Random random = new Random(this.randomSeed);
		// int nbRead = 0;
		// while ((nbRead = ipsReader.readSamples(bufferRead, 0,
		// bufferRead.length)) > 0) {
		//
		// for (int i = nbRead; i < bufferRead.length; i++) {
		// bufferRead[i] = new int[bufferRead[0].length];
		// }
		//
		// for (int[] bufferReadRows : bufferRead) {
		// if (null != bufferReadRows) {
		// for (int bufferReadElm : bufferReadRows) {
		// if (bufferReadElm < 0 || bufferReadElm > 255) {
		// throw new MiadPacsSystemInternalException("UNSUPPORTED IDA for
		// bitsAllocated > 8bit");
		// }
		// }
		// }
		// }
		//
		// int[][] bufferWrite = null;
		// if (useSalt) {
		// final int[][] salt = this.generateSalt(random, this.m,
		// ipsReader.getNumBands());
		// bufferWrite = this.gfMatrix.multiply(this.mSecretKey,
		// this.gfMatrix.add(bufferRead, salt));
		// } else {
		// bufferWrite = this.gfMatrix.multiply(this.mSecretKey, bufferRead);
		// }
		//
		// for (int i = 0; i < ipsWriter.length; i++) {
		// ipsWriter[i].writeSamples(bufferWrite[i]);
		// }
		//
		// bufferRead = new int[this.m][];
		// }
		//
		// final SecretSharing ss = new SecretSharing();
		// final int[][] unsignedSecretShares = ss.split(this.n, this.m,
		// this.gfMatrix.getColumn(this.mSecretKey, 1));
		// if (LOG.isDebugEnabled()) {
		// LOG.debug("ipsWriter.length=[" + ipsWriter.length + "]");
		// LOG.debug("mSecret=[" + Arrays.deepToString(this.mSecretKey) + "]");
		// LOG.debug("secret=[" +
		// Arrays.toString(this.gfMatrix.getColumn(this.mSecretKey, 1)) + "]");
		// LOG.debug("randomSeed=[" + this.randomSeed + "]");
		// LOG.debug("secretShares=[" +
		// Arrays.deepToString(unsignedSecretShares) + "]");
		// }
		//
		// final Map<AttributeTag, String> attributeTags =
		// this.extractAttributeTag(dicomReader.getAttributeList());
		// attributeTags.put(new AttributeTag(PrivateTag.CREATOR.getGroup(),
		// PrivateTag.CREATOR.getElement()),
		// DICOM_CREATOR);
		// attributeTags.put(
		// new AttributeTag(PrivateTag.ORIGINAL_HEIGHT.getGroup(),
		// PrivateTag.ORIGINAL_HEIGHT.getElement()),
		// Integer.toString(heightInput));
		// attributeTags.put(
		// new AttributeTag(PrivateTag.ORIGINAL_WIDTH.getGroup(),
		// PrivateTag.ORIGINAL_WIDTH.getElement()),
		// Integer.toString(heightInput));
		//
		// final String sourceFileNameBase =
		// FilenameUtils.getBaseName(origFile.getAbsolutePath());
		// final File[] dispersedFiles = new File[ipsWriter.length];
		// for (int i = 0; i < ipsWriter.length; i++) {
		//
		// final DicomWriter dicomWriter = new
		// DicomWriter(ipsWriter[i].getBufferedImage());
		// attributeTags.put(
		// new AttributeTag(PrivateTag.DISPERSAL_INDEX.getGroup(),
		// PrivateTag.DISPERSAL_INDEX.getElement()),
		// Integer.toString(i));
		// attributeTags.put(
		// new AttributeTag(PrivateTag.SHAMIR_SHARED_KEY.getGroup(),
		// PrivateTag.SHAMIR_SHARED_KEY.getElement()),
		// Converter.convertUnsignedByteToHexString(unsignedSecretShares[i]));
		//
		// final File dispersedDicomFile = new File(outputDir.getAbsolutePath()
		// + File.separator + sourceFileNameBase
		// + ".pacs" + Integer.toString(i + 1) + ".dcm");
		//
		// dicomWriter.importToDicom(dispersedDicomFile, attributeTags);
		// dispersedFiles[i] = dispersedDicomFile;
		//
		// }

		return dispersedFiles;
	}

	private int[][] generateSalt(final Random random, final int threshold, final int numBands) {

		final int[][] salt = new int[threshold][numBands];
		for (int i = 0; i < salt.length; i++) {
			final int r = random.nextInt(256);
			for (int j = 0; j < salt[i].length; j++) {
				salt[i][j] = r;
			}
		}

		return salt;
	}
}
