package id.ac.polibatam.mj.dcloud.io;

import id.ac.polibatam.mj.dcloud.util.Converter;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DcloudHeaderTest {

    private static final Logger LOG = Logger.getLogger(DcloudHeaderTest.class);

    private DcloudHeader header0;

    private DcloudHeader header1;

    private DcloudHeader header1CopyRef;

    private DcloudHeader header1CopyClone;

    private DcloudHeader header2;

    private String header1Str = "20020100210500000000002201042301052401032510DB89BB5CEAB87F9C0FCC2AB36C189C2C";

    private String header2Str = "200201002105FF000A00002201402301802401FF2510DB89BB5CEAB87F9C0FCC2AB36C189C2C";

    private String headerBadInvalidThStr = "200201002105FF000A00002201402301002401FF2510DB89BB5CEAB87F9C0FCC2AB36C189C2C";

    private String headerBadInvalidVSecretShareLenStr = "200201002104FF000A00002201402301802401FF2510DB89BB5CEAB87F9C0FCC2AB36C189C2C";

    private String headerBadExtraLenStr = "200201002105FF000A00002201402301802401FF2510DB89BB5CEAB87F9C0FCC2AB36C189C2C00";

    @Before
    public void before() {
        header0 = new DcloudHeader();

        header1 = new DcloudHeader();
        header1.setVSecretShare(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        header1.setDispersalIdx(4);
        header1.setThreshold(5);
        header1.setPaddLen(3);
        header1.setMd5(Converter.convertHexStringToSignedByte("DB89BB5CEAB87F9C0FCC2AB36C189C2C"));

        header1CopyRef = header1;
        header1CopyClone = (DcloudHeader) header1.clone();

        header2 = new DcloudHeader();
        header2.setVSecretShare(new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x0A, (byte) 0x00, (byte) 0x00});
        header2.setDispersalIdx(64);
        header2.setThreshold(128);
        header2.setPaddLen(255);
        header2.setMd5(Converter.convertHexStringToSignedByte("DB89BB5CEAB87F9C0FCC2AB36C189C2C"));
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
            DcloudHeader.parseHeader(Converter.convertHexStringToSignedByte(headerBadInvalidVSecretShareLenStr));
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
