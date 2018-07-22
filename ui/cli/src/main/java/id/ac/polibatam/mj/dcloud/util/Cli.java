/**
 *
 */
package id.ac.polibatam.mj.dcloud.util;

import id.ac.polibatam.mj.dcloud.algo.FileDispersal;
import id.ac.polibatam.mj.dcloud.config.DcloudConfig;
import id.ac.polibatam.mj.dcloud.config.DcloudConfig.Param;
import id.ac.polibatam.mj.dcloud.config.DcloudSecureConfig;
import id.ac.polibatam.mj.dcloud.exception.BaseDcloudException;
import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudSystemInternalRuntimeException;
import id.ac.polibatam.mj.dcloud.io.CloudClientFactory;
import id.ac.polibatam.mj.dcloud.io.ICloudClient;
import org.apache.commons.cli.*;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mia
 */
public final class Cli {

    private static final Logger LOG = Logger.getLogger(Cli.class);
    private static final String DCLOUD_VERSION = ":: dCLOUD v1.0 :: miarifSOFT :: copyright (r) 2018";

    private static final int HELP_WIDTH = 640;
    private static final int HELP_LEFT_PAD = 4;
    private static final int HELP_DESC_PAD = 4;

    private static final DcloudConfig CONFIG = DcloudConfig.getInstance();
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

