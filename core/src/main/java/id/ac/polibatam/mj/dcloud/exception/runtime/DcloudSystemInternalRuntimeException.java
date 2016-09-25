package id.ac.polibatam.mj.dcloud.exception.runtime;

import id.ac.polibatam.mj.dcloud.exception.DcloudExceptionCode;

/**
 * IDAGenericRuntimeException
 *
 * @author mia
 */
public class DcloudSystemInternalRuntimeException extends BaseDcloudRuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5556340209454637142L;

    /**
     * Constructor.
     */
    public DcloudSystemInternalRuntimeException() {
        super(DcloudExceptionCode.SYSTEM_INTERNAL_EXCEPTION);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     */
    public DcloudSystemInternalRuntimeException(final String msg) {
        super(DcloudExceptionCode.SYSTEM_INTERNAL_EXCEPTION, msg);
    }

    /**
     * Constructor.
     *
     * @param t another exception to be wrapped.
     */
    public DcloudSystemInternalRuntimeException(final Throwable t) {
        super(DcloudExceptionCode.SYSTEM_INTERNAL_EXCEPTION, t);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     * @param t another exception to be wrapped.
     */
    public DcloudSystemInternalRuntimeException(final String msg, final Throwable t) {
        super(DcloudExceptionCode.SYSTEM_INTERNAL_EXCEPTION, msg, t);
    }
}
