package id.ac.polibatam.mj.dcloud.config;

import id.ac.polibatam.mj.dcloud.util.Converter;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DcloudSecureConfigTest {

    protected static final Logger LOG = Logger.getLogger(DcloudSecureConfigTest.class);

    @Before
    public void before() {

    }

    @Test
    public void test() throws Exception {

        System.setProperty("sconfigPassword", "password");
        final DcloudSecureConfig sConfig = DcloudSecureConfig.getInstance();

//        sConfig.setByte("dcloud1", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, Converter.convertHexStringToSignedByte(
//                "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"), "password");
//        sConfig.setByte("dcloud2", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, Converter.convertHexStringToSignedByte(
//                "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"), "password");
//        sConfig.setByte("dcloud3", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, Converter.convertHexStringToSignedByte(
//                "33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333"), "password");
//        sConfig.setByte("dcloud4", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, Converter.convertHexStringToSignedByte(
//                "44444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444"), "password");
//        sConfig.setByte("dcloud5", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, Converter.convertHexStringToSignedByte(
//                "55555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555"), "password");

        final byte[] btoken1 = sConfig.getByte("dcloud1", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final String htoken1 = Converter.convertSignedByteToHexString(btoken1);
        LOG.trace(htoken1);
        assertNotNull(htoken1);

        final byte[] btoken2 = sConfig.getByte("dcloud2", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final String htoken2 = Converter.convertSignedByteToHexString(btoken2);
        LOG.trace(htoken2);
        assertNotNull(htoken2);

        final byte[] btoken3 = sConfig.getByte("dcloud3", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final String htoken3 = Converter.convertSignedByteToHexString(btoken3);
        LOG.trace(htoken3);
        assertNotNull(htoken3);

        final byte[] btoken4 = sConfig.getByte("dcloud4", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final String htoken4 = Converter.convertSignedByteToHexString(btoken4);
        LOG.trace(htoken4);
        assertNotNull(htoken4);

        final byte[] btoken5 = sConfig.getByte("dcloud5", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final String htoken5 = Converter.convertSignedByteToHexString(btoken5);
        LOG.trace(htoken5);
        assertNotNull(htoken5);

        final byte[] btoken99 = sConfig.getByte("dcloud99", DcloudSecureConfig.Param.DROPBOX_ACCESS_TOKEN, System.getProperty("sconfigPassword"));
        final String htoken99 = Converter.convertSignedByteToHexString(btoken99);
        LOG.trace(htoken99);
        assertNull(htoken99);
    }

}
