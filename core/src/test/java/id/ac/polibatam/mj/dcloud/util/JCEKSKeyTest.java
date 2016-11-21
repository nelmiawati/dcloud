package id.ac.polibatam.mj.dcloud.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudSystemInternalRuntimeException;

public class JCEKSKeyTest {

	// --------------------------------------------------------------------------
	// Static members
	// --------------------------------------------------------------------------
	private static final Logger LOG = Logger.getLogger(JCEKSKeyTest.class);

	private static final String KEYSTORE_FILE_NAME = "dcloud-keystore.jceks";

	private static final String KEYSTORE_PASSWORD = "keystore123";

	private static final String DUMMY_ALIAS = "dummy";

	private static final String DUMMY_PASSWORD = "dummy123";
	// -------------------------------------------------------------------------
	// Members
	// -------------------------------------------------------------------------

	private JCEKSKey keys = null;
	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// Implements interface <IMyInterface>
	// -------------------------------------------------------------------------

	// --------------------------------------------------------------------------
	// Methods
	// --------------------------------------------------------------------------

	@Test
	public void testListAliases() {

		Throwable t1 = null;
		try {
			final List<String> aliases = this.keys.getAliases();
			LOG.trace(aliases);
		} catch (Throwable t) {
			t1 = t;
			LOG.error(t.getMessage(), t);
		}
		assertNull(t1);

	}

	@Test
	public void testContainsAlias() {

		Throwable t1 = null;
		try {
			assertTrue(this.keys.containsAlias(DUMMY_ALIAS));
			assertFalse(this.keys.containsAlias("toto"));
		} catch (Throwable t) {
			t1 = t;
			LOG.error(t.getMessage(), t);
		}
		assertNull(t1);

	}

	@Test
	public void testGetSecretKey() {

		Throwable t1 = null;
		try {
			final byte[] secret = this.keys.getSecretKey(DUMMY_ALIAS, DUMMY_PASSWORD);
			LOG.trace(Converter.convertSignedByteToHexString(secret));
			assertNotNull(secret);
		} catch (Throwable t) {
			t1 = t;
			LOG.error(t.getMessage(), t);
		}
		assertNull(t1);

	}

	@Test
	public void testSetSecretKey() {

		Throwable t1 = null;
		try {

			final List<String> aliases = this.keys.getAliases();
			final int nbAlias = aliases.size();
			LOG.trace(aliases);

			final String alias = "test";
			final byte[] secret = new byte[] { (byte) 0x00, (byte) 0x10, (byte) 0x0A, (byte) 0xFF };
			final String secretPwd = "testSecretPwd";

			this.keys.setSecretKey(alias, secret, secretPwd);
			assertEquals(nbAlias + 1, this.keys.getAliases().size());
			LOG.trace(Converter.convertSignedByteToHexString(this.keys.getSecretKey(alias, secretPwd)));
			assertEquals(Converter.convertSignedByteToHexString(secret),
					Converter.convertSignedByteToHexString(this.keys.getSecretKey(alias, secretPwd)));

			this.keys.deleteSecretKey(alias);
			assertEquals(nbAlias, this.keys.getAliases().size());

		} catch (DcloudSystemInternalException t) {
			t1 = t;
			LOG.error(t.getMessage(), t);
		}
		assertNull(t1);

	}
	// --------------------------------------------------------------------------
	// Any other separator such as "Utility methods", etc.
	// --------------------------------------------------------------------------

	@Before
	public void before() {

		try {
			final URL url = ClassLoader.getSystemResource(KEYSTORE_FILE_NAME);
			if (null == url) {
				throw new DcloudInvalidConfigurationRuntimeException(
						"LOADING NON-EXIST config-file=[" + KEYSTORE_FILE_NAME + "]");
			}

			this.keys = new JCEKSKey(new File(url.getFile()), KEYSTORE_PASSWORD);
		} catch (DcloudSystemInternalException e) {
			LOG.error(e.getMessage(), e);
			throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
		} catch (DcloudInvalidDataException e) {
			LOG.error(e.getMessage(), e);
			throw new DcloudInvalidDataRuntimeException(e.getMessage(), e);
		}
	}
}