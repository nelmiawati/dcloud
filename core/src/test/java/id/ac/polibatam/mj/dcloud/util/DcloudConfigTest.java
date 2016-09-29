/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
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
		for (DcloudConfig.Param param : DcloudConfig.Param.values()) {
			Assert.assertFalse(param.getDefaultValue().equals(config.getString(param)));
		}

	}

}
