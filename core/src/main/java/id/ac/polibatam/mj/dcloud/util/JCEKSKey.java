package id.ac.polibatam.mj.dcloud.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;

public class JCEKSKey {

	private static final Logger LOG = Logger.getLogger(JCEKSKey.class);

	private static final String KEYSTORE_TYPE = "JCEKS";

	private static final String SECRET_ALGO = "DES";

	private KeyStore ks = null;

	private File keystoreFile = null;

	private char[] keystorePwd = null;

	public JCEKSKey(final File keystoreFile, final String password)
			throws DcloudSystemInternalException, DcloudInvalidDataException {
		try {
			if (!keystoreFile.exists()) {
				throw new DcloudInvalidDataException("INVALID keystoreFile=[" + keystoreFile.getAbsolutePath() + "]");
			} else {
				this.keystoreFile = keystoreFile;
			}
			//this.keystorePwd = password.toCharArray();
			this.ks = KeyStore.getInstance(KEYSTORE_TYPE);

			if (StringUtils.isEmpty(password)) {
				throw new DcloudInvalidDataException("INVALID empty password");
			}

		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			throw new DcloudSystemInternalException(e.getMessage(), e);
		}
	}

	public List<String> getAliases() throws DcloudSystemInternalException {

		final List<String> aliases = new ArrayList<String>();
		InputStream is = null;
		try {
			is = new FileInputStream(this.keystoreFile);
			this.ks.load(is, this.keystorePwd);
			final Enumeration<String> enumAliases = this.ks.aliases();
			while (enumAliases.hasMoreElements()) {
				aliases.add(enumAliases.nextElement());
			}

		} catch (FileNotFoundException e) {
			throw new DcloudInvalidDataRuntimeException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (KeyStoreException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (IOException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					if (LOG.isEnabledFor(Level.WARN)) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}

		return aliases;
	}

	public byte[] getSecretKey(final String alias, final String pwd) throws DcloudSystemInternalException {

		byte[] secret = null;
		InputStream is = null;
		try {
			is = new FileInputStream(this.keystoreFile);
			this.ks.load(is, this.keystorePwd);
			final Key key = this.ks.getKey(alias, pwd.toCharArray());
			secret = key.getEncoded();

		} catch (FileNotFoundException e) {
			throw new DcloudInvalidDataRuntimeException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (KeyStoreException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (UnrecoverableKeyException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (IOException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					if (LOG.isEnabledFor(Level.WARN)) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}

		return secret;
	}

	public void setSecretKey(final String alias, final byte[] secret, final String pwd)
			throws DcloudSystemInternalException {

		InputStream is = null;
		try {
			is = new FileInputStream(this.keystoreFile);
			this.ks.load(is, this.keystorePwd);
			final SecretKey mySecretKey = new SecretKeySpec(secret, 0, secret.length, SECRET_ALGO);
			final KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(mySecretKey);
			this.ks.setEntry(alias, skEntry, new KeyStore.PasswordProtection(pwd.toCharArray()));

		} catch (FileNotFoundException e) {
			throw new DcloudInvalidDataRuntimeException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (KeyStoreException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (IOException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					if (LOG.isEnabledFor(Level.WARN)) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}

		this.saveKeystore();

	}

	public void deleteSecretKey(final String alias, final String pwd) throws DcloudSystemInternalException {

		InputStream is = null;
		try {
			is = new FileInputStream(this.keystoreFile);
			this.ks.deleteEntry(alias);

		} catch (FileNotFoundException e) {
			throw new DcloudInvalidDataRuntimeException(e.getMessage(), e);
		} catch (KeyStoreException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					if (LOG.isEnabledFor(Level.WARN)) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}

		this.saveKeystore();

	}

	private void saveKeystore() throws DcloudSystemInternalException {

		OutputStream os = null;
		try {
			os = new FileOutputStream(this.keystoreFile);
			this.ks.store(os, this.keystorePwd);

		} catch (FileNotFoundException e) {
			throw new DcloudInvalidDataRuntimeException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (KeyStoreException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (IOException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					if (LOG.isEnabledFor(Level.WARN)) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}
	}

}
