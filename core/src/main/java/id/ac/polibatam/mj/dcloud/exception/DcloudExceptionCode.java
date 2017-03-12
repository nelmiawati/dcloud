package id.ac.polibatam.mj.dcloud.exception;

/**
 * List of exception type
 *
 * @author mia
 */
public enum DcloudExceptionCode {

    GENERIC_EXCEPTION("DCERR-1000"),
    INVALID_DATA_EXCEPTION("DCERR-1001"),
    INVALID_CONFIGURATION_EXCEPTION("DCERR-1002"),    
    SYSTEM_INTERNAL_EXCEPTION("DCERR-1003"),
    SYSTEM_EXTERNAL_EXCEPTION("DCERR-1004"),
    ;
    
    private String code = null;

    /**
     * Constructor.
     *
     * @param code code of the exception.
     */
    private DcloudExceptionCode(final String code) {
        this.code = code;
    }

    /**
     * Get exception code.
     *
     * @return exception code.
     */
    public String getCode() {
        return this.code;
    }
}
