package id.ac.polibatam.mj.dcloud.io;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.users.FullAccount;
import id.ac.polibatam.mj.dcloud.exception.BaseDcloudException;
import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemExternalException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DropBoxClient implements ICloudClient {

    private static final Logger LOG = Logger.getLogger(DropBoxClient.class);

    private String authAccessToken = null;

    private DbxClientV2 dbxClient = null;

    public DropBoxClient(final String authAccessToken) {
        this.authAccessToken = authAccessToken;
        this.dbxClient = this.auth(this.authAccessToken);

        if (LOG.isTraceEnabled()) {
            LOG.trace(this.getClientInfo());
        }

    }

    @Override
    public void upload(final String fromLocal, final String toRemote)
            throws DcloudInvalidDataException, DcloudSystemInternalException, DcloudSystemExternalException {

        final File file = new File(fromLocal);
        if (!file.exists() || !file.isFile()) {
            throw new DcloudInvalidDataException("[" + fromLocal + "] is not a valid local file");
        }

        FileInputStream fis = null;
        UploadUploader uploader = null;
        try {

            fis = new FileInputStream(file);

            uploader = this.dbxClient.files().upload(toRemote);
            final FileMetadata meta = uploader.uploadAndFinish(fis);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Uploading into remote file=[" + meta.getName() + "]");
            }

        } catch (FileNotFoundException e) {
            throw new DcloudInvalidDataException(e.getMessage(), e);
        } catch (IOException e) {
            throw new DcloudSystemInternalException(e.getMessage(), e);
        } catch (DbxException e) {
            throw new DcloudSystemExternalException(e.getMessage(), e);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    if (LOG.isEnabledFor(Level.WARN)) {
                        LOG.warn(e.getMessage());
                    }
                }
            }
            if (null != uploader) {
                uploader.close();
            }
        }
    }

    @Override
    public void download(final String fromRemote, final String toLocal) throws BaseDcloudException {

        if (!FileType.FILE.equals(this.getType(fromRemote))) {
            throw new DcloudInvalidDataException("[" + fromRemote + "] is not a valid remote file");
        }

        FileOutputStream fos = null;
        DbxDownloader<FileMetadata> downloader = null;
        try {

            fos = new FileOutputStream(toLocal);
            downloader = this.dbxClient.files().download(fromRemote);
            final FileMetadata meta = downloader.download(fos);
            fos.flush();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Downloading from remote file=[" + meta.getName() + "]");
            }

        } catch (FileNotFoundException e) {
            throw new DcloudSystemInternalException(e.getMessage(), e);
        } catch (DbxException e) {
            throw new DcloudSystemExternalException(e.getMessage(), e);
        } catch (IOException e) {
            throw new DcloudSystemInternalException(e.getMessage(), e);
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    if (LOG.isEnabledFor(Level.WARN)) {
                        LOG.warn(e.getMessage());
                    }
                }
            }
            if (null != downloader) {
                downloader.close();
            }
        }
    }

    @Override
    public void delete(final String fileRemote) throws BaseDcloudException {
        try {

//			if (!FileType.FILE.equals(this.getType(fileRemote))) {
//				throw new DcloudInvalidDataException("[" + fileRemote + "] is not a valid remote file");
//			}

            final DeleteResult delResult = this.dbxClient.files().deleteV2(fileRemote);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Deleting remote file=[" + delResult.getMetadata().getName() + "]");
            }

        } catch (DbxException e) {
            throw new DcloudSystemExternalException(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, FileType> list(final String dirRemote) throws BaseDcloudException {

        if (null == dirRemote) {
            throw new DcloudInvalidDataException("[" + dirRemote + "] is not a valid remote directory");
        }
        String dirRemote1 = dirRemote;
        if ("/".equals(dirRemote1)) {
            dirRemote1 = "";
        }

        final Map<String, FileType> map = new HashMap<String, FileType>();
        try {

            // if (!FileType.DIRECTORY.equals(this.getType(dirRemote))) {
            // throw new DcloudInvalidDataException("[" + dirRemote + "] is not
            // a valid remote directory");
            // }

            final List<Metadata> entries = this.dbxClient.files().listFolder(dirRemote1).getEntries();
            for (Metadata entry : entries) {

                if (entry instanceof FileMetadata) {
                    map.put(entry.getName(), FileType.FILE);
                } else if (entry instanceof FolderMetadata) {
                    map.put(entry.getName(), FileType.DIRECTORY);
                } else {
                    map.put(entry.getName(), FileType.UNKNOWN);
                }
            }
        } catch (DbxException e) {
            throw new DcloudSystemExternalException(e.getMessage(), e);
        }
        return map;
    }

    @Override
    public FileType getType(final String objRemote) throws BaseDcloudException {

        if (null == objRemote) {
            throw new DcloudInvalidDataException("[" + objRemote + "] is not a valid remote directory");
        } else if ("/".equals(objRemote) || "".equals(objRemote)) {
            return FileType.DIRECTORY;
        }

        FileType type = FileType.UNKNOWN;
        try {
            final Metadata meta = this.dbxClient.files().getMetadata(objRemote);
            if (null == meta) {
                throw new DcloudInvalidDataException("[" + objRemote + "] is not avalid remote file or directory");
            }
            if (meta instanceof FileMetadata) {
                type = FileType.FILE;
            } else if (meta instanceof FolderMetadata) {
                type = FileType.DIRECTORY;
            }
        } catch (GetMetadataErrorException e) {
            throw new DcloudSystemExternalException(e.getMessage(), e);
        } catch (DbxException e) {
            throw new DcloudSystemExternalException(e.getMessage(), e);
        }

        return type;
    }

    private DbxClientV2 auth(final String authAccessToken) {
        final DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("dcloud/1.0")
                .withUserLocaleFrom(Locale.US).build();
        return new DbxClientV2(dbxRequestConfig, authAccessToken);
    }

    @Override
    public String getClientInfo() {

        String info = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);

            final FullAccount dbxAccountInfo = dbxClient.users().getCurrentAccount();
            pw.println(dbxAccountInfo.toString());

            info = sw.toString();
        } catch (DbxException e) {
            if (LOG.isEnabledFor(Level.WARN)) {
                LOG.warn(e.getMessage());
            }
        } finally {

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

        return info;

    }

}
