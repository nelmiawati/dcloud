package id.ac.polibatam.mj.dcloud.io;


import id.ac.polibatam.mj.dcloud.config.DcloudSecureConfig;
import id.ac.polibatam.mj.dcloud.util.Converter;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * @author mia
 */
public class DropBoxClientTest {

    private static final Logger LOG = Logger.getLogger(DropBoxClient.class);

    @Before
    public void before() {

    }

    @After
    public void after() {

    }

    @Test
    public void
    test1() throws Exception {
        assertEquals(true, true);

        System.setProperty("sconfigPassword", "???");
        final DcloudSecureConfig sConfig = DcloudSecureConfig.getInstance();

        final byte[] btoken1 = sConfig.getByte("dcloud1", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final DropBoxClient client1 = new DropBoxClient(new String(btoken1, "UTF-8"));
        LOG.trace(Converter.convertSignedByteToHexString(btoken1));
        LOG.trace(client1.getClientInfo());

        LOG.trace(client1.list("/"));
        final URL url1 = ClassLoader.getSystemResource("lipsum.txt");
        final File file1 = new File(new URI(url1.toString()).getPath());
        client1.upload(file1.getAbsolutePath(), "/lipsum.txt");
        LOG.trace(client1.list("/"));
        final URL url2 = ClassLoader.getSystemResource("github-mark.png");
        final File file2 = new File(new URI(url2.toString()).getPath());
        client1.upload(file2.getAbsolutePath(), "/github-mark.png");
        LOG.trace(client1.list("/"));

        client1.download("/lipsum.txt", "lipsum.txt");


        client1.delete("/lipsum.txt");
        client1.delete("/github-mark.png");
        LOG.trace(client1.list("/"));

        final byte[] btoken2 = sConfig.getByte("dcloud2", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final DropBoxClient client2 = new DropBoxClient(new String(btoken2, "UTF-8"));
        LOG.trace(Converter.convertSignedByteToHexString(btoken2));
        LOG.trace(client2.getClientInfo());

        final byte[] btoken3 = sConfig.getByte("dcloud3", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final DropBoxClient client3 = new DropBoxClient(new String(btoken3, "UTF-8"));
        LOG.trace(Converter.convertSignedByteToHexString(btoken3));
        LOG.trace(client3.getClientInfo());

        final byte[] btoken4 = sConfig.getByte("dcloud4", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final DropBoxClient client4 = new DropBoxClient(new String(btoken4, "UTF-8"));
        LOG.trace(Converter.convertSignedByteToHexString(btoken4));
        LOG.trace(client4.getClientInfo());

        final byte[] btoken5 = sConfig.getByte("dcloud5", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final DropBoxClient client5 = new DropBoxClient(new String(btoken5, "UTF-8"));
        LOG.trace(Converter.convertSignedByteToHexString(btoken5));
        LOG.trace(client5.getClientInfo());


    }

}
