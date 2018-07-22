package id.ac.polibatam.mj.dcloud.exception.runtime;

import id.ac.polibatam.mj.dcloud.exception.DcloudExceptionCode;

/**
 * IDAGenericRuntimeException
 *
 * @author mia
 */
public class DcloudSystemExternalRuntimeException extends BaseDcloudRuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 743600081568050588L;

    /**
     * Constructor.
     */
    public DcloudSystemExternalRuntimeException() {
        super(DcloudExceptionCode.SYSTEM_EXTERNAL_EXCEPTION);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     */
    public DcloudSystemExternalRuntimeException(final String msg) {
        super(DcloudExceptionCode.SYSTEM_EXTERNAL_EXCEPTION, msg);
    }

    /**
     * Constructor.
     *
     * @param t another exception to be wrapped.
     */
    public DcloudSystemExternalRuntimeException(final Throwable t) {
        super(DcloudExceptionCode.SYSTEM_EXTERNAL_EXCEPTION, t);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     * @param t   another exception to be wrapped.
     */
    public DcloudSystemExternalRuntimeException(final String msg, final Throwable t) {
        super(DcloudExceptionCode.SYSTEM_EXTERNAL_EXCEPTION, msg, t);
    }
}
