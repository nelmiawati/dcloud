package id.ac.polibatam.mj.dcloud.config;

public class DcloudSecureConfig extends ASecureConfig {

    private static final String CONFIG_FILE_NAME = "dcloud-sconfig.jceks";
    private static final DcloudSecureConfig CONFIG = new DcloudSecureConfig();

    private DcloudSecureConfig() {
        super.loadConfigProperties();
    }

    public static DcloudSecureConfig getInstance() {
        return CONFIG;
    }

    @Override
    protected String getConfigFileName() {
        return CONFIG_FILE_NAME;
    }

    @Override
    protected String getConfigFilePassword() {
        return System.getProperty("sconfigPassword");
    }

    @Override
    protected void validateConfig() {

    }

    public enum Param implements IParam {

        DROPBOX_ACCESS_TOKEN("dropbox-access-token"),;

        private final String name;

        private Param(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getDefaultValue() {
            return null;
        }

        @Override
        public String getPattern() {
            return null;
        }

    }

}
