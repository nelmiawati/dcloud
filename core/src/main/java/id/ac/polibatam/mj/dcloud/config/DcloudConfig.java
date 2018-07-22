/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.ac.polibatam.mj.dcloud.config;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;

import java.io.File;

/**
 * @author mia
 */
public class DcloudConfig extends APlainConfig {

    private static final String CONFIG_FILE_NAME = "dcloud-config.properties";
    private static final DcloudConfig CONFIG = new DcloudConfig();

    private DcloudConfig() {
        super.loadConfigProperties();
        this.validateConfig();
    }

    public static DcloudConfig getInstance() {
        return CONFIG;
    }

    @Override
    protected String getConfigFileName() {
        return CONFIG_FILE_NAME;
    }

    @Override
    protected void validateConfig() {

        // Validate localWs
        String localWsName = this.getString(Param.DCLOUD_LOCAL_WORKSPACE);
        if (StringUtils.isEmpty(localWsName)) {
            localWsName = Param.DCLOUD_LOCAL_WORKSPACE.getDefaultValue();
            if (LOG.isEnabledFor(Level.WARN)) {
                LOG.warn("Empty " + Param.DCLOUD_LOCAL_WORKSPACE.getName() + " configuration. Use default=["
                        + Param.DCLOUD_LOCAL_WORKSPACE.getDefaultValue() + "]");
            }
        }
        final File localWs = new File(localWsName);
        if (!localWs.exists()) {
            localWs.mkdirs();
            if (LOG.isEnabledFor(Level.WARN)) {
                LOG.warn(Param.DCLOUD_LOCAL_WORKSPACE.getName() + "=[" + localWs.getAbsolutePath()
                        + "] is NOT EXISTING. Create new directory");
            }
        }

        // Validate dcloudThreshold
        final int threshold = this.getInt(Param.DCLOUD_THRESHOLD);
        if (threshold <= 1) {
            throw new DcloudInvalidConfigurationRuntimeException(
                    "INVALID " + Param.DCLOUD_THRESHOLD.getName() + "=[" + threshold + "] it has to be more than 1.");
        }

        final int nbClouds = this.getInt(Param.DCLOUD_COUNT);
        if (threshold >= nbClouds) {
            throw new DcloudInvalidConfigurationRuntimeException("INVALID " + Param.DCLOUD_THRESHOLD.getName() + "=["
                    + threshold + "] it has to be less than " + Param.DCLOUD_COUNT.getName() + "=[" + nbClouds + "]");
        }

        // Validate dcloud servers connection details
        for (int i = 0; i < nbClouds; i++) {
            final String client = this.getString("dcloud" + Integer.toString(i + 1), Param.CLIENT);
            if (StringUtils.isEmpty(client)) {
                throw new DcloudInvalidConfigurationRuntimeException("INVALID EMPTY dcloud" + Integer.toString(i + 1)
                        + "-" + Param.CLIENT.getName() + "=[" + client + "]");
            }

            final String credential = this.getString("dcloud" + Integer.toString(i + 1), Param.CREDENTIAL);
            if (StringUtils.isEmpty(client)) {
                throw new DcloudInvalidConfigurationRuntimeException("INVALID EMPTY dcloud" + Integer.toString(i + 1)
                        + "-" + Param.CREDENTIAL.getName() + "=[" + credential + "]");
            }

        }
    }

    public enum Param implements IParam {

        DCLOUD_LOCAL_WORKSPACE("dcloud-local-workspace", "workspace", ".+"), DCLOUD_COUNT("dcloud-count", "4",
                "[0-9]"), DCLOUD_THRESHOLD("dcloud-threshold", "2", "[0-9]"), CLIENT("client",
                "id.ac.polibatam.mj.dcloud.io.UtopiaClient", ".+"), CREDENTIAL("credential", "utopia", ".+"),;

        private final String name;
        private final String defaultValue;
        private final String pattern;

        private Param(final String name, final String defaultValue, final String pattern) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.pattern = pattern;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String getPattern() {
            return this.pattern;
        }

    }

}
