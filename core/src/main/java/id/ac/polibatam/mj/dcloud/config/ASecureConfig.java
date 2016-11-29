package id.ac.polibatam.mj.dcloud.config;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudSystemInternalRuntimeException;
import id.ac.polibatam.mj.dcloud.util.JCEKSKey;

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

	public byte[] getByte(final IParam param, final String pwd) {
		return this.getByte(null, param, pwd);
	}

	public byte[] getByte(final String prefix, final IParam param, final String pwd) {

		byte[] value = null;
		try {
			if (StringUtils.isEmpty(prefix)) {
				value = this.keyStore.getSecretKey(param.getName(), pwd);
			} else {
				value = this.keyStore.getSecretKey(prefix.concat("-").concat(param.getName()), pwd);
			}

		} catch (DcloudSystemInternalException e) {
			throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
		}

		return value;

	}

	public void setByte(final String prefix, final IParam param, final byte[] value, final String pwd) {

		try {
			if (StringUtils.isEmpty(prefix)) {
				this.keyStore.setSecretKey(param.getName(), value, pwd);
			} else {
				this.keyStore.setSecretKey(prefix.concat("-").concat(param.getName()), value, pwd);
			}
		} catch (DcloudSystemInternalException e) {
			throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
		}

	}

	public boolean containsKey(final IParam param) {
		return this.containsKey(null, param);
	}

	public boolean containsKey(final String prefix, final IParam param) {

		boolean exist = false;
		try {
			if (StringUtils.isEmpty(prefix)) {
				exist = this.keyStore.containsAlias(param.getName());
			} else {
				exist = this.keyStore.containsAlias(prefix.concat("-").concat(param.getName()));
			}
		} catch (DcloudSystemInternalException e) {
			throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
		}
		return exist;
	}
}
