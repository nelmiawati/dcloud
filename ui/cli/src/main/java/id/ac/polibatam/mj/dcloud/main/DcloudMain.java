/**
 * 
 */
package id.ac.polibatam.mj.dcloud.main;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import id.ac.polibatam.mj.dcloud.util.Cli;

/**
 * @author mia
 *
 */
public final class DcloudMain {

	private static final Logger LOG = Logger.getLogger(DcloudMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("args=[" + Arrays.toString(args) + "]");
		}

		try {
			final Cli cli = new Cli(args);
			System.out.println(cli.exec());
		} catch (Exception e) {
			if (LOG.isEnabledFor(Level.ERROR)) {
				LOG.error(e.getMessage(), e);
			}
		}
	

	}

}
