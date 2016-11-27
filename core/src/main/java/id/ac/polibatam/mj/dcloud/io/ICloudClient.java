package id.ac.polibatam.mj.dcloud.io;

import java.io.File;

import id.ac.polibatam.mj.dcloud.exception.BaseDcloudException;

public interface ICloudClient {

	void upload(final File file) throws BaseDcloudException;

	void download(final File file);

	void delete(final File file);

	void list(final File dir);

}
