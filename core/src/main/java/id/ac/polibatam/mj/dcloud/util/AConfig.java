/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.util;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;

/**
 *
 * @author mia
 */
public abstract class AConfig {

	private static final Logger LOG = Logger.getLogger(AConfig.class);

	protected interface IParam {

		public String getName();

		public String getDefaultValue();

		public String getPattern();
	}

	protected Configuration configProperties = null;
	private String configFileURL = null;

	protected abstract String getConfigFileName();

	protected void loadConfigProperties() {
		final URL url = ClassLoader.getSystemResource(this.getConfigFileName());
		if (null == url) {
			throw new DcloudInvalidConfigurationRuntimeException(
					"config-file=[" + this.getConfigFileName() + "] is NOT existing");
		}
		try {
			this.configFileURL = url.getFile();
			final PropertiesConfiguration propConfig = new PropertiesConfiguration(this.configFileURL);
			propConfig.setDelimiterParsingDisabled(true);
			propConfig.setAutoSave(true);
			propConfig.refresh();
			this.configProperties = propConfig;
		} catch (ConfigurationException e) {
			throw new DcloudInvalidConfigurationRuntimeException(e.getMessage(), e);
		}
	}

	public String getConfigFileURL() {
		return this.configFileURL;
	}

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
			if (LOG.isDebugEnabled()) {
				LOG.debug("RETRIEVED configuration at configFile=[" + this.configFileURL + "], param=["
						+ param.getName() + "], actualValue=[" + value + "]");
			}
			return value;
		}

	}

	public long getShort(final IParam param) {
		return Short.parseShort(this.getString(param));
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
