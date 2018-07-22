package id.ac.polibatam.mj.dcloud.io;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import id.ac.polibatam.mj.dcloud.util.Converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DcloudFileOutputStream extends FileOutputStream {

    public DcloudFileOutputStream(final String fileName, final DcloudHeader header)
            throws DcloudInvalidDataException, FileNotFoundException, IOException {
        super(fileName);
        this.init(header);
    }

    public DcloudFileOutputStream(final File file, final DcloudHeader header)
            throws DcloudInvalidDataException, FileNotFoundException, IOException {
        super(file);
        this.init(header);
    }

    private void init(final DcloudHeader header) throws DcloudInvalidDataException, IOException {
        try {
            header.validate();
        } catch (DcloudInvalidDataRuntimeException e) {
            throw new DcloudInvalidDataException(e.getMessage(), e);
        }
        this.writeDcloudIndicator();
        this.writeHeader(header);
    }

    private void writeDcloudIndicator() throws IOException {
        this.write(DcloudHeader.getIndicator());
    }

    private void writeHeader(final DcloudHeader header) throws IOException, DcloudInvalidDataException {
        try {
            final byte[] headerByte = header.generateHeader();
            String headerLenStr = Integer.toString(headerByte.length, 16);
            while (headerLenStr.length() < 4) {
                headerLenStr = "0".concat(headerLenStr);
            }
            this.write(Converter.convertHexStringToSignedByte(headerLenStr));
            this.write(headerByte);
        } catch (DcloudSystemInternalException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

}
