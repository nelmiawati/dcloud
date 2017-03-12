package id.ac.polibatam.mj.dcloud.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemExternalException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;

public class DropBoxClient implements ICloudClient {

	private static final Logger LOG = Logger.getLogger(DropBoxClient.class);

	private String authAccessToken = null;

	private DbxClient dbxClient = null;

	public DropBoxClient(final String authAccessToken) {
		this.authAccessToken = authAccessToken;
		this.dbxClient = this.auth(this.authAccessToken);

		if (LOG.isTraceEnabled()) {
			this.printInfo();
		}

	}

	@Override
	public void upload(final File file, final String destination)
			throws DcloudInvalidDataException, DcloudSystemInternalException, DcloudSystemExternalException {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			final DbxEntry.File uploadedFile = this.dbxClient.uploadFile("/" + destination, DbxWriteMode.force(),
					file.length(), fis);
			if (LOG.isDebugEnabled()) {
				LOG.debug("UploadedFIle=[" + uploadedFile.toString() + "]");
			}
		} catch (FileNotFoundException e) {
			throw new DcloudInvalidDataException(e.getMessage(), e);
		} catch (IOException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (DbxException e) {
			throw new DcloudSystemExternalException(e.getMessage(), e);
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					if (LOG.isEnabledFor(Level.WARN)) {
						LOG.warn(e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void download(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(final String fileName) throws DcloudInvalidDataException, DcloudSystemExternalException {
		try {
			// final DbxEntry entry = this.dbxClient.getMetadata(fileName);
			// if (entry.isFolder()) {
			// throw new DcloudInvalidDataException("UNABLE to delete a
			// DIRECTORY=[" + fileName + "]");
			// }
			this.dbxClient.delete(fileName);
		} catch (DbxException e) {
			throw new DcloudSystemExternalException(e.getMessage(), e);
		}
	}

	@Override
	public Map<String, FileType> list(final String dir) throws DcloudSystemExternalException {
		final Map<String, FileType> map = new HashMap<String, FileType>();
		try {
			final DbxEntry.WithChildren listing = this.dbxClient.getMetadataWithChildren(dir);
			for (DbxEntry child : listing.children) {
				if (child.isFile()) {
					map.put(child.name, FileType.FILE);
				} else if (child.isFolder()) {
					map.put(child.name, FileType.DIRECTORY);
				} else {
					map.put(child.name, FileType.UNKNOWN);
				}
			}
		} catch (DbxException e) {
			throw new DcloudSystemExternalException(e.getMessage(), e);
		}
		return map;
	}

	private DbxClient auth(final String authAccessToken) {
		final DbxRequestConfig dbxRequestConfig = new DbxRequestConfig("dcloud/1.0", Locale.getDefault().toString());
		return new DbxClient(dbxRequestConfig, authAccessToken);
	}

	private void printInfo() {
		try {
			final DbxAccountInfo dbxAccountInfo = dbxClient.getAccountInfo();
			// in GB :)
			LOG.trace(dbxAccountInfo.toString());
			LOG.trace("Dropbox accountName=[" + dbxAccountInfo.displayName + "]");
			LOG.trace("dropboxSize=[" + (dbxAccountInfo.quota.total / 1024 / 1024 / 1024 + "]"));
		} catch (DbxException e) {
			if (LOG.isEnabledFor(Level.WARN)) {
				LOG.warn(e.getMessage());
			}
		}

	}

}
