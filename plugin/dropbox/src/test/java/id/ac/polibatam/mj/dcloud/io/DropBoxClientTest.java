package id.ac.polibatam.mj.dcloud.io;

import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Test;

public class DropBoxClientTest {

	private static final Logger LOG = Logger.getLogger(DropBoxClientTest.class);

	@Test
	public void test() throws Exception {

		assertNull(null);
	}

	@Test
	public void test1() throws Exception {
		final URL url = ClassLoader.getSystemResource("lipsum.txt");
		File file = new File(url.getFile());
		LOG.debug(file.exists());

		if (file.exists()) {
			DropBoxClient client = new DropBoxClient(
					"OY04Asw8haAAAAAAAAAAHz3IJT3_VhpVOqrUCig-4CIs3r27YU9hoVU_yGRcVRUC");
			client.upload(file.getAbsolutePath(), "/lipsum-upload.txt");
			LOG.debug(client.list("/"));
			client.download("/lipsum-upload.txt", "C:/@arif/lipsum-download.txt");
//			client.delete("/lipsum-upload.txt");
			LOG.debug(client.list("/"));
		}
	}

}
