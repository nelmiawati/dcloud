package id.ac.polibatam.mj.dcloud.io;

import id.ac.polibatam.mj.dcloud.exception.BaseDcloudException;

import java.util.Map;

public interface ICloudClient {

    void upload(final String fromLocal, final String toRemote) throws BaseDcloudException;

    void download(final String fromRemote, final String toLocal) throws BaseDcloudException;

    void delete(final String fileRemote) throws BaseDcloudException;

    Map<String, FileType> list(final String dirRemote) throws BaseDcloudException;

    FileType getType(final String objRemote) throws BaseDcloudException;

    String getClientInfo();

    public enum FileType {
        FILE, DIRECTORY, UNKNOWN;
    }

}
