package id.ac.polibatam.mj.dcloud.algo;

import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URL;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import id.ac.polibatam.mj.dcloud.exception.BaseDcloudException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;
import id.ac.polibatam.mj.dcloud.util.Converter;

public class FileDispersalReconstructTest {

	private static final Logger LOG = Logger.getLogger(FileDispersalReconstructTest.class);

	private int nbTarget = 5;

	private int threshold = 3;

	private byte[] vSecretKey = null;

	private String origFileName1 = "lipsum.txt";

	private File origFile1 = null;

	private File outputDir = new File("out");

	@Before
	public void before() {

		this.origFile1 = this.foundFile(origFileName1);

		final Random random = new Random();
		final int[] vSecretKeyUnsigned = new int[this.nbTarget];
		for (int i = 0; i < vSecretKeyUnsigned.length; i++) {
			vSecretKeyUnsigned[i] = random.nextInt(256);
		}

		this.vSecretKey = Converter.convertUnsignedByteToSignedByte(vSecretKeyUnsigned);

	}

	private File foundFile(final String fileName) {

		File file = null;
		final URL url = ClassLoader.getSystemResource(origFileName1);
		if (null == url) {
			throw new DcloudInvalidConfigurationRuntimeException("file=[" + fileName + "] is NOT existing");
		} else {
			file = new File(url.getFile());
			LOG.trace("file=[" + file.getAbsolutePath() + "]");
		}
		return file;
	}

	@After
	public void after() {

	}

	@Test
	public void testDisperseReconstructNoSalt() {

		Throwable t1 = null;
		try {
			final FileDispersal fileD = new FileDispersal(vSecretKey, threshold);
			File[] dispersedFile = fileD.disperse(this.origFile1, this.outputDir, false);

			final File reconFile = new File(this.outputDir.getAbsolutePath().concat(File.separator).concat(origFileName1));
			final File[] dispersedFileStar = new File[] { dispersedFile[3], dispersedFile[0], dispersedFile[4] };
			final FileReconstruct fileR = new FileReconstruct();
			fileR.reconstruct(dispersedFileStar, reconFile, false);

		} catch (BaseDcloudException e) {
			LOG.error(e.getMessage(), e);
			t1 = e;
		}
		assertNull(t1);

	}

	@Test
	public void testDisperseReconstructSalt() {

		Throwable t1 = null;
		try {
			final FileDispersal fileD = new FileDispersal(vSecretKey, threshold);
			@SuppressWarnings("unused")
			File[] dispersedFile = fileD.disperse(this.origFile1, this.outputDir, true);
		} catch (BaseDcloudException e) {
			LOG.error(e.getMessage(), e);
			t1 = e;
		}
		assertNull(t1);

	}

}
