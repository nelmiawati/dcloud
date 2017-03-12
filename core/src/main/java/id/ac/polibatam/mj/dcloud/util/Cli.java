/**
 * 
 */
package id.ac.polibatam.mj.dcloud.util;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * @author mia
 *
 */
public final class Cli {

	private static final Logger LOG = Logger.getLogger(Cli.class);

	/**
	 * @param args
	 */
	public Cli(final String[] args) {

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		final Options options = this.buildOptions();

		try {
			final CommandLine cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("args=[" + Arrays.toString(args) + "]");
		}

	}

	private Options buildOptions() {
		final Options options = new Options();
		options.addOptionGroup(this.buildOptionGroupDownload());
		options.addOptionGroup(this.buildOptionGroupUpload());
		// options.addOptionGroup(this.buildOptionGroupList());
		// options.addOptionGroup(this.buildOptionGroupDelete());
		return options;
	}

	private OptionGroup buildOptionGroupDownload() {
		final OptionGroup optGroup = new OptionGroup();

		final Option optDownload = new Option("d", "download", false,
				"download file from remote cloud to local workspace");
		optDownload.setRequired(true);
		optGroup.addOption(optDownload);

		final Option optFromRemote = new Option("fr", "fromRemote", true,
				"full path of remote file at the cloud to be downloaded from");
		optFromRemote.setRequired(true);
		optGroup.addOption(optFromRemote);

		final Option optToLocal = new Option("tl", "toLocal", true,
				"full path of local file at the workspace to be downloaded to");
		optToLocal.setRequired(true);
		optGroup.addOption(optToLocal);

		return optGroup;
	}

	private OptionGroup buildOptionGroupUpload() {
		final OptionGroup optGroup = new OptionGroup();

		final Option optUpload = new Option("u", "upload", false, "upload file from local workspace to remote cloud");
		optUpload.setRequired(true);
		optGroup.addOption(optUpload);

		final Option optFromRemote = new Option("fl", "fromLocal", true,
				"full path of local file at the workspace to be uploaded from");
		optUpload.setRequired(true);
		optGroup.addOption(optFromRemote);

		final Option optToLocal = new Option("tl", "toLocal", true,
				"full path of remote file at the cloud to be uploaded to");
		optToLocal.setRequired(true);
		optGroup.addOption(optToLocal);

		return optGroup;
	}

	private OptionGroup buildOptionGroupList() {
		final OptionGroup optGroup = new OptionGroup();
		return optGroup;
	}

	private OptionGroup buildOptionGroupDelete() {
		final OptionGroup optGroup = new OptionGroup();
		return optGroup;
	}
	
	public static void main(String[] args) {
		Cli cli = new Cli(new String[]{});
	}

}
