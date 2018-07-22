package id.ac.polibatam.mj.dcloud.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils {

    private static final Logger LOG = Logger.getLogger(FileUtils.class);

    public static byte[] getMd5(final File file) {

        byte[] md5 = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            md5 = DigestUtils.md5(fis);
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

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
        return md5;

    }

}
