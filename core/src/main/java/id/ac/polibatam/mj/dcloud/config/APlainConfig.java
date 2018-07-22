package id.ac.polibatam.mj.dcloud.config;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudSystemInternalRuntimeException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class APlainConfig extends AConfig {

    protected Configuration configProperties = null;
    private String configFileURL = null;

    @Override
    protected void loadConfigProperties() {
        final URL url = ClassLoader.getSystemResource(this.getConfigFileName());
        if (null == url) {
            throw new DcloudInvalidConfigurationRuntimeException(
                    "LOADING NON-EXIST config-file=[" + this.getConfigFileName() + "]");
        }
        try {
            this.configFileURL = (new URI(url.toString())).getPath();
            if (LOG.isInfoEnabled()) {
                LOG.info("LOADING config-file=[" + this.configFileURL + "]");
            }
            final PropertiesConfiguration propConfig = new PropertiesConfiguration(this.configFileURL);
            propConfig.setDelimiterParsingDisabled(true);
            propConfig.setAutoSave(true);
            propConfig.refresh();
            this.configProperties = propConfig;
        } catch (ConfigurationException e) {
            throw new DcloudInvalidConfigurationRuntimeException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
        }
    }

    // public String getConfigFileURL() {
    // return this.configFileURL;
    // }

    public String getString(final IParam param) {
        return this.getString(null, param);
    }

    public String getString(final String prefix, final IParam param) {

        String value = null;
        if (StringUtils.isEmpty(prefix)) {
            value = this.configProperties.getString(param.getName(), param.getDefaultValue());
        } else {
            value = this.configProperties.getString(prefix.concat("-").concat(param.getName()),
                    param.getDefaultValue());
        }
        final Pattern pattern = Pattern.compile(param.getPattern());
        final Matcher matcher = pattern.matcher(value.trim());
        boolean validPattern = matcher.find();
        if (!validPattern) {
            throw new DcloudInvalidConfigurationRuntimeException("INVALID configuration found at configFile=["
                    + this.configFileURL + "], prefix=[" + prefix + "], param=[" + param.getName() + "], actualValue=["
                    + value + "], expectedValuePattern=[" + param.getPattern() + "]");
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("RETRIEVED configuration prefix=[" + prefix + "], param=[" + param.getName()
                        + "], actualValue=[" + value + "]");
            }
            return value;
        }

    }

    public int getInt(final IParam param) {
        return this.getInt(null, param);
    }

    public int getInt(final String prefix, final IParam param) {
        return Integer.parseInt(this.getString(param));
    }

    public long getLong(final IParam param) {
        return this.getLong(null, param);
    }

    public long getLong(final String prefix, final IParam param) {
        return Long.parseLong(this.getString(param));
    }

    public boolean getBoolean(final IParam param) {
        return this.getBoolean(null, param);
    }

    public boolean getBoolean(final String prefix, final IParam param) {
        return Boolean.parseBoolean(this.getString(param));
    }

    public boolean setString(final IParam param, final String value) {
        return this.setString(null, param, value);
    }

    public boolean setString(final String prefix, final IParam param, final String value) {

        final Pattern pattern = Pattern.compile(param.getPattern());
        final Matcher matcher = pattern.matcher(value.trim());
        boolean validPattern = matcher.find();
        if (validPattern) {
            if (StringUtils.isEmpty(prefix)) {
                this.configProperties.setProperty(param.getName(), value.trim());
            } else {
                this.configProperties.setProperty(prefix.concat("-").concat(param.getName()), value.trim());
            }
        }
        return validPattern;
    }

    public boolean containsKey(final IParam param) {
        return this.containsKey(null, param);
    }

    public boolean containsKey(final String prefix, final IParam param) {

        boolean exist = false;
        if (StringUtils.isEmpty(prefix)) {
            exist = this.configProperties.containsKey(param.getName());
        } else {
            exist = this.configProperties.containsKey(prefix.concat("-").concat(param.getName()));
        }
        return exist;
    }

}
