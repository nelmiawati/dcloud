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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author mia
 *
 */
public final class Cli {

	private static final Logger LOG = Logger.getLogger(Cli.class);
	private static final String DCLOUD_VERSION = ":: dCLOUD v1.0 :: miarifSOFT :: copyright (r) 2017";

	private static enum Command {

		/**
		 * 
		 */
		UPLOAD("ul", "upload", "Upload a file from local workspace to remote cloud."),
		/**
		 * 
		 */
		DOWNLOAD("dl", "download", "Download a file from remote cloud to local workspace."),
		/**
		 * 
		 */
		LIST("ls", "list", "List of files/directories inside a directory of the cloud. Argument is a directory at the cloud to be listed its contents."),
		/**
		 * 
		 */
		REMOVE("rm", "remove", "Remove a file at the cloud. Argument is full path of the file to be removed from the cloud."),
		/**
		 * 
		 */
		FROM_LOCAL("fl", "fromLocal", "The full path local file at the workspace to be uploaded from."),
		/**
		 * 
		 */
		TO_REMOTE("tr", "toRemote", "The full path remote file at the cloud of the uploaded file to be saved into."),
		/**
		 * 
		 */
		FROM_REMOTE("fr", "fromRemote", "The full path remote file at the cloud to be downloaded from."),
		/**
		 * 
		 */
		TO_LOCAL("tl", "toLocal", "The full path local file at the workspace of the downloaded file to be saved into."),
		/**
		 * 
		 */
		STORE_PASS("sp", "storePass", "Passphrase to access the keystore file."),
		/**
		 * 		
		 */
		KEY_PASS("kp", "keyPass", "Passphrase to access key inside the keystore file."),
		/**
		 * 
		 */
		HELP("h", "help", "Help with commands used at dCLOUD."),
		/**
		 * 
		 */
		VERSION("v", "version", "Print dCLOUD version."),;

		private static Map<String, Command> MAP_OPT_ENUM = new HashMap<String, Command>();

		static {
			for (Command cmd : Command.values()) {
				MAP_OPT_ENUM.put(cmd.shortCmd, cmd);
				MAP_OPT_ENUM.put(cmd.longCmd, cmd);
			}
		}

		private String shortCmd;
		private String longCmd;
		private String desc;

		private Command(final String shortCmd, final String longCmd, final String desc) {
			this.shortCmd = shortCmd;
			this.longCmd = longCmd;
			this.desc = desc;
		}

		public String getShortCmd() {
			return this.shortCmd;
		}

		public String getLongCmd() {
			return this.longCmd;
		}

		public String getDesc() {
			return this.desc;
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
			final CommandLine cl = clParser.parse(optsMain, this.args, true);
			Option[] opts = cl.getOptions();
			if (opts.length != 1) {
				this.printMainHelp();
			} else {
				final Command cmd = Command.getCommand(opts[0].getOpt());
				final List<String> args = opts[0].getValuesList();
				if (LOG.isTraceEnabled()) {
					LOG.trace("cmd=[" + cmd + "]");
					LOG.trace("cmdValSeparator=[" + opts[0].getValueSeparator() + "]");
					LOG.trace("cmdNbParams=[" + opts[0].getArgs() + "]");
					LOG.trace("cmdParams=[" + args + "]");
				}

				switch (cmd) {
				case UPLOAD: {
					if (LOG.isDebugEnabled()) {
						LOG.debug("##### UPLODAD #####");
					}						
					break;

				}
				case DOWNLOAD: {
					if (LOG.isDebugEnabled()) {
						LOG.debug("##### DOWNLOAD #####");
					}						
					break;

				}
				case LIST: {
					if (LOG.isDebugEnabled()) {
						LOG.debug("##### LIST #####");
					}						
					break;

				}
				case REMOVE: {
					if (LOG.isDebugEnabled()) {
						LOG.debug("##### REMOVE #####");
					}						
					break;

				}
				case VERSION: {
					if (LOG.isDebugEnabled()) {
						LOG.debug("##### VERSION #####");
					}						
					this.execVersion();
					break;

				}
				default: {
					if (LOG.isDebugEnabled()) {
						LOG.debug("##### DEFAULT #####");
					}						
					if (args.size() != 1) {
						this.printMainHelp();
					} else {
						this.execHelp(args.get(0));
					}
				}
				}
			}

		} catch (ParseException e) {
			if (LOG.isEnabledFor(Level.WARN)) {
				LOG.warn(e.getMessage());
			}
			this.printMainHelp();
		}
	}

