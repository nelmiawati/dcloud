package id.ac.polibatam.mj.dcloud.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.util.Converter;

public class DcloudFileIOStreamTest {

	private static final Logger LOG = Logger.getLogger(DcloudFileIOStreamTest.class);

	@Test
	public void ioDcloudFile() {

		final File dcloudFile = new File("test.dc");
		final String headerStr = "200201002105FF000A00002201402301802401FF2510DB89BB5CEAB87F9C0FCC2AB36C189C2C";
		final String contentStr = "01234567890123456789012345678901234567890123456789012345678901234567890123456789";

		Throwable t1 = null;
		DcloudFileOutputStream dos = null;
		try {

			final DcloudHeader header = DcloudHeader.parseHeader(Converter.convertHexStringToSignedByte(headerStr));
			dos = new DcloudFileOutputStream(dcloudFile, header);
			dos.write(Converter.convertHexStringToSignedByte(contentStr));

		} catch (DcloudInvalidDataException e) {
			t1 = e;
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			t1 = e;
			LOG.error(e.getMessage(), e);
		} finally {
			if (null != dos) {
				try {
					dos.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage(), e);
				}
			}
		}
		assertNull(t1);

		Throwable t2 = null;
		DcloudFileInputStream dis = null;
		try {

			dis = new DcloudFileInputStream(dcloudFile);
			assertEquals(headerStr, Converter.convertSignedByteToHexString(dis.getHeader().generateHeader()));
			final byte[] content = Converter.convertHexStringToSignedByte(contentStr);

			int b;
			int i = 0;
			while ((b = dis.read()) > -1) {
				assertEquals(content[i++], (byte) b);
			}
			assertEquals(content.length, i);

		} catch (DcloudSystemInternalException e) {
			t2 = e;
			LOG.error(e.getMessage(), e);
		} catch (DcloudInvalidDataException e) {
			t2 = e;
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			t2 = e;
			LOG.error(e.getMessage(), e);
		} finally {
			if (null != dis) {
				try {
					dis.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage(), e);
				}
			}
		}
		assertNull(t2);
		
		assertTrue(dcloudFile.delete());

	}

}
