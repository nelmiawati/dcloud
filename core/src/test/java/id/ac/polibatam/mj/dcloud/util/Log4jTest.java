/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.util;


import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mia
 */
public class Log4jTest {

    private static final Logger LOG = Logger.getLogger(Log4jTest.class);

    @Before
    public void before() {

    }

    @Test
    public void test() {

        LOG.debug("start TEST");
        Assert.assertTrue(true);
        LOG.debug("finish TEST");

    }

}
