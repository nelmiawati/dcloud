package id.ac.polibatam.mj.dcloud.util;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;

public abstract class APlainConfig extends AConfig {

	private String configFileURL = null;

	protected Configuration configProperties = null;

	protected abstract String getConfigFileName();

	protected void loadConfigProperties() {
		final URL url = ClassLoader.getSystemResource(this.getConfigFileName());
		if (null == url) {
			throw new DcloudInvalidConfigurationRuntimeException(
					"LOADING NON-EXIST config-file=[" + this.getConfigFileName() + "]");
		}
		try {
			this.configFileURL = url.getFile();
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
		}
	}

	// public String getConfigFileURL() {
	// return this.configFileURL;
	// }

	public String getString(final IParam param) {

		final String value = this.configProperties.getString(param.getName(), param.getDefaultValue());
		final Pattern pattern = Pattern.compile(param.getPattern());
		final Matcher matcher = pattern.matcher(value.trim());
		boolean validPattern = matcher.find();
		if (!validPattern) {
			throw new DcloudInvalidConfigurationRuntimeException(
					"INVALID configuration found at configFile=[" + this.configFileURL + "], param=[" + param.getName()
							+ "], actualValue=[" + value + "], expectedValuePattern=[" + param.getPattern() + "]");
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("RETRIEVED configuration param=[" + param.getName() + "], actualValue=[" + value + "]");
			}
			return value;
		}

	}

	public int getInt(final IParam param) {
		return Integer.parseInt(this.getString(param));
	}

	public long getLong(final IParam param) {
		return Long.parseLong(this.getString(param));
	}

	public boolean getBoolean(final IParam param) {
		return Boolean.parseBoolean(this.getString(param));
	}

	public boolean setString(final IParam param, final String value) {

		final Pattern pattern = Pattern.compile(param.getPattern());
		final Matcher matcher = pattern.matcher(value.trim());
		boolean validPattern = matcher.find();
		if (validPattern) {
			this.configProperties.setProperty(param.getName(), value.trim());
		}
		return validPattern;
	}

}
