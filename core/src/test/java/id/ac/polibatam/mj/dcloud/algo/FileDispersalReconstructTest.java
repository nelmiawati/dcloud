package id.ac.polibatam.mj.dcloud.algo;

import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import id.ac.polibatam.mj.dcloud.exception.BaseDcloudException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidConfigurationRuntimeException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudSystemInternalRuntimeException;
import id.ac.polibatam.mj.dcloud.util.Converter;

public class FileDispersalReconstructTest {

	private static final Logger LOG = Logger.getLogger(FileDispersalReconstructTest.class);

	private int nbTarget = 5;

	private int threshold = 3;

	private byte[] vSecretKey = null;

	private String origFileName1 = "lipsum.txt";

	private String origFileName2 = "github-mark.png";

	private File origFile1 = null;

	private File origFile2 = null;

	private File outputDir = new File("out");

	@Before
	public void before() {

		this.origFile1 = this.foundFile(origFileName1);
		this.origFile2 = this.foundFile(origFileName2);

		final Random random = new Random();
		final int[] vSecretKeyUnsigned = new int[this.nbTarget];
		for (int i = 0; i < vSecretKeyUnsigned.length; i++) {
			vSecretKeyUnsigned[i] = random.nextInt(256);
		}

		this.vSecretKey = Converter.convertUnsignedByteToSignedByte(vSecretKeyUnsigned);

	}

	private File foundFile(final String fileName) {

		File file = null;
		final URL url = ClassLoader.getSystemResource(fileName);
		if (null == url) {
			throw new DcloudInvalidConfigurationRuntimeException("file=[" + fileName + "] is NOT existing");
		} else {
			try {
				file = new File((new URI(url.toString())).getPath());
			} catch (URISyntaxException e) {
				throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
			}
			LOG.trace("file=[" + file.getAbsolutePath() + "]");
		}
		return file;
	}

	@After
	public void after() {

		for (File file : outputDir.listFiles()) {
			file.delete();
		}

	}

	private void disperseReconstruct1 (final File origFile, final boolean salt) {

		Throwable t1 = null;
		try {

			final FileDispersal fileD = new FileDispersal(vSecretKey, threshold);
			File[] dispersedFile = fileD.disperse(origFile, this.outputDir, salt);

			final File reconFile = new File(
					this.outputDir.getAbsolutePath().concat(File.separator).concat(origFile.getName()));

			final FileReconstruct fileR = new FileReconstruct();

			final File[] dispersedFileStar1 = new File[] { dispersedFile[3], dispersedFile[0], dispersedFile[4] };
			fileR.reconstruct(dispersedFileStar1, reconFile, salt);

			final File[] dispersedFileStar2 = new File[] { dispersedFile[0], dispersedFile[1], dispersedFile[2] };
			fileR.reconstruct(dispersedFileStar2, reconFile, salt);

			final File[] dispersedFileStar3 = new File[] { dispersedFile[4], dispersedFile[1], dispersedFile[2] };
			fileR.reconstruct(dispersedFileStar3, reconFile, salt);

		} catch (BaseDcloudException e) {
			LOG.error(e.getMessage(), e);
			t1 = e;
		}
		assertNull(t1);

	}

	private void disperseReconstruct2 (final File origFile, final boolean salt) {

		Throwable t1 = null;
		try {

			final FileDispersal fileD = new FileDispersal(nbTarget, threshold);
			File[] dispersedFile = fileD.disperse(origFile, this.outputDir, salt);

			final File reconFile = new File(
					this.outputDir.getAbsolutePath().concat(File.separator).concat(origFile.getName()));

			final FileReconstruct fileR = new FileReconstruct();

			final File[] dispersedFileStar1 = new File[] { dispersedFile[3], dispersedFile[0], dispersedFile[4] };
			fileR.reconstruct(dispersedFileStar1, reconFile, salt);

			final File[] dispersedFileStar2 = new File[] { dispersedFile[0], dispersedFile[1], dispersedFile[2] };
			fileR.reconstruct(dispersedFileStar2, reconFile, salt);

			final File[] dispersedFileStar3 = new File[] { dispersedFile[4], dispersedFile[1], dispersedFile[2] };
			fileR.reconstruct(dispersedFileStar3, reconFile, salt);

		} catch (BaseDcloudException e) {
			LOG.error(e.getMessage(), e);
			t1 = e;
		}
		assertNull(t1);

	}
	
	
	@Test
	public void testDisperseReconstructNoSalt() {
		this.disperseReconstruct1(origFile1, false);
		this.disperseReconstruct1(origFile1, true);
		this.disperseReconstruct2(origFile2, false);
		this.disperseReconstruct2(origFile2, true);
	}

}
