/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;

/**
 *
 * @author mia
 */
public class ConverterTest {

	@Before
	public void before() {

	}

	@Test
	public void testConvertSignedByteToHexString() {

		final byte[] signedByte = new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x1C, (byte) 0x7E,
				(byte) 0x7F, (byte) 0x80, (byte) 0xFE, (byte) 0xFF };

		assertNull(Converter.convertSignedByteToHexString(null));
		assertEquals("", Converter.convertSignedByteToHexString(new byte[] {}));
		assertEquals("0001101C7E7F80FEFF", Converter.convertSignedByteToHexString(signedByte));

	}

	@Test
	public void testConvertUnsignedByteToHexString() {

		final int[] unsignedByte1 = new int[] { 0, 1, 16, 28, 126, 127, 128, 254, 255 };
		final int[] unsignedByte2 = new int[] { 0, 1, 16, 28, 126, 127, 128, 254, 256, 255 };
		final int[] unsignedByte3 = new int[] { 0, 1, 16, 28, 126, 127, 128, 254, -5, 255 };

		assertNull(Converter.convertUnsignedByteToHexString(null));
		assertEquals("", Converter.convertUnsignedByteToHexString(new int[] {}));
		assertEquals("0001101C7E7F80FEFF", Converter.convertUnsignedByteToHexString(unsignedByte1));

		DcloudInvalidDataRuntimeException e1 = null;
		try {
			Converter.convertUnsignedByteToHexString(unsignedByte2);
		} catch (DcloudInvalidDataRuntimeException ex) {
			e1 = ex;
		}
		assertNotNull(e1);

		DcloudInvalidDataRuntimeException e2 = null;
		try {
			Converter.convertUnsignedByteToHexString(unsignedByte3);
		} catch (DcloudInvalidDataRuntimeException ex) {
			e2 = ex;
		}
		assertNotNull(e2);

	}

	@Test
	public void testConvertHexStringToSignedByte() {

		final byte[] extectedB = new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x1C, (byte) 0x7E,
				(byte) 0x7F, (byte) 0x80, (byte) 0xFE, (byte) 0xFF };

		assertNull(Converter.convertHexStringToSignedByte(null));
		assertTrue(Converter.convertHexStringToSignedByte("").length == 0);
		assertTrue(Arrays.equals(extectedB, Converter.convertHexStringToSignedByte("0001101C7E7F80FEFF")));
		assertTrue(Arrays.equals(extectedB, Converter.convertHexStringToSignedByte("00 01 10 1C 7E 7F 80 FE FF")));

		DcloudInvalidDataRuntimeException e1 = null;
		try {
			Converter.convertHexStringToSignedByte("0001101C7E7F80FEF");
		} catch (DcloudInvalidDataRuntimeException ex) {
			e1 = ex;
		}
		assertNotNull(e1);

		DcloudInvalidDataRuntimeException e2 = null;
		try {
			Converter.convertHexStringToSignedByte("0 0 01 1 01 C7 E7 F8 0F EF");
		} catch (DcloudInvalidDataRuntimeException ex) {
			e2 = ex;
		}
		assertNotNull(e2);

	}

	@Test
	public void testConvertSignedByteToUnsignedByte() {

		assertEquals(0, Converter.convertSignedByteToUnsignedByte((byte) 0x00));
		assertEquals(1, Converter.convertSignedByteToUnsignedByte((byte) 0x01));
		assertEquals(16, Converter.convertSignedByteToUnsignedByte((byte) 0x10));
		assertEquals(28, Converter.convertSignedByteToUnsignedByte((byte) 0x1C));
		assertEquals(126, Converter.convertSignedByteToUnsignedByte((byte) 0x7E));
		assertEquals(127, Converter.convertSignedByteToUnsignedByte((byte) 0x7F));
		assertEquals(128, Converter.convertSignedByteToUnsignedByte((byte) 0x80));
		assertEquals(254, Converter.convertSignedByteToUnsignedByte((byte) 0xFE));
		assertEquals(255, Converter.convertSignedByteToUnsignedByte((byte) 0xFF));

		final byte[] signedByte1D = null;
		final byte[][] signedByte2D = null;

		assertNull(Converter.convertSignedByteToUnsignedByte(signedByte1D));
		assertNull(Converter.convertSignedByteToUnsignedByte(signedByte2D));

		assertTrue(Arrays.equals(new int[] {}, Converter.convertSignedByteToUnsignedByte(new byte[] {})));
		assertTrue(Arrays.deepEquals(new int[][] {}, Converter.convertSignedByteToUnsignedByte(new byte[][] {})));

		assertTrue(Arrays.equals(new int[] { 0, 1, 16, 28, 126, 127, 128, 254, 255 },
				Converter.convertSignedByteToUnsignedByte(new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x10,
						(byte) 0x1C, (byte) 0x7E, (byte) 0x7F, (byte) 0x80, (byte) 0xFE, (byte) 0xFF })));

		assertTrue(Arrays.deepEquals(
				new int[][] { new int[] { 0, 1, 16 }, new int[] { 28, 126, 127 }, new int[] { 128, 254, 255 } },
				Converter.convertSignedByteToUnsignedByte(
						new byte[][] { new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x10 },
								new byte[] { (byte) 0x1C, (byte) 0x7E, (byte) 0x7F },
								new byte[] { (byte) 0x80, (byte) 0xFE, (byte) 0xFF } })));

	}

	@Test
	public void testConvertUnsignedByteToSignedByte() {

		assertEquals((byte) 0x00, Converter.convertUnsignedByteToSignedByte(0));
		assertEquals((byte) 0x01, Converter.convertUnsignedByteToSignedByte(1));
		assertEquals((byte) 0x10, Converter.convertUnsignedByteToSignedByte(16));
		assertEquals((byte) 0x1C, Converter.convertUnsignedByteToSignedByte(28));
		assertEquals((byte) 0x7E, Converter.convertUnsignedByteToSignedByte(126));
		assertEquals((byte) 0x7F, Converter.convertUnsignedByteToSignedByte(127));
		assertEquals((byte) 0x80, Converter.convertUnsignedByteToSignedByte(128));
		assertEquals((byte) 0xFE, Converter.convertUnsignedByteToSignedByte(254));
		assertEquals((byte) 0xFF, Converter.convertUnsignedByteToSignedByte(255));

		DcloudInvalidDataRuntimeException e1 = null;
		try {
			Converter.convertUnsignedByteToSignedByte(-1);
		} catch (DcloudInvalidDataRuntimeException ex) {
			e1 = ex;
		}
		assertNotNull(e1);

		DcloudInvalidDataRuntimeException e2 = null;
		try {
			Converter.convertUnsignedByteToSignedByte(256);
		} catch (DcloudInvalidDataRuntimeException ex) {
			e2 = ex;
		}
		assertNotNull(e2);

		final int[] unsignedByte1D = null;
		final int[][] unsignedByte2D = null;

		assertNull(Converter.convertUnsignedByteToSignedByte(unsignedByte1D));
		assertNull(Converter.convertUnsignedByteToSignedByte(unsignedByte2D));

		assertTrue(Arrays.equals(new byte[] {}, Converter.convertUnsignedByteToSignedByte(new int[] {})));
		assertTrue(Arrays.deepEquals(new byte[][] {}, Converter.convertUnsignedByteToSignedByte(new int[][] {})));

		assertTrue(Arrays.equals(
				new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x1C, (byte) 0x7E, (byte) 0x7F, (byte) 0x80,
						(byte) 0xFE, (byte) 0xFF },
				Converter.convertUnsignedByteToSignedByte(new int[] { 0, 1, 16, 28, 126, 127, 128, 254, 255 })));

		assertTrue(Arrays.deepEquals(
				new byte[][] { new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x10 },
						new byte[] { (byte) 0x1C, (byte) 0x7E, (byte) 0x7F },
						new byte[] { (byte) 0x80, (byte) 0xFE, (byte) 0xFF } },
				Converter.convertUnsignedByteToSignedByte(new int[][] { new int[] { 0, 1, 16 },
						new int[] { 28, 126, 127 }, new int[] { 128, 254, 255 } })));

	}

}
