package id.ac.polibatam.mj.dcloud.exception.runtime;

import id.ac.polibatam.mj.dcloud.exception.DcloudExceptionCode;

/**
 * CloudPacsInvalidDataRuntimeException
 *
 * @author mia
 */
public class DcloudInvalidDataRuntimeException extends BaseDcloudRuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -9212462898453813719L;

    /**
     * Constructor.
     */
    public DcloudInvalidDataRuntimeException() {
        super(DcloudExceptionCode.INVALID_DATA_EXCEPTION);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     */
    public DcloudInvalidDataRuntimeException(final String msg) {
        super(DcloudExceptionCode.INVALID_DATA_EXCEPTION, msg);
    }

    /**
     * Constructor.
     *
     * @param t another exception to be wrapped.
     */
    public DcloudInvalidDataRuntimeException(final Throwable t) {
        super(DcloudExceptionCode.INVALID_DATA_EXCEPTION, t);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     * @param t   another exception to be wrapped.
     */
    public DcloudInvalidDataRuntimeException(final String msg, final Throwable t) {
        super(DcloudExceptionCode.INVALID_DATA_EXCEPTION, msg, t);
    }
}
