/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.ac.polibatam.mj.dcloud.algo;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.util.Converter;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author mia
 */
public class SecretSharingTest {

    private static final Logger LOG = Logger.getLogger(SecretSharingTest.class);

    @Before
    public void before() {

    }

    @Test
    public void test1byte() throws DcloudInvalidDataException {

        final SecretSharing ss = new SecretSharing();
        final int n = 10;
        final int t = 5;
        final int secret = 250;
        final int[][] sharedKeys = ss.split(n, t, secret);
        LOG.debug(Arrays.deepToString(sharedKeys));

        final int[][] reconstructShared = new int[][]{sharedKeys[5], sharedKeys[1], sharedKeys[2], sharedKeys[9], sharedKeys[4], sharedKeys[6]};
        final int[] reconstructKey = ss.reconstruct(reconstructShared);
        LOG.debug(Arrays.toString(reconstructKey));
        assertEquals(1, reconstructKey.length);
        assertEquals(secret, reconstructKey[0]);
    }

    @Test
    public void testNbyte() throws DcloudInvalidDataException {

        final SecretSharing ss = new SecretSharing();
        final int n = 10;
        final int t = 5;
        final byte[] signedSecret = new byte[]{(byte) 0x03, (byte) 0x0F, (byte) 0x06, (byte) 0x02, (byte) 0x04, (byte) 0x0A};
        LOG.debug(Arrays.toString(signedSecret));
        final int[] unsignedSecret = Converter.convertSignedByteToUnsignedByte(signedSecret);
        LOG.debug(Arrays.toString(unsignedSecret));
        final int[][] unsignedSharedKeys = ss.split(n, t, unsignedSecret);
        LOG.debug(Arrays.deepToString(unsignedSharedKeys));

        final int[][] usignedReconstructShared = new int[][]{unsignedSharedKeys[5], unsignedSharedKeys[1], unsignedSharedKeys[2], unsignedSharedKeys[9], unsignedSharedKeys[4], unsignedSharedKeys[6]};
        LOG.debug(Arrays.deepToString(usignedReconstructShared));
        final int[] unsignedReconstructKey = ss.reconstruct(usignedReconstructShared);
        final byte[] signedReconstructKey = Converter.convertUnsignedByteToSignedByte(unsignedReconstructKey);
        LOG.debug(Arrays.toString(unsignedReconstructKey));
        LOG.debug(Arrays.toString(signedReconstructKey));
        assertTrue(Arrays.equals(signedSecret, signedReconstructKey));
    }


}
