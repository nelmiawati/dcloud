/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.config;

import id.ac.polibatam.mj.dcloud.config.DcloudConfig.Param;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author mia
 */
public class DcloudConfigTest {

    @Before
    public void before() {

    }

    @Test
    public void testGetConfigFileName() {
        assertNotNull(DcloudConfig.getInstance().getConfigFileName());
    }

    @Test
    public void testConfig() {

        final DcloudConfig config = DcloudConfig.getInstance();
        assertFalse(Param.DCLOUD_COUNT.getDefaultValue().equals(config.getString(Param.DCLOUD_COUNT)));
        assertFalse(Param.DCLOUD_THRESHOLD.getDefaultValue().equals(config.getString(Param.DCLOUD_THRESHOLD)));
        assertFalse(Param.CLIENT.getDefaultValue().equals(config.getString("dcloud1", Param.CLIENT)));
        assertFalse(Param.CREDENTIAL.getDefaultValue().equals(config.getString("dcloud1", Param.CREDENTIAL)));
        assertTrue(Param.CLIENT.getDefaultValue().equals(config.getString("dcloud99", Param.CLIENT)));
        assertTrue(Param.CREDENTIAL.getDefaultValue().equals(config.getString("dcloud99", Param.CREDENTIAL)));

    }

}
