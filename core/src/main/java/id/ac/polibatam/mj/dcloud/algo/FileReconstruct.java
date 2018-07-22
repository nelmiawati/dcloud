/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.algo;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import id.ac.polibatam.mj.dcloud.io.DcloudFileInputStream;
import id.ac.polibatam.mj.dcloud.io.DcloudHeader;
import id.ac.polibatam.mj.dcloud.math.GFMath;
import id.ac.polibatam.mj.dcloud.math.GFMatrix;
import id.ac.polibatam.mj.dcloud.math.IDAMath;
import id.ac.polibatam.mj.dcloud.util.ArrayUtils;
import id.ac.polibatam.mj.dcloud.util.Converter;
import id.ac.polibatam.mj.dcloud.util.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author mia
 */
public class FileReconstruct {

    private static final Logger LOG = Logger.getLogger(FileReconstruct.class);
    private final GFMatrix gfMatrix = new GFMatrix();
    private final GFMath gfMath = GFMath.getInstance();

    public FileReconstruct() throws DcloudInvalidDataException {

    }

    public void reconstruct(final File[] dispersedFiles, final File reconstructedFile, final boolean useSalt)
            throws DcloudInvalidDataException, DcloudSystemInternalException {

        if (null == dispersedFiles || null == reconstructedFile) {
            throw new DcloudInvalidDataException("INVALID null dispersedFiles or reconstructedFile");
        }

        if (dispersedFiles.length < 2) {
            throw new DcloudInvalidDataException("nbDispersedFiles length has to be > 1");
        }

        byte[] origMd5 = null;
        FileOutputStream fos = null;
        final DcloudFileInputStream[] arDis = new DcloudFileInputStream[dispersedFiles.length];
        try {

            for (int i = 0; i < dispersedFiles.length; i++) {
                if (null == dispersedFiles[i] || !dispersedFiles[i].exists()) {
                    throw new DcloudInvalidDataException(
                            "INVALID dispersedFile[" + i + "]=[" + dispersedFiles[i].getAbsolutePath() + "]");
                }
                arDis[i] = new DcloudFileInputStream(dispersedFiles[i]);
            }

            origMd5 = arDis[0].getHeader().getMd5();
            final int[][] keys = this.validateDispersedFilesAndReconstructSecret(arDis);
            final int[] vSecretKey = keys[0];
            final int[] vSecretKeyStar = keys[1];
            final int randomSeed = IDAMath.generateRandomSeed(this.gfMath, vSecretKey);

            if (LOG.isTraceEnabled()) {
                LOG.trace("secret=[" + Arrays.toString(vSecretKey) + "]");
                LOG.trace("secretStar=[" + Arrays.toString(vSecretKeyStar) + "]");
                LOG.trace("randomSeed=[" + randomSeed + "]");
            }

            final Random random = new Random(randomSeed);
            fos = new FileOutputStream(reconstructedFile);
            boolean readMore = true;
            byte[] bWrite = null;
            while (readMore) {

                int[][] mRead = new int[arDis.length][];
                for (int i = 0; i < arDis.length; i++) {
                    byte[] bRead = new byte[1];
                    final int nbRead = arDis[i].read(bRead);
                    if (nbRead < 1) {
                        readMore = false;
                        break;
                    } else {
                        mRead[i] = Converter.convertSignedByteToUnsignedByte(bRead);
                    }
                }

                if (readMore) {
                    int[][] mWrite = null;
                    if (useSalt) {
                        final int[][] bufferWriterSalt = this.gfMatrix.solveVandermondeSystemT(vSecretKeyStar, mRead);
                        final int[][] salt = IDAMath.generateSalt(random, arDis.length, 1);
                        // if (LOG.isTraceEnabled()) {
                        // LOG.trace("salt=[" + Arrays.deepToString(salt) +
                        // "]");
                        // }
                        mWrite = this.gfMatrix.substract(bufferWriterSalt, salt);
                    } else {
                        mWrite = this.gfMatrix.solveVandermondeSystemT(vSecretKeyStar, mRead);
                    }
                    if (null != bWrite) {
                        // if (LOG.isTraceEnabled()) {
                        // LOG.trace("Write bWrite=[" +
                        // Converter.convertSignedByteToHexString(bWrite) +
                        // "]");
                        // }
                        fos.write(bWrite);
                        fos.flush();
                    }
                    bWrite = Converter.convertUnsignedByteToSignedByte(this.gfMatrix.getColumn(mWrite, 0));
                }

            }

            if (null != bWrite) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Write last bWrite with padding=[" + arDis[0].getHeader().getPaddLen() + "], bWrite=["
                            + Converter.convertSignedByteToHexString(bWrite) + "]");
                }
                fos.write(bWrite, 0, bWrite.length - arDis[0].getHeader().getPaddLen());
                fos.flush();
            }

        } catch (DcloudInvalidDataRuntimeException e) {
            throw new DcloudInvalidDataException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new DcloudInvalidDataException(e.getMessage(), e);
        } catch (IOException e) {
            throw new DcloudSystemInternalException(e.getMessage(), e);
        } finally {

            for (DcloudFileInputStream dis : arDis) {
                if (null != dis) {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        if (LOG.isEnabledFor(Level.WARN)) {
                            LOG.warn(e.getMessage());
                        }
                    }
                }
            }

            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    if (LOG.isEnabledFor(Level.WARN)) {
                        LOG.warn(e.getMessage());
                    }
                }
            }

        }

        // verify md5 reconFile
        final byte[] reconMd5 = FileUtils.getMd5(reconstructedFile);
        if (LOG.isTraceEnabled()) {
            LOG.trace("origMd5=[" + Converter.convertSignedByteToHexString(origMd5) + "]");
            LOG.trace("reconMd5=[" + Converter.convertSignedByteToHexString(reconMd5) + "]");
        }
        if (!Arrays.equals(reconMd5, origMd5)) {
            throw new DcloudInvalidDataException(
                    "INVALID dispersedFile, reconMd5=[" + Converter.convertSignedByteToHexString(reconMd5)
                            + "] <> origMd5=[" + Converter.convertSignedByteToHexString(origMd5) + "]");
        }

    }

    private int[][] validateDispersedFilesAndReconstructSecret(final DcloudFileInputStream[] arDis)
            throws DcloudInvalidDataException {

        final List<Integer> idxs = new ArrayList<Integer>();
        final int[][] secretShares = new int[arDis.length][];

        // Validate headers
        for (int i = 0; i < arDis.length; i++) {

            final DcloudHeader header = arDis[i].getHeader();
            final Integer idx = Integer.valueOf(header.getDispersalIdx());
            if (idxs.contains(idx)) {
                throw new DcloudInvalidDataException(
                        "INVALID dispersedFiles[" + i + "]. Duplicate dispersalIdx=[" + idx + "]");
            } else {
                idxs.add(idx);
            }

            if (arDis.length != header.getThreshold()) {
                throw new DcloudInvalidDataException("INVALID dispersedFiles[" + i + "]. threshold=["
                        + header.getThreshold() + "] different from numberOfDispersedFiles=[" + arDis.length + "]");
            }

            if (arDis[0].getHeader().getPaddLen() != header.getPaddLen()) {
                throw new DcloudInvalidDataException(
                        "INVALID dispersedFiles[" + i + "]. padding=[" + header.getPaddLen()
                                + "] different from padding0=[" + arDis[0].getHeader().getPaddLen() + "]");
            }

            if (!Arrays.equals(arDis[0].getHeader().getMd5(), header.getMd5())) {
                throw new DcloudInvalidDataException("INVALID dispersedFiles[" + i + "]. md5=["
                        + Converter.convertSignedByteToHexString(header.getMd5()) + "] different from md50=["
                        + Converter.convertSignedByteToHexString(arDis[0].getHeader().getMd5()) + "]");
            }

            secretShares[i] = Converter.convertSignedByteToUnsignedByte(header.getVSecretShare());

        }

        // Reconstruct secret key
        if (LOG.isTraceEnabled()) {
            LOG.trace("secretShares=[" + Arrays.deepToString(secretShares) + "]");
        }
        final SecretSharing ss = new SecretSharing();
        final int[] vSecretKey = ss.reconstruct(secretShares);
        if (!ArrayUtils.isUnique(vSecretKey)) {
            throw new DcloudInvalidDataException("NON-UNIQUE vSecretKey=[" + Arrays.toString(vSecretKey) + "]");
        }

        // validate padding
        if (arDis[0].getHeader().getPaddLen() > vSecretKey.length - 1) {
            throw new DcloudInvalidDataException("INVALID dispersedFile, paddLen=[" + arDis[0].getHeader().getPaddLen()
                    + "] > " + (vSecretKey.length - 1));
        }

        // Build secret key star
        final int[] vSecretKeyStar = new int[arDis.length];
        for (int i = 0; i < idxs.size(); i++) {
            vSecretKeyStar[i] = vSecretKey[idxs.get(i).intValue()];
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("secret=[" + Arrays.toString(vSecretKey) + "]");
            LOG.trace("secretStar=[" + Arrays.toString(vSecretKeyStar) + "]");
        }

        return new int[][]{vSecretKey, vSecretKeyStar};

    }
}
