/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.util;

import static org.junit.Assert.assertTrue;

import java.util.Random;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 * @author mia
 */
public class RandomTest {

	private final static Logger LOG = Logger.getLogger(RandomTest.class);

	@Test
	public void test() {
		Random random1 = new Random();
		Random random2 = new Random();

		Random random3 = new Random(5);
		Random random4 = new Random(5);

		LOG.debug(random1.nextInt(256));
		LOG.debug(random2.nextInt(256));
		LOG.debug(random1.nextInt(256));
		LOG.debug(random2.nextInt(256));
		LOG.debug(random1.nextInt(256));
		LOG.debug(random2.nextInt(256));

		LOG.debug("++++++++");
		LOG.debug(random3.nextInt(256));
		LOG.debug(random4.nextInt(256));
		LOG.debug(random3.nextInt(256));
		LOG.debug(random4.nextInt(256));
		LOG.debug(random3.nextInt(256));
		LOG.debug(random4.nextInt(256));

		assertTrue(true);

	}

}