    public String exec() {

        final Options optsMain = this.buildOptsMain();

        try {
            final CommandLine cl = clParser.parse(optsMain, this.args, true);
            Option[] opts = cl.getOptions();
            if (opts.length != 1) {
                return this.printMainHelp();
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
                            LOG.debug("##### UPLOAD #####");
                        }
                        //TODO:
                        return "##### UPLOAD #####";

                    }
                    case DOWNLOAD: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("##### DOWNLOAD #####");
                        }
                        //TODO:
                        return "##### DOWNLOAD #####";

                    }
                    case LIST: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("##### LIST #####");
                        }
                        return this.execList();

                    }
                    case REMOVE: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("##### REMOVE #####");
                        }
                        //TODO:
                        return this.execRemove();

                    }
                    case PING: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("##### PING #####");
                        }
                        return this.execPing();

                    }
                    case VERSION: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("##### VERSION #####");
                        }
                        return this.execVersion();

                    }
                    default: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("##### DEFAULT #####");
                        }
                        if (args.size() != 1) {
                            return this.printMainHelp();
                        } else {
                            return this.execHelp(args.get(0));
                        }
                    }
                }
            }

        } catch (ParseException e) {
            if (LOG.isEnabledFor(Level.WARN)) {
                LOG.warn(e.getMessage());
            }
            return this.printMainHelp();
        }

    }

    private String printMainHelp() {

        String cmdLog = null;
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        helpFormatter.printHelp(pw, HELP_WIDTH, "<command> [arg] [<param1> ... <paramN>]",
                "Following are available commands on dCLOUD. To get details for each of command, type -h <command> or --help <command>",
                this.buildOptsMain(), HELP_LEFT_PAD, HELP_DESC_PAD, null);
        cmdLog = sw.toString();

        this.closeCmdLogWriter(pw, sw);

        return cmdLog;
        // helpFormatter.printHelp(
        // "Following are available commands on dCLOUD. To get details for each
        // of command, type -h <command> or --help <command>",
        // this.buildOptsMain());

    }

    //TODO:
    private String execUpload() throws DcloudInvalidDataException {

        final Options optsUpload = this.buildOptsUpload();

        final String[] cmdArgs = new String[this.args.length - 1];
        System.arraycopy(this.args, 1, cmdArgs, 0, cmdArgs.length);
        if (LOG.isTraceEnabled()) {
            LOG.trace("cmdArgs=[" + Arrays.toString(cmdArgs) + "]");
        }

        String cmdLog = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            final CommandLine cl = clParser.parse(optsUpload, cmdArgs);
            Option[] opts = cl.getOptions();
            if (opts.length < 4) {
                cmdLog = this.execHelp(this.args[0]);
            } else {

                sw = new StringWriter();
                pw = new PrintWriter(sw);

                // Get cloud details
                final String localWs = CONFIG.getString(Param.DCLOUD_LOCAL_WORKSPACE);
                final int nbClouds = CONFIG.getInt(Param.DCLOUD_COUNT);
                final int threshold = CONFIG.getInt(Param.DCLOUD_THRESHOLD);

                // Get input parameters
                final String storePass = cl.getOptionValue(Command.STORE_PASS.shortCmd);
                final String keyPass = cl.getOptionValue(Command.KEY_PASS.shortCmd);
                final String fromLocal = cl.getOptionValue(Command.FROM_LOCAL.shortCmd);
                final String toRemote = cl.getOptionValue(Command.TO_REMOTE.shortCmd);

                final File input = new File(localWs + File.separator + fromLocal);
                if (!input.exists() || !input.isFile()) {
                    new DcloudInvalidDataException("INVALID fromLocal=[" + input.getAbsolutePath() + "]");
                }

                // Get sconfig
                System.setProperty("sconfigPassword", storePass);
                final DcloudSecureConfig sconfig = DcloudSecureConfig.getInstance();

                // dispersal
                final FileDispersal dispersal = new FileDispersal(nbClouds, threshold);
                //dispersal.disperse(input, outputDir, useSalt)

                // pw.println(":: TEST CONNECTION TO DCLOUD SERVERS ::");
                // for (int i = 0; i < nbCloud; i++) {
                // final String idx = Integer.toString(i + 1);
                //
                // final String client = CONFIG.getString("dcloud" + idx,
                // DcloudConfig.Param.CLIENT);
                // final String credential = CONFIG.getString("dcloud" + idx,
                // DcloudConfig.Param.CREDENTIAL);
                //
                // final byte[] accessToken = sconfig.getByte(credential,
                // keyPass);
                // final String strAccessToken = new String(accessToken,
                // "UTF-8");
                //
                // if (LOG.isTraceEnabled()) {
                // LOG.trace("client=[" + client + "]");
                // LOG.trace("credential=[" + credential + "]");
                // // LOG.trace("strAccessToken=[" + strAccessToken + "]");
                // }
                //
                // pw.println("Connecting to dcloud" + idx + "...");
                // final ICloudClient cloudClient =
                // CloudClientFactory.getCloudClient(client, strAccessToken);
                // pw.println(cloudClient.getClientInfo());
                // }

                cmdLog = sw.toString();
            }
        } catch (ParseException e) {
            if (LOG.isEnabledFor(Level.WARN)) {
                LOG.warn(e.getMessage());
            }
            return this.execHelp(this.args[0]);
        } finally {
            this.closeCmdLogWriter(pw, sw);
        }

        return cmdLog;
    }


    private String execList() {

        final Options optsList = this.buildOptsList();

        final String[] cmdArgs = new String[this.args.length - 1];
        System.arraycopy(this.args, 1, cmdArgs, 0, cmdArgs.length);
        if (LOG.isTraceEnabled()) {
            LOG.trace("cmdArgs=[" + Arrays.toString(cmdArgs) + "]");
        }
        final String remoteDirName = cmdArgs[0];

        String cmdLog = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            final CommandLine cl = clParser.parse(optsList, cmdArgs);
            Option[] opts = cl.getOptions();
            if (opts.length < 2) {
                cmdLog = this.execHelp(this.args[0]);
            } else {

                sw = new StringWriter();
                pw = new PrintWriter(sw);
                final int nbCloud = CONFIG.getInt(DcloudConfig.Param.DCLOUD_COUNT);

                // dcloud server
                final String storePass = cl.getOptionValue(Command.STORE_PASS.shortCmd);
                final String keyPass = cl.getOptionValue(Command.KEY_PASS.shortCmd);
                // if (LOG.isTraceEnabled()) {
                // LOG.trace("storePass=[" + storePass + "]");
                // LOG.trace("keyPass=[" + keyPass + "]");
                // }
                System.setProperty("sconfigPassword", storePass);
                final DcloudSecureConfig sconfig = DcloudSecureConfig.getInstance();
                pw.println(":: LIST DIRECTORY [" + remoteDirName + "] AT DCLOUD SERVERS ::");
                for (int i = 0; i < nbCloud; i++) {
                    final String idx = Integer.toString(i + 1);

                    final String client = CONFIG.getString("dcloud" + idx, DcloudConfig.Param.CLIENT);
                    final String credential = CONFIG.getString("dcloud" + idx, DcloudConfig.Param.CREDENTIAL);

                    final byte[] accessToken = sconfig.getByte(credential, keyPass);
                    final String strAccessToken = new String(accessToken, "UTF-8");

                    if (LOG.isTraceEnabled()) {
                        LOG.trace("client=[" + client + "]");
                        LOG.trace("credential=[" + credential + "]");
                        // LOG.trace("strAccessToken=[" + strAccessToken + "]");
                    }

                    pw.println("List directory [" + remoteDirName + "] at dcloud" + idx + "...");
                    final ICloudClient cloudClient = CloudClientFactory.getCloudClient(client, strAccessToken);
                    try {
                        pw.println(cloudClient.list(remoteDirName));
                        pw.println("SUCCESS listing directory [" + remoteDirName + "] at dcloud" + idx + "...");
                    } catch (BaseDcloudException e) {
                        pw.println(e.getMessage());
                        if (LOG.isEnabledFor(Level.ERROR)) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }
                pw.println();

                cmdLog = sw.toString();

            }
        } catch (UnsupportedEncodingException e) {
            throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
        } catch (ParseException e) {
            if (LOG.isEnabledFor(Level.WARN)) {
                LOG.warn(e.getMessage());
            }
            return this.execHelp(this.args[0]);
        } finally {
            this.closeCmdLogWriter(pw, sw);
        }

        return cmdLog;

    }

    private String execRemove() {

        final Options optsRemove = this.buildOptsRemove();

        final String[] cmdArgs = new String[this.args.length - 1];
        System.arraycopy(this.args, 1, cmdArgs, 0, cmdArgs.length);
        if (LOG.isTraceEnabled()) {
            LOG.trace("cmdArgs=[" + Arrays.toString(cmdArgs) + "]");
        }
        final String remoteFileName = cmdArgs[0];

        String cmdLog = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            final CommandLine cl = clParser.parse(optsRemove, cmdArgs);
            Option[] opts = cl.getOptions();
            if (opts.length < 2) {
                cmdLog = this.execHelp(this.args[0]);
            } else {

                sw = new StringWriter();
                pw = new PrintWriter(sw);
                final int nbCloud = CONFIG.getInt(DcloudConfig.Param.DCLOUD_COUNT);

                // dcloud server
                final String storePass = cl.getOptionValue(Command.STORE_PASS.shortCmd);
                final String keyPass = cl.getOptionValue(Command.KEY_PASS.shortCmd);
                // if (LOG.isTraceEnabled()) {
                // LOG.trace("storePass=[" + storePass + "]");
                // LOG.trace("keyPass=[" + keyPass + "]");
                // }
                System.setProperty("sconfigPassword", storePass);
                final DcloudSecureConfig sconfig = DcloudSecureConfig.getInstance();
                pw.println(":: REMOVE FILE [" + remoteFileName + "] AT DCLOUD SERVERS ::");
                for (int i = 0; i < nbCloud; i++) {
                    final String idx = Integer.toString(i + 1);

                    final String client = CONFIG.getString("dcloud" + idx, DcloudConfig.Param.CLIENT);
                    final String credential = CONFIG.getString("dcloud" + idx, DcloudConfig.Param.CREDENTIAL);

                    final byte[] accessToken = sconfig.getByte(credential, keyPass);
                    final String strAccessToken = new String(accessToken, "UTF-8");

                    if (LOG.isTraceEnabled()) {
                        LOG.trace("client=[" + client + "]");
                        LOG.trace("credential=[" + credential + "]");
                        // LOG.trace("strAccessToken=[" + strAccessToken + "]");
                    }

                    pw.println("Remove file [" + remoteFileName + "] at dcloud" + idx + "...");
                    final ICloudClient cloudClient = CloudClientFactory.getCloudClient(client, strAccessToken);
                    try {
                        cloudClient.delete(remoteFileName);
                        pw.println("SUCCESS removing file [" + remoteFileName + "] at dcloud" + idx + "...");
                    } catch (BaseDcloudException e) {
                        pw.println(e.getMessage());
                        if (LOG.isEnabledFor(Level.ERROR)) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }
                pw.println();

                cmdLog = sw.toString();

            }
        } catch (UnsupportedEncodingException e) {
            throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
        } catch (ParseException e) {
            if (LOG.isEnabledFor(Level.WARN)) {
                LOG.warn(e.getMessage());
            }
            return this.execHelp(this.args[0]);
        } finally {
            this.closeCmdLogWriter(pw, sw);
        }

        return cmdLog;

    }


    private String execPing() {

        final Options optsPing = this.buildOptsPing();

        final String[] cmdArgs = new String[this.args.length - 1];
        System.arraycopy(this.args, 1, cmdArgs, 0, cmdArgs.length);
        if (LOG.isTraceEnabled()) {
            LOG.trace("cmdArgs=[" + Arrays.toString(cmdArgs) + "]");
        }

        String cmdLog = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            final CommandLine cl = clParser.parse(optsPing, cmdArgs);
            Option[] opts = cl.getOptions();
            if (opts.length < 2) {
                cmdLog = this.execHelp(this.args[0]);
            } else {

                sw = new StringWriter();
                pw = new PrintWriter(sw);
                final int nbCloud = CONFIG.getInt(DcloudConfig.Param.DCLOUD_COUNT);

                // Iterate config
                pw.println(":: CONFIGURATIONS ::");
                for (DcloudConfig.Param param : DcloudConfig.Param.values()) {
                    if (!DcloudConfig.Param.CLIENT.equals(param) && !DcloudConfig.Param.CREDENTIAL.equals(param)) {
                        pw.println(param.getName().concat("=[").concat(CONFIG.getString(param)).concat("]"));
                    } else {
                        for (int i = 0; i < nbCloud; i++) {
                            final String idx = Integer.toString(i + 1);
                            pw.println("dcloud" + idx + "-" + param.getName().concat("=[")
                                    .concat(CONFIG.getString("dcloud" + idx, param)).concat("]"));
                        }
                    }
                }
                pw.println();

                // dcloud server
                final String storePass = cl.getOptionValue(Command.STORE_PASS.shortCmd);
                final String keyPass = cl.getOptionValue(Command.KEY_PASS.shortCmd);
                // if (LOG.isTraceEnabled()) {
                // LOG.trace("storePass=[" + storePass + "]");
                // LOG.trace("keyPass=[" + keyPass + "]");
                // }
                System.setProperty("sconfigPassword", storePass);
                final DcloudSecureConfig sconfig = DcloudSecureConfig.getInstance();
                pw.println(":: TEST CONNECTION TO DCLOUD SERVERS ::");
                for (int i = 0; i < nbCloud; i++) {
                    final String idx = Integer.toString(i + 1);

                    final String client = CONFIG.getString("dcloud" + idx, DcloudConfig.Param.CLIENT);
                    final String credential = CONFIG.getString("dcloud" + idx, DcloudConfig.Param.CREDENTIAL);

                    final byte[] accessToken = sconfig.getByte(credential, keyPass);
                    final String strAccessToken = new String(accessToken, "UTF-8");

                    if (LOG.isTraceEnabled()) {
                        LOG.trace("client=[" + client + "]");
                        LOG.trace("credential=[" + credential + "]");
                        // LOG.trace("strAccessToken=[" + strAccessToken + "]");
                    }

                    pw.println("Connecting to dcloud" + idx + "...");
                    final ICloudClient cloudClient = CloudClientFactory.getCloudClient(client, strAccessToken);
                    pw.println(cloudClient.getClientInfo());
                }
                pw.println();

                cmdLog = sw.toString();

            }
        } catch (UnsupportedEncodingException e) {
            throw new DcloudSystemInternalRuntimeException(e.getMessage(), e);
        } catch (ParseException e) {
            if (LOG.isEnabledFor(Level.WARN)) {
                LOG.warn(e.getMessage());
            }
            return this.execHelp(this.args[0]);
        } finally {
            this.closeCmdLogWriter(pw, sw);
        }

        return cmdLog;

    }

    private String execHelp(final String arg) {

        if (StringUtils.isEmpty(arg)) {
            return printMainHelp();
        } else {
            final Command cmd = Command.getCommand(arg);
            if (LOG.isTraceEnabled()) {
                LOG.trace("cmd=[" + cmd + "]");
            }
            if (null == cmd) {
                return this.printMainHelp();
            } else {

                String cmdLog = null;
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);

                switch (cmd) {
                    case UPLOAD: {
                        this.helpFormatter.printHelp(pw, HELP_WIDTH,
                                "-" + Command.UPLOAD.shortCmd + "," + "--" + Command.UPLOAD.longCmd,
                                Command.UPLOAD.getDesc() + " Following are parameters for this command: ",
                                this.buildOptsUpload(), HELP_LEFT_PAD, HELP_DESC_PAD, null, true);
                        cmdLog = sw.toString();
                        // this.helpFormatter.printHelp(
                        // "-" + Command.UPLOAD.shortCmd + "," + "--" +
                        // Command.UPLOAD.longCmd + "; "
                        // + Command.UPLOAD.getDesc() + " Following are parameters
                        // for this command: ",
                        // this.buildOptsUpload());
                        break;

                    }
                    case DOWNLOAD: {
                        this.helpFormatter.printHelp(pw, HELP_WIDTH,
                                "-" + Command.DOWNLOAD.shortCmd + "," + "--" + Command.DOWNLOAD.longCmd,
                                Command.DOWNLOAD.getDesc() + " Following are parameters for this command: ",
                                this.buildOptsDownload(), HELP_LEFT_PAD, HELP_DESC_PAD, null, true);
                        cmdLog = sw.toString();
                        // this.helpFormatter.printHelp(
                        // "-" + Command.DOWNLOAD.shortCmd + "," + "--" +
                        // Command.DOWNLOAD.longCmd + "; "
                        // + Command.DOWNLOAD.getDesc() + " Following are parameters
                        // for this command: ",
                        // this.buildOptsDownload());
                        break;

                    }
                    case LIST: {
                        this.helpFormatter.printHelp(pw, HELP_WIDTH,
                                "-" + Command.LIST.shortCmd + "," + "--" + Command.LIST.longCmd
                                        + " <remoteDirName>",
                                Command.LIST.getDesc() + " Following are parameters for this command: ",
                                this.buildOptsList(), HELP_LEFT_PAD, HELP_DESC_PAD, null, true);
                        cmdLog = sw.toString();
                        // this.helpFormatter.printHelp(
                        // "-" + Command.LIST.shortCmd + "," + "--" +
                        // Command.LIST.longCmd + "; "
                        // + Command.LIST.getDesc() + " Following are parameters for
                        // this command: ",
                        // this.buildOptsList());
                        break;

                    }
                    case REMOVE: {
                        this.helpFormatter.printHelp(pw, HELP_WIDTH,
                                "-" + Command.REMOVE.shortCmd + "," + "--" + Command.REMOVE.longCmd
                                        + " <remoteFileName>",
                                Command.REMOVE.getDesc() + " Following are parameters for this command: ",
                                this.buildOptsRemove(), HELP_LEFT_PAD, HELP_DESC_PAD, null, true);
                        cmdLog = sw.toString();
                        // this.helpFormatter.printHelp(
                        // "-" + Command.REMOVE.shortCmd + "," + "--" +
                        // Command.REMOVE.longCmd + "; "
                        // + Command.REMOVE.getDesc() + " Following are parameters
                        // for this command: ",
                        // this.buildOptsRemove());
                        break;

                    }
                    case PING: {
                        this.helpFormatter.printHelp(pw, HELP_WIDTH,
                                "-" + Command.PING.shortCmd + "," + "--" + Command.PING.longCmd + " <param1> ... <paramN>",
                                Command.PING.getDesc() + " Following are parameters for this command: ",
                                this.buildOptsPing(), HELP_LEFT_PAD, HELP_DESC_PAD, null, true);
                        cmdLog = sw.toString();
                        // this.helpFormatter.printHelp(
                        // "-" + Command.PING.shortCmd + "," + "--" +
                        // Command.PING.longCmd + "; "
                        // + Command.PING.getDesc() + " Following are parameters for
                        // this command: ",
                        // this.buildOptsPing());
                        break;

                    }
                    case VERSION: {
                        this.helpFormatter.printHelp(pw, HELP_WIDTH,
                                "-" + Command.VERSION.shortCmd + "," + "--" + Command.VERSION.longCmd,
                                Command.VERSION.getDesc(), new Options(),
                                HELP_LEFT_PAD, HELP_DESC_PAD, null, true);
                        cmdLog = sw.toString();
                        // this.helpFormatter.printHelp(
                        // "-" + Command.VERSION.shortCmd + "," + "--" +
                        // Command.VERSION.longCmd + "; "
                        // + Command.VERSION.getDesc() + " Following are parameters
                        // for this command: ",
                        // new Options());
                        break;

                    }
                    case HELP: {
                        this.helpFormatter.printHelp(pw, HELP_WIDTH,
                                "-" + Command.HELP.shortCmd + "," + "--" + Command.HELP.longCmd + " <commandShort|commandLong>",
                                Command.HELP.desc, new Options(),
                                HELP_LEFT_PAD, HELP_DESC_PAD, null, true);
                        cmdLog = sw.toString();
                        // this.helpFormatter.printHelp(
                        // "-" + Command.REMOVE.shortCmd + "," + "--" +
                        // Command.REMOVE.longCmd + "; "
                        // + Command.HELP.desc + " Following are parameters for this
                        // command: ",
                        // new Options());
                        break;

                    }
                    default: {
                        cmdLog = this.printMainHelp();
                    }
                }

                this.closeCmdLogWriter(pw, sw);
                return cmdLog;
            }
        }
    }

    private String execVersion() {
        return DCLOUD_VERSION;
    }

    private void closeCmdLogWriter(final PrintWriter pw, final StringWriter sw) {

        if (null != pw) {
            pw.flush();
            pw.close();
        }

        if (null != sw) {
            sw.flush();
            try {
                sw.close();
            } catch (IOException e) {
                if (LOG.isEnabledFor(Level.WARN)) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }

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

        final Option optPing = new Option(Command.PING.getShortCmd(), Command.PING.getLongCmd(), false,
                Command.PING.getDesc());
        optsG.addOption(optPing);

        final Option optVersion = new Option(Command.VERSION.getShortCmd(), Command.VERSION.getLongCmd(), false,
                Command.VERSION.getDesc());
        optsG.addOption(optVersion);

        final Option optHelp = new Option(Command.HELP.getShortCmd(), Command.HELP.getLongCmd(), true,
                Command.HELP.getDesc());
        optHelp.setOptionalArg(true);
        optsG.addOption(optHelp);

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
        optStorePass.setRequired(true);
        opts.addOption(optStorePass);

        final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
                Command.KEY_PASS.getDesc());
        optKeyPass.setRequired(true);
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
        optStorePass.setRequired(true);
        opts.addOption(optStorePass);

        final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
                Command.KEY_PASS.getDesc());
        optKeyPass.setRequired(true);
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
        optStorePass.setRequired(true);
        opts.addOption(optStorePass);

        final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
                Command.KEY_PASS.getDesc());
        optKeyPass.setRequired(true);
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
        optStorePass.setRequired(true);
        opts.addOption(optStorePass);

        final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
                Command.KEY_PASS.getDesc());
        optKeyPass.setRequired(true);
        opts.addOption(optKeyPass);

        return opts;
    }

    private Options buildOptsPing() {

        final Options opts = new Options();

        // final Option optPing = new Option(Command.PING.getShortCmd(),
        // Command.PING.getLongCmd(), false,
        // "Ping connection to cloud servers.");
        // optRemove.setRequired(true);
        // opts.addOption(optPing);

        final Option optStorePass = new Option(Command.STORE_PASS.getShortCmd(), Command.STORE_PASS.getLongCmd(), true,
                Command.STORE_PASS.getDesc());
        optStorePass.setRequired(true);
        opts.addOption(optStorePass);

        final Option optKeyPass = new Option(Command.KEY_PASS.getShortCmd(), Command.KEY_PASS.getLongCmd(), true,
                Command.KEY_PASS.getDesc());
        optKeyPass.setRequired(true);
        opts.addOption(optKeyPass);

        return opts;
    }

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
        LIST("ls", "list", "List of files/directories inside a directory of the cloud."),
        /**
         *
         */
        REMOVE("rm", "remove", "Remove a file at the cloud."),
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
        PING("p", "ping", "Ping connection to cloud servers."),
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

        public static Command getCommand(final String opt) {
            return MAP_OPT_ENUM.get(opt);
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
    }
}
