package id.ac.polibatam.mj.dcloud.io;

import java.io.File;
import java.util.Map;

import id.ac.polibatam.mj.dcloud.exception.BaseDcloudException;

public interface ICloudClient {

	public enum FileType {
		FILE, DIRECTORY, UNKNOWN;
	}

	void upload(final File file, final String destination) throws BaseDcloudException;

	void download(final File file) throws BaseDcloudException;

	void delete(final String fileName) throws BaseDcloudException;

	Map<String, FileType> list(final String dir) throws BaseDcloudException;

}
