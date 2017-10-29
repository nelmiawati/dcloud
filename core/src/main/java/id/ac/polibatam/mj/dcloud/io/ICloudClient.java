package id.ac.polibatam.mj.dcloud.io;

import java.util.Map;

import id.ac.polibatam.mj.dcloud.exception.BaseDcloudException;

public interface ICloudClient {

	public enum FileType {
		FILE, DIRECTORY, UNKNOWN;
	}

	void upload(final String fromLocal, final String toRemote) throws BaseDcloudException;

	void download(final String fromRemote, final String toLocal) throws BaseDcloudException;

	void delete(final String fileRemote) throws BaseDcloudException;

	Map<String, FileType> list(final String dirRemote) throws BaseDcloudException;
	
	FileType getType(final String objRemote) throws BaseDcloudException;
	
	String getClientInfo();

}
