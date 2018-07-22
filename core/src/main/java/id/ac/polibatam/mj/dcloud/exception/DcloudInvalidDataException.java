package id.ac.polibatam.mj.dcloud.exception;

/**
 * CloudPacsInvalidDataException
 *
 * @author mia
 */
public class DcloudInvalidDataException extends BaseDcloudException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6844144892419559476L;

    /**
     * Constructor.
     */
    public DcloudInvalidDataException() {
        super(DcloudExceptionCode.INVALID_DATA_EXCEPTION);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     */
    public DcloudInvalidDataException(final String msg) {
        super(DcloudExceptionCode.INVALID_DATA_EXCEPTION, msg);
    }

    /**
     * Constructor.
     *
     * @param t another exception to be wrapped.
     */
    public DcloudInvalidDataException(final Throwable t) {
        super(DcloudExceptionCode.INVALID_DATA_EXCEPTION, t);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     * @param t   another exception to be wrapped.
     */
    public DcloudInvalidDataException(final String msg, final Throwable t) {
        super(DcloudExceptionCode.INVALID_DATA_EXCEPTION, msg, t);
    }
}
