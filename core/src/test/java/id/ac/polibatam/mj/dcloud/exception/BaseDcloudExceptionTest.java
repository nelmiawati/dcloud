package id.ac.polibatam.mj.dcloud.exception;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Test;

public class BaseDcloudExceptionTest {

	public static final Logger LOG = Logger.getLogger(BaseDcloudException.class);

	@Test
	public void testBaseDcloudException() {

		final BaseDcloudException e1 = new DcloudGenericException();
		LOG.trace(e1.getMessage(), e1);
		assertNotNull(e1.toString());
		assertNotNull(e1.getMessage());
		final BaseDcloudException e2 = new DcloudInvalidConfigurationException("details");
		LOG.trace(e2.getMessage(), e2);
		assertNotNull(e2.toString());
		assertNotNull(e2.getMessage());
		final BaseDcloudException e3 = new DcloudInvalidDataException(e2);
		LOG.trace(e3.getMessage(), e3);
		assertNotNull(e3.toString());
		assertNotNull(e3.getMessage());
		final BaseDcloudException e4 = new DcloudSystemInternalException("details", e2);
		LOG.trace(e4.getMessage(), e4);
		assertNotNull(e4.toString());
		assertNotNull(e4.getMessage());

	}

}
