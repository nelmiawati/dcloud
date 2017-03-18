/**
 * 
 */
package id.ac.polibatam.mj.dcloud.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author mia
 *
 */
public final class Cli {

	private static final Logger LOG = Logger.getLogger(Cli.class);
	private static final String DCLOUD_VERSION = ":: dCLOUD v1.0 :: miarifSOFT :: copyright (r) 2017";

	private static enum Command {

		UPLOAD("ul", "upload"), DOWNLOAD("dl", "download"), LIST("ls", "list"), REMOVE("rm", "remove"), FROM_LOCAL("fl",
				"fromLocal"), TO_REMOTE("tr", "toRemote"), FROM_REMOTE("fr", "fromRemote"), TO_LOCAL("tl",
						"toLocal"), STORE_PASS("sp",
								"storePass"), KEY_PASS("kp", "keyPass"), HELP("h", "help"), VERSION("v", "version"),;

		private static Map<String, Command> MAP_OPT_ENUM = new HashMap<String, Command>();

		static {
			for (Command cmd : Command.values()) {
				MAP_OPT_ENUM.put(cmd.shortCmd, cmd);
				MAP_OPT_ENUM.put(cmd.longCmd, cmd);
			}
		}

		private String shortCmd;
		private String longCmd;

		private Command(final String shortCmd, final String longCmd) {
			this.shortCmd = shortCmd;
			this.longCmd = longCmd;
		}

		public String getShortCmd() {
			return this.shortCmd;
		}

		public String getLongCmd() {
			return this.longCmd;
		}

		public static Command getCommand(final String opt) {
			return MAP_OPT_ENUM.get(opt);
		}
	}

	private CommandLineParser clParser = null;
	private HelpFormatter helpFormatter = null;
	private String[] args;

