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

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;

/**
 *
 * @author mia
 */
public abstract class AConfig {

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
		return this.configProperties.getString(param.getName(), param.getDefaultValue());
	}

	public long getShort(final IParam param) {
		return this.configProperties.getLong(param.getName(), Short.parseShort(param.getDefaultValue()));
	}

	public int getInt(final IParam param) {
		return this.configProperties.getInt(param.getName(), Integer.parseInt(param.getDefaultValue()));
	}

	public long getLong(final IParam param) {
		return this.configProperties.getLong(param.getName(), Long.parseLong(param.getDefaultValue()));
	}

	public boolean getBoolean(final IParam param) {
		return this.configProperties.getBoolean(param.getName(), Boolean.parseBoolean(param.getDefaultValue()));
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
