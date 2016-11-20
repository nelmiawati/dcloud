package id.ac.polibatam.mj.dcloud.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

	private static final Logger LOG = Logger.getLogger(JCEKSKeyTest.class);

	private static final String KEYSTORE_FILE_NAME = "dcloud-keystore.jceks";

	private static final String KEYSTORE_PASSWORD = "dcloud123";

	private static final String DUMMY_ALIAS = "dummy";

	private static final String DUMMY_PASSWORD = "dcloud123";

	private JCEKSKey keys = null;

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
			throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
		} catch (DcloudInvalidDataException e) {
			throw new DcloudInvalidDataRuntimeException(e.getMessage(), e);
		}
	}

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

			final int nbAlias = this.keys.getAliases().size();

			final String alias = "test";
			final byte[] secret = "testSecret".getBytes("UTF-8");
			final String secretPwd = "testSecretPwd";

			this.keys.setSecretKey(alias, secret, secretPwd);
			assertEquals(nbAlias + 1, this.keys.getAliases().size());
			assertEquals(secret, this.keys.getSecretKey(alias, secretPwd));

			this.keys.deleteSecretKey(alias, secretPwd);
			assertEquals(nbAlias, this.keys.getAliases().size());

		} catch (Throwable t) {
			t1 = t;
			LOG.error(t.getMessage(), t);
		}
		assertNull(t1);

	}

}
