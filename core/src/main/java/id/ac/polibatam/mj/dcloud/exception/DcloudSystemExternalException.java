package id.ac.polibatam.mj.dcloud.exception;

/**
 * CloudPacsSystemInternalException
 *
 * @author mia
 */
public class DcloudSystemExternalException extends BaseDcloudException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 456360841355067887L;

	/**
	 * Constructor.
	 */
	public DcloudSystemExternalException() {
		super(DcloudExceptionCode.SYSTEM_EXTERNAL_EXCEPTION);
	}

	/**
	 * Constructor.
	 *
	 * @param msg
	 *            exception message.
	 */
	public DcloudSystemExternalException(final String msg) {
		super(DcloudExceptionCode.SYSTEM_EXTERNAL_EXCEPTION, msg);
	}

	/**
	 * Constructor.
	 *
	 * @param t
	 *            another exception to be wrapped.
	 */
	public DcloudSystemExternalException(final Throwable t) {
		super(DcloudExceptionCode.SYSTEM_EXTERNAL_EXCEPTION, t);
	}

	/**
	 * Constructor.
	 *
	 * @param msg
	 *            exception message.
	 * @param t
	 *            another exception to be wrapped.
	 */
	public DcloudSystemExternalException(final String msg, final Throwable t) {
		super(DcloudExceptionCode.SYSTEM_EXTERNAL_EXCEPTION, msg, t);
	}
}
