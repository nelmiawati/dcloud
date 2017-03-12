/**
 * 
 */
package id.ac.polibatam.mj.dcloud.main;

import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * @author mia
 *
 */
public final class DcloudConsoleUI {

	private static final Logger LOG = Logger.getLogger(DcloudConsoleUI.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {



		if (LOG.isTraceEnabled()) {
			LOG.trace("args=[" + Arrays.toString(args) + "]");
		}

	}


}