	private void printMainHelp() {
		helpFormatter.printHelp(
				"Following are available commands on dCLOUD. To get details for each of command, type -h <command> or --help <command>",
				this.buildOptsMain());
	}

	private void execHelp(final String arg) {
		if (StringUtils.isEmpty(arg)) {
			this.printMainHelp();
		} else {
			final Command cmd = Command.getCommand(arg);
			if (LOG.isTraceEnabled()) {
				LOG.trace("cmd=[" + cmd + "]");
			}
			if (null == cmd) {
				this.printMainHelp();
			} else {

				switch (cmd) {
				case UPLOAD: {
					this.helpFormatter.printHelp(
							"-" + Command.UPLOAD.shortCmd + "," + "--" + Command.UPLOAD.longCmd + "; "
									+ Command.UPLOAD.getDesc() + " Following are parameters for this command: ",
							this.buildOptsUpload());
					break;

				}
				case DOWNLOAD: {
					this.helpFormatter.printHelp(
							"-" + Command.DOWNLOAD.shortCmd + "," + "--" + Command.DOWNLOAD.longCmd + "; "
									+ Command.DOWNLOAD.getDesc() + " Following are parameters for this command: ",
							this.buildOptsDownload());
					break;

				}
				case LIST: {
					this.helpFormatter.printHelp(
							"-" + Command.LIST.shortCmd + "," + "--" + Command.LIST.longCmd + "; "
									+ Command.LIST.getDesc() + " Following are parameters for this command: ",
							this.buildOptsList());
					break;

				}
				case REMOVE: {
					this.helpFormatter.printHelp(
							"-" + Command.REMOVE.shortCmd + "," + "--" + Command.REMOVE.longCmd + "; "
									+ Command.REMOVE.getDesc() + " Following are parameters for this command: ",
							this.buildOptsRemove());
					break;

				}
				case HELP: {
					this.helpFormatter.printHelp(
							"-" + Command.REMOVE.shortCmd + "," + "--" + Command.REMOVE.longCmd + "; "
									+ Command.HELP.desc + " Following are parameters for this command: ",
							new Options());
					break;

				}
				case VERSION: {
					this.helpFormatter.printHelp(
							"-" + Command.VERSION.shortCmd + "," + "--" + Command.VERSION.longCmd + "; "
									+ Command.VERSION.getDesc() + " Following are parameters for this command: ",
							new Options());
					break;

				}
				default: {
					this.printMainHelp();
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
				Command.UPLOAD.getDesc());
		optsG.addOption(optUpload);

		final Option optDownload = new Option(Command.DOWNLOAD.getShortCmd(), Command.DOWNLOAD.getLongCmd(), false,
				Command.DOWNLOAD.getDesc());
		optsG.addOption(optDownload);

		final Option optList = new Option(Command.LIST.getShortCmd(), Command.LIST.getLongCmd(), true,
				Command.LIST.getDesc());
		optsG.addOption(optList);

		final Option optRemove = new Option(Command.REMOVE.getShortCmd(), Command.REMOVE.getLongCmd(), true,
				Command.REMOVE.desc);
		optsG.addOption(optRemove);

		final Option optHelp = new Option(Command.HELP.getShortCmd(), Command.HELP.getLongCmd(), true,
				Command.HELP.getDesc());
		optHelp.setOptionalArg(true);
		optsG.addOption(optHelp);

		final Option optVersion = new Option(Command.VERSION.getShortCmd(), Command.VERSION.getLongCmd(), false,
				Command.VERSION.getDesc());
		optsG.addOption(optVersion);

		optsG.setRequired(true);
		opts.addOptionGroup(optsG);

		return opts;
	}

	private Options buildOptsUpload() {

		final Options opts = new Options();

		// final Option optUpload = new Option(Command.UPLOAD.getShortCmd(),
		// Command.UPLOAD.getLongCmd(), false,
		// "Upload a file from local workspace to remote cloud.");
		// optUpload.setRequired(true);
		// opts.addOption(optUpload);

		final Option optFromLocal = new Option(Command.FROM_LOCAL.getShortCmd(), Command.FROM_LOCAL.getLongCmd(), true,
				Command.FROM_LOCAL.getDesc());
		optFromLocal.setRequired(true);
		opts.addOption(optFromLocal);

		final Option optToRemote = new Option(Command.TO_REMOTE.getShortCmd(), Command.TO_REMOTE.getLongCmd(), true,
				Command.TO_REMOTE.getDesc());
		optToRemote.setRequired(true);
		opts.addOption(optToRemote);

		final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
				Command.STORE_PASS.getDesc());
		optStorePass.setRequired(false);
		opts.addOption(optStorePass);

		final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
				Command.KEY_PASS.getDesc());
		optKeyPass.setRequired(false);
		opts.addOption(optKeyPass);

		return opts;
	}

	private Options buildOptsDownload() {

		final Options opts = new Options();

		// final Option optDownload = new Option(Command.DOWNLOAD.getShortCmd(),
		// Command.DOWNLOAD.getLongCmd(), false,
		// "Download a file from remote cloud to local workspace.");
		// optDownload.setRequired(true);
		// opts.addOption(optDownload);

		final Option optFromRemote = new Option(Command.FROM_REMOTE.getShortCmd(), Command.FROM_REMOTE.getLongCmd(),
				true, Command.FROM_REMOTE.getDesc());
		optFromRemote.setRequired(true);
		opts.addOption(optFromRemote);

		final Option optToLocal = new Option(Command.TO_LOCAL.getShortCmd(), Command.TO_LOCAL.getLongCmd(), true,
				Command.TO_LOCAL.getDesc());
		optToLocal.setRequired(true);
		opts.addOption(optToLocal);

		final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
				Command.STORE_PASS.getDesc());
		optStorePass.setRequired(false);
		opts.addOption(optStorePass);

		final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
				Command.KEY_PASS.getDesc());
		optKeyPass.setRequired(false);
		opts.addOption(optKeyPass);

