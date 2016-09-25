package id.ac.polibatam.mj.dcloud.exception.runtime;

import id.ac.polibatam.mj.dcloud.exception.DcloudExceptionCode;

/**
 * Base class for IDARuntimeException.
 *
 * @author mia
 */
public class BaseDcloudRuntimeException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4922599419440385254L;
    /**
     * Exception code.
     */
    private DcloudExceptionCode code = null;

    /**
     * Constructor.
     *
     * @param code exception code.
     */
    protected BaseDcloudRuntimeException(final DcloudExceptionCode code) {
        super("[" + code.getCode() + "]");
        this.code = code;
    }

    /**
     * Constructor.
     *
     * @param code exception code.
     * @param msg exception message.
     */
    protected BaseDcloudRuntimeException(final DcloudExceptionCode code,
            final String msg) {
        super("[" + code.getCode() + "] " + msg);
        this.code = code;
    }

    /**
     * Constructor.
     *
     * @param code exception code.
     * @param t another exception to be wrapped.
     */
    protected BaseDcloudRuntimeException(final DcloudExceptionCode code,
            final Throwable t) {
        super("[" + code.getCode() + "]", t);
        this.code = code;
    }

    /**
     * Constructor
     *
     * @param code exception code.
     * @param msg exception message.
     * @param t another exception to be wrapped.
     */
    protected BaseDcloudRuntimeException(final DcloudExceptionCode code,
            final String msg, final Throwable t) {
        super("[" + code.getCode() + "] " + msg, t);
        this.code = code;
    }

    /**
     * Get exception code.
     *
     * @return exception code.
     */
    public DcloudExceptionCode getCode() {
        return this.code;
    }
}
