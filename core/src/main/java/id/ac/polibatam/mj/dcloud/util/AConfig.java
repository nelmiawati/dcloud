/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.util;

import org.apache.log4j.Logger;

/**
 *
 * @author mia
 */
public abstract class AConfig {

	protected static final Logger LOG = Logger.getLogger(AConfig.class);

	protected interface IParam {

		public String getName();

		public String getDefaultValue();

		public String getPattern();
	}

}
