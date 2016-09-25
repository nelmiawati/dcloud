package id.ac.polibatam.mj.dcloud.exception;

/**
 * Base class for IDAException
 *
 * @author mia
 */
public class BaseDcloudException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4013376577804585142L;
    /**
     * Exception code.
     */
    private DcloudExceptionCode code = null;

    /**
     * Constructor.
     *
     * @param code exception code.
     */
    protected BaseDcloudException(final DcloudExceptionCode code) {
        super("[" + code.getCode() + "] " + code.toString());
        this.code = code;
    }

    /**
     * Constructor.
     *
     * @param code exception code.
     * @param msg exception message.
     */
    protected BaseDcloudException(final DcloudExceptionCode code,
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
    protected BaseDcloudException(final DcloudExceptionCode code,
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
    protected BaseDcloudException(final DcloudExceptionCode code,
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
