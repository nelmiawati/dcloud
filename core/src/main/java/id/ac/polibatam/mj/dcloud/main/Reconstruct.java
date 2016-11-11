/**
 * 
 */
package id.ac.polibatam.mj.dcloud.main;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author mia
 *
 */
public final class Reconstruct {

	private static final Logger LOG = Logger.getLogger(Reconstruct.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (LOG.isTraceEnabled()) {
			LOG.trace("args=[" + Arrays.toString(args) + "]");
		}
		if (LOG.isEnabledFor(Level.WARN)) {
			LOG.warn(DcloudDownload.class + ": NOT IMPLEMENTED");
		}
	}

}
