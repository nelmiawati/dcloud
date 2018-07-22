package id.ac.polibatam.mj.dcloud.exception;

/**
 * CloudPacsSystemInternalException
 *
 * @author mia
 */
public class DcloudSystemInternalException extends BaseDcloudException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -224496693374177140L;

    /**
     * Constructor.
     */
    public DcloudSystemInternalException() {
        super(DcloudExceptionCode.SYSTEM_INTERNAL_EXCEPTION);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     */
    public DcloudSystemInternalException(final String msg) {
        super(DcloudExceptionCode.SYSTEM_INTERNAL_EXCEPTION, msg);
    }

    /**
     * Constructor.
     *
     * @param t another exception to be wrapped.
     */
    public DcloudSystemInternalException(final Throwable t) {
        super(DcloudExceptionCode.SYSTEM_INTERNAL_EXCEPTION, t);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     * @param t   another exception to be wrapped.
     */
    public DcloudSystemInternalException(final String msg, final Throwable t) {
        super(DcloudExceptionCode.SYSTEM_INTERNAL_EXCEPTION, msg, t);
    }
}
