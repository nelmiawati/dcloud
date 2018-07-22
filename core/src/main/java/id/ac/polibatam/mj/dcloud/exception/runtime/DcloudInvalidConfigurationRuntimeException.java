package id.ac.polibatam.mj.dcloud.exception.runtime;

import id.ac.polibatam.mj.dcloud.exception.DcloudExceptionCode;

/**
 * CloudPacsInvalidDataRuntimeException
 *
 * @author mia
 */
public class DcloudInvalidConfigurationRuntimeException extends BaseDcloudRuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -9212462898453813719L;

    /**
     * Constructor.
     */
    public DcloudInvalidConfigurationRuntimeException() {
        super(DcloudExceptionCode.INVALID_CONFIGURATION_EXCEPTION);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     */
    public DcloudInvalidConfigurationRuntimeException(final String msg) {
        super(DcloudExceptionCode.INVALID_CONFIGURATION_EXCEPTION, msg);
    }

    /**
     * Constructor.
     *
     * @param t another exception to be wrapped.
     */
    public DcloudInvalidConfigurationRuntimeException(final Throwable t) {
        super(DcloudExceptionCode.INVALID_CONFIGURATION_EXCEPTION, t);
    }

    /**
     * Constructor.
     *
     * @param msg exception message.
     * @param t   another exception to be wrapped.
     */
    public DcloudInvalidConfigurationRuntimeException(final String msg, final Throwable t) {
        super(DcloudExceptionCode.INVALID_CONFIGURATION_EXCEPTION, msg, t);
    }
}
