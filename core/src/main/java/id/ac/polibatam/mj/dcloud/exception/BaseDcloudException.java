package id.ac.polibatam.mj.dcloud.exception;

import org.apache.commons.lang.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
     * Exception id
     */
    private String id = null;

    /**
     * Exception details
     */
    private String details = null;

    /**
     * Constructor.
     *
     * @param code exception code.
     */
    protected BaseDcloudException(final DcloudExceptionCode code) {
        this(code, null, null);
    }

    /**
     * Constructor.
     *
     * @param code    exception code.
     * @param details exception details.
     */
    protected BaseDcloudException(final DcloudExceptionCode code, final String details) {
        this(code, details, null);
    }

    /**
     * Constructor.
     *
     * @param code exception code.
     * @param t    another exception to be wrapped.
     */
    protected BaseDcloudException(final DcloudExceptionCode code, final Throwable t) {
        this(code, null, t);
    }

    /**
     * Constructor
     *
     * @param code    exception code.
     * @param details exception details.
     * @param t       another exception to be wrapped.
     */
    protected BaseDcloudException(final DcloudExceptionCode code, final String details, final Throwable t) {
        super(t);
        this.code = code;
        this.details = details;

        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        this.id = format.format(new Date()).concat("_")
                .concat(RandomStringUtils.randomAlphanumeric(4).toUpperCase(Locale.US));
    }

    /**
     * Get exception code.
     *
     * @return exception code.
     */
    public DcloudExceptionCode getCode() {
        return this.code;
    }

    /**
     * Get exception id.
     *
     * @return exception id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get exception details.
     *
     * @return exception details.
     */
    public String getDetails() {
        return this.details;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return code + "; id=[" + id + "], details=[" + this.details + "]";

    }

    /**
     * @see Exception#getMessage()
     */
    @Override
    public String getMessage() {
        return this.toString();

    }
}
