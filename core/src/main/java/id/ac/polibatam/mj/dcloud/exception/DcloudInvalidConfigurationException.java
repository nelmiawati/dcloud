package id.ac.polibatam.mj.dcloud.exception;

/**
 * CloudPacsInvalidDataException
 *
 * @author mia
 */
public class DcloudInvalidConfigurationException extends BaseDcloudException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6844144892419559476L;

	/**
	 * Constructor.
	 */
	public DcloudInvalidConfigurationException() {
		super(DcloudExceptionCode.INVALID_CONFIGURATION_EXCEPTION);
	}

	/**
	 * Constructor.
	 *
	 * @param msg
	 *            exception message.
	 */
	public DcloudInvalidConfigurationException(final String msg) {
		super(DcloudExceptionCode.INVALID_CONFIGURATION_EXCEPTION, msg);
	}

	/**
	 * Constructor.
	 *
	 * @param t
	 *            another exception to be wrapped.
	 */
	public DcloudInvalidConfigurationException(final Throwable t) {
		super(DcloudExceptionCode.INVALID_CONFIGURATION_EXCEPTION, t);
	}

	/**
	 * Constructor.
	 *
	 * @param msg
	 *            exception message.
	 * @param t
	 *            another exception to be wrapped.
	 */
	public DcloudInvalidConfigurationException(final String msg, final Throwable t) {
		super(DcloudExceptionCode.INVALID_CONFIGURATION_EXCEPTION, msg, t);
	}
}
