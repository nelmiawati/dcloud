package id.ac.polibatam.mj.dcloud.exception;

/**
 * CloudPacsGenericException
 *
 * @author mia
 */
public class DcloudGenericException extends BaseDcloudException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5124941628980671397L;

    /**
     * Constructor.
     */
    public DcloudGenericException() {
        super(DcloudExceptionCode.GENERIC_EXCEPTION);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     */
    public DcloudGenericException(final String msg) {
        super(DcloudExceptionCode.GENERIC_EXCEPTION, msg);
    }

    /**
     * Constructor.
     *
     * @param t another exception to be wrapped.
     */
    public DcloudGenericException(final Throwable t) {
        super(DcloudExceptionCode.GENERIC_EXCEPTION, t);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     * @param t another exception to be wrapped.
     */
    public DcloudGenericException(final String msg, final Throwable t) {
        super(DcloudExceptionCode.GENERIC_EXCEPTION, msg, t);
    }
}