	/**
	 * @param args
	 */
	public Cli(final String[] args) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("args=[" + Arrays.toString(args) + "]");
		}

		this.clParser = new DefaultParser();
		this.helpFormatter = new HelpFormatter();
		this.args = args;

	}

	public void exec() {
		final Options optsMain = this.buildOptsMain();

		try {
			final CommandLine cl = clParser.parse(optsMain, this.args);
			Option[] opts = cl.getOptions();
			if (opts.length != 1) {
				this.helpFormatter.printHelp("java", optsMain);
			} else {
				final Command cmd = Command.getCommand(opts[0].getOpt());
				final List<String> args = opts[0].getValuesList();
				if (LOG.isTraceEnabled()) {
					LOG.trace(cmd);
					LOG.trace(";" + opts[0].getValueSeparator() + ";");
					LOG.trace(opts[0].getArgs());
					LOG.trace(args);
				}

				switch (cmd) {
				case UPLOAD: {
					break;

				}
				case DOWNLOAD: {
					break;

				}
				case LIST: {
					break;

				}
				case REMOVE: {
					break;

				}
				case VERSION: {
					this.execVersion();
					break;

				}
				default: {
					this.execHelp(args.size() == 0 ? null : args.get(0));
				}
				}
			}

		} catch (ParseException e) {
			LOG.warn(e.getMessage());
			helpFormatter.printHelp("java", optsMain);
		}
	}

	public static void main(String[] args) {
		Cli cli = new Cli(new String[] { "--help", "dl" });
		cli.exec();
	}

	private void execHelp(final String arg) {
		if (StringUtils.isEmpty(arg)) {
			helpFormatter.printHelp("java", this.buildOptsMain());
		} else {
			final Command cmd = Command.getCommand(arg);
			if (LOG.isTraceEnabled()) {
				LOG.trace(cmd);
			}
			if (null == cmd) {
				helpFormatter.printHelp("java", this.buildOptsMain());
			} else {

				switch (cmd) {
				case UPLOAD: {
					helpFormatter.printHelp("java", this.buildOptsUpload());
					break;

				}
				case DOWNLOAD: {
					helpFormatter.printHelp("java", this.buildOptsDownload());
					break;

				}
				case LIST: {
					helpFormatter.printHelp("java", this.buildOptsList());
					break;

				}
				case REMOVE: {
					helpFormatter.printHelp("java", this.buildOpsRemove());
					break;

				}
				default: {
					helpFormatter.printHelp("java", this.buildOptsMain());
				}
				}
			}
		}
	}

	private void execVersion() {
		System.out.println(DCLOUD_VERSION);
	}

	private Options buildOptsMain() {

		final Options opts = new Options();
		final OptionGroup optsG = new OptionGroup();

		final Option optUpload = new Option(Command.UPLOAD.getShortCmd(), Command.UPLOAD.getLongCmd(), false,
				"Upload a file from local workspace to remote cloud.");
		optsG.addOption(optUpload);

		final Option optDownload = new Option(Command.DOWNLOAD.getShortCmd(), Command.DOWNLOAD.getLongCmd(), false,
				"Download a file from remote cloud to local workspace.");
		optsG.addOption(optDownload);

		final Option optList = new Option(Command.LIST.getShortCmd(), Command.LIST.getLongCmd(), true,
				"List of files/directories inside a directory of the cloud. Argument is a directory at the cloud to be listed its contents.");
		optsG.addOption(optList);

		final Option optRemove = new Option(Command.REMOVE.getShortCmd(), Command.REMOVE.getLongCmd(), true,
				"Remove a file at the cloud. Argument is full path of the file to be removed from the cloud.");
		optsG.addOption(optRemove);

		final Option optHelp = new Option(Command.HELP.getShortCmd(), Command.HELP.getLongCmd(), true,
				"Help with commands used at dCLOUD.");
		optHelp.setOptionalArg(true);
		optsG.addOption(optHelp);

		final Option optVersion = new Option(Command.VERSION.getShortCmd(), Command.VERSION.getLongCmd(), false,
				"Print dCLOUD version");
		optsG.addOption(optVersion);

		optsG.setRequired(true);
		opts.addOptionGroup(optsG);

		return opts;
	}

	private Options buildOptsUpload() {

		final Options opts = new Options();

		final Option optUpload = new Option(Command.UPLOAD.getShortCmd(), Command.UPLOAD.getLongCmd(), false,
				"Upload a file from local workspace to remote cloud.");
		optUpload.setRequired(true);
		opts.addOption(optUpload);

		final Option optFromLocal = new Option(Command.FROM_LOCAL.getShortCmd(), Command.FROM_LOCAL.getLongCmd(), true,
				"The full path local file at the workspace to be uploaded from.");
		optUpload.setRequired(true);
		opts.addOption(optFromLocal);

		final Option optToRemote = new Option(Command.TO_REMOTE.getShortCmd(), Command.TO_REMOTE.getLongCmd(), true,
				"The full path remote file at the cloud of the uploaded file to be saved into.");
		optToRemote.setRequired(true);
		opts.addOption(optToRemote);

		final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
				"Passphrase to access the keystore file.");
		optStorePass.setRequired(false);
		opts.addOption(optStorePass);

		final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
				"Passphrase to access key inside the keystore file.");
		optKeyPass.setRequired(false);
		opts.addOption(optKeyPass);

		return opts;
	}

	private Options buildOptsDownload() {

		final Options opts = new Options();

		final Option optDownload = new Option(Command.DOWNLOAD.getShortCmd(), Command.DOWNLOAD.getLongCmd(), false,
				"Download a file from remote cloud to local workspace.");
		optDownload.setRequired(true);
		opts.addOption(optDownload);

		final Option optFromRemote = new Option(Command.FROM_REMOTE.getShortCmd(), Command.FROM_REMOTE.getLongCmd(),
				true, "The full path remote file at the cloud to be downloaded from.");
		optFromRemote.setRequired(true);
		opts.addOption(optFromRemote);

		final Option optToLocal = new Option(Command.TO_LOCAL.getShortCmd(), Command.TO_LOCAL.getLongCmd(), true,
				"The full path local file at the workspace of the downloaded file to be saved into.");
		optToLocal.setRequired(true);
		opts.addOption(optToLocal);

		final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
				"Passphrase to access the keystore file.");
		optStorePass.setRequired(false);
		opts.addOption(optStorePass);

		final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
				"Passphrase to access key inside the keystore file.");
		optKeyPass.setRequired(false);
		opts.addOption(optKeyPass);

		return opts;
	}

	private Options buildOptsList() {

		final Options opts = new Options();

		final Option optList = new Option(Command.LIST.getShortCmd(), Command.LIST.getLongCmd(), true,
				"List of files/directories inside a directory of the cloud. Argument is a directory at the cloud to be listed its contents.");
		optList.setRequired(true);
		opts.addOption(optList);

		final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
				"Passphrase to access the keystore file.");
		optStorePass.setRequired(false);
		opts.addOption(optStorePass);

		final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
				"Passphrase to access key inside the keystore file.");
		optKeyPass.setRequired(false);
		opts.addOption(optKeyPass);

		return opts;
	}

	private Options buildOpsRemove() {

		final Options opts = new Options();

		final Option optRemove = new Option(Command.REMOVE.getShortCmd(), Command.REMOVE.getLongCmd(), true,
				"Remove a file at the cloud. Argument is full path of the file to be removed from the cloud.");
		optRemove.setRequired(true);
		opts.addOption(optRemove);

		final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
				"Passphrase to access the keystore file.");
		optStorePass.setRequired(false);
		opts.addOption(optStorePass);

		final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
				"Passphrase to access key inside the keystore file.");
		optKeyPass.setRequired(false);
		opts.addOption(optKeyPass);

		return opts;
	}

}