		return opts;
	}

	private Options buildOptsList() {

		final Options opts = new Options();

		// final Option optList = new Option(Command.LIST.getShortCmd(),
		// Command.LIST.getLongCmd(), true,
		// "List of files/directories inside a directory of the cloud. Argument
		// is a directory at the cloud to be listed its contents.");
		// optList.setRequired(true);
		// opts.addOption(optList);

		final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
				Command.STORE_PASS.getDesc());
		optStorePass.setRequired(false);
		opts.addOption(optStorePass);

		final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
				Command.KEY_PASS.getDesc());
		optKeyPass.setRequired(false);
		opts.addOption(optKeyPass);

		return opts;
	}

	private Options buildOptsRemove() {

		final Options opts = new Options();

		// final Option optRemove = new Option(Command.REMOVE.getShortCmd(),
		// Command.REMOVE.getLongCmd(), true,
		// "Remove a file at the cloud. Argument is full path of the file to be
		// removed from the cloud.");
		// optRemove.setRequired(true);
		// opts.addOption(optRemove);

		final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
				Command.STORE_PASS.getDesc());
		optStorePass.setRequired(false);
		opts.addOption(optStorePass);

		final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
				Command.KEY_PASS.getDesc());
		optKeyPass.setRequired(false);
		opts.addOption(optKeyPass);

		return opts;
	}

}
