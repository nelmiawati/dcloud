package id.ac.polibatam.mj.dcloud.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import id.ac.polibatam.mj.dcloud.io.DcloudHeader;
import id.ac.polibatam.mj.dcloud.util.Converter;

public class DcloudHeaderTest {

	private static final Logger LOG = Logger.getLogger(DcloudHeaderTest.class);

	private DcloudHeader header0;

	private DcloudHeader header1;

	private DcloudHeader header1CopyRef;

	private DcloudHeader header1CopyClone;

	private DcloudHeader header2;

	private String header1Str = "2002010021050000000000220104230105240103";

	private String header2Str = "200201002105FF000A00002201402301802401FF";

	private String headerBadInvalidThStr = "200201002105FF000A00002201402301002401FF";

	private String headerBadInvalidVSekretKeyDistLenStr = "200201002104FF000A00002201402301802401FF";

	private String headerBadExtraLenStr = "200201002105FF000A00002201402301802401FF00";

	@Before
	public void before() {
		header0 = new DcloudHeader();

		header1 = new DcloudHeader();
		header1.setVSecretKeyDist(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 });
		header1.setDispersalIdx(4);
		header1.setThreshold(5);
		header1.setPaddLen(3);

		header1CopyRef = header1;
		header1CopyClone = (DcloudHeader) header1.clone();

		header2 = new DcloudHeader();
		header2.setVSecretKeyDist(new byte[] { (byte) 0xFF, (byte) 0x00, (byte) 0x0A, (byte) 0x00, (byte) 0x00 });
		header2.setDispersalIdx(64);
		header2.setThreshold(128);
		header2.setPaddLen(255);
	}

	@Test
	public void testObjects() {

		Throwable t1 = null;
		try {
			LOG.trace(header0.toString());
			LOG.trace(header1.toString());
			LOG.trace(header2.toString());

			LOG.trace(header0.hashCode());
			LOG.trace(header1.hashCode());
			LOG.trace(header2.hashCode());

			assertTrue(header1.equals(header1CopyRef));
			assertTrue(header1.equals(header1CopyClone));
			assertFalse(header1.equals(header2));

		} catch (Throwable t) {
			t1 = t;
			LOG.error(t.getMessage(), t);
		}
		assertNull(t1);
	}

	@Test
	public void testGenerateHeader() {

		Throwable t1 = null;
		try {
			LOG.trace(Converter.convertSignedByteToHexString(header0.generateHeader()));

		} catch (Throwable t) {
			// LOG.error(t.getMessage(), t);
			t1 = t;
		}
		assertNotNull(t1);

		Throwable t2 = null;
		try {
			assertEquals(header1Str, Converter.convertSignedByteToHexString(header1.generateHeader()));
			LOG.trace(Converter.convertSignedByteToHexString(header1.generateHeader()));

			assertEquals(header1Str, Converter.convertSignedByteToHexString(header1.generateHeader()));
			LOG.trace(Converter.convertSignedByteToHexString(header1CopyRef.generateHeader()));

			assertEquals(header1Str, Converter.convertSignedByteToHexString(header1.generateHeader()));
			LOG.trace(Converter.convertSignedByteToHexString(header1CopyClone.generateHeader()));

			assertEquals(header2Str, Converter.convertSignedByteToHexString(header2.generateHeader()));
			LOG.trace(Converter.convertSignedByteToHexString(header2.generateHeader()));

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			t2 = t;
		}
		assertNull(t2);

	}

	@Test
	public void testParseHeader() {

		Throwable t1 = null;
		try {
			assertEquals(header1, DcloudHeader.parseHeader(Converter.convertHexStringToSignedByte(header1Str)));
			assertEquals(header2, DcloudHeader.parseHeader(Converter.convertHexStringToSignedByte(header2Str)));

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			t1 = t;
		}
		assertNull(t1);

		Throwable t2 = null;
		try {
			DcloudHeader.parseHeader(Converter.convertHexStringToSignedByte(headerBadInvalidThStr));
		} catch (Throwable t) {
			// LOG.error(t.getMessage(), t);
			t2 = t;
		}
		assertNotNull(t2);

		Throwable t3 = null;
		try {
			DcloudHeader.parseHeader(Converter.convertHexStringToSignedByte(headerBadInvalidVSekretKeyDistLenStr));
		} catch (Throwable t) {
			// LOG.error(t.getMessage(), t);
			t3 = t;
		}
		assertNotNull(t3);

		Throwable t4 = null;
		try {
			DcloudHeader.parseHeader(Converter.convertHexStringToSignedByte(headerBadExtraLenStr));
		} catch (Throwable t) {
			// LOG.error(t.getMessage(), t);
			t4 = t;
		}
		assertNotNull(t4);
	}
}
