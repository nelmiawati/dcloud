package id.ac.polibatam.mj.dcloud.io;

import java.io.File;

public interface ICloudClient {

	void upload(final File file);

	void download(final File file);

	void delete(final File file);

	void list(final File dir);

}
