package id.ac.polibatam.mj.dcloud.exception.runtime;

import id.ac.polibatam.mj.dcloud.exception.DcloudExceptionCode;

/**
 * CloudPacsGenericRuntimeException
 *
 * @author mia
 */
public class DcloudGenericRuntimeException extends BaseDcloudRuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7541114135732876626L;

    /**
     * Constructor.
     */
    public DcloudGenericRuntimeException() {
        super(DcloudExceptionCode.GENERIC_EXCEPTION);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     */
    public DcloudGenericRuntimeException(final String msg) {
        super(DcloudExceptionCode.GENERIC_EXCEPTION, msg);
    }

    /**
     * Constructor.
     *
     * @param t another exception to be wrapped.
     */
    public DcloudGenericRuntimeException(final Throwable t) {
        super(DcloudExceptionCode.GENERIC_EXCEPTION, t);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     * @param t another exception to be wrapped.
     */
    public DcloudGenericRuntimeException(final String msg, final Throwable t) {
        super(DcloudExceptionCode.GENERIC_EXCEPTION, msg, t);
    }
}
