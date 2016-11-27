package id.ac.polibatam.mj.dcloud.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudSystemInternalRuntimeException;

public abstract class ASecureConfig extends AConfig {

	private String configFileURL = null;

	protected JCEKSKey keyStore = null;

	protected abstract String getConfigFileName();

	protected abstract String getConfigFilePassword();

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
			this.keyStore = new JCEKSKey(new File(url.getFile()), this.getConfigFilePassword());
		} catch (DcloudInvalidDataException e) {
			throw new DcloudInvalidDataRuntimeException(e.getMessage(), e);
		} catch (DcloudSystemInternalException e) {
			throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
		}
	}

	public String getString(final IParam param, final String pwd) {

		String value = null;
		try {
			value = new String(this.keyStore.getSecretKey(param.getName(), pwd), "UTF-8");
			if (StringUtils.isEmpty(value)) {
				value = param.getDefaultValue();
			}
			final Pattern pattern = Pattern.compile(param.getPattern());
			final Matcher matcher = pattern.matcher(value.trim());
			boolean validPattern = matcher.find();
			if (!validPattern) {
				throw new DcloudInvalidConfigurationRuntimeException("INVALID configuration found at configFile=["
						+ this.configFileURL + "], param=[" + param.getName() + "], actualValue=[" + value
						+ "], expectedValuePattern=[" + param.getPattern() + "]");
			} else {
				if (LOG.isTraceEnabled()) {
					LOG.trace("RETRIEVED configuration param=[" + param.getName() + "], actualValue=[" + value + "]");
				}
			}

		} catch (UnsupportedEncodingException e) {
			throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
		} catch (DcloudSystemInternalException e) {
			throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
		}

		return value;
	}

	public int getInt(final IParam param, final String pwd) {
		return Integer.parseInt(this.getString(param, pwd));
	}

	public long getLong(final IParam param, final String pwd) {
		return Long.parseLong(this.getString(param, pwd));
	}

	public boolean getBoolean(final IParam param, final String pwd) {
		return Boolean.parseBoolean(this.getString(param, pwd));
	}

	public boolean setString(final IParam param, final String value, final String pwd) {

		final Pattern pattern = Pattern.compile(param.getPattern());
		final Matcher matcher = pattern.matcher(value.trim());
		boolean validPattern = matcher.find();
		if (validPattern) {
			try {
				this.keyStore.setSecretKey(param.getName(), value.getBytes("UTF-8"), pwd);
			} catch (UnsupportedEncodingException e) {
				throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
			} catch (DcloudSystemInternalException e) {
				throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
			}
		}
		return validPattern;
	}

}
