package id.ac.polibatam.mj.dcloud.io;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.util.Converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class DcloudFileInputStream extends FileInputStream {

    private DcloudHeader header = null;

    public DcloudFileInputStream(final String fileName)
            throws FileNotFoundException, IOException, DcloudInvalidDataException {
        super(fileName);
        this.init();
    }

    public DcloudFileInputStream(final File file)
            throws FileNotFoundException, IOException, DcloudInvalidDataException {
        super(file);
        this.init();
    }

    private void init() throws DcloudInvalidDataException, IOException {
        this.checkDcloudIndicator();
        this.parseHeader();
    }

    public DcloudHeader getHeader() {
        return this.header;
    }

    private void checkDcloudIndicator() throws IOException, DcloudInvalidDataException {

        final byte[] dcloudIndicator = new byte[DcloudHeader.getIndicator().length];
        this.read(dcloudIndicator);
        if (!Arrays.equals(DcloudHeader.getIndicator(), dcloudIndicator)) {
            throw new DcloudInvalidDataException("INVALID file, found no dcloudIndicator=["
                    + Converter.convertSignedByteToHexString(DcloudHeader.getIndicator()) + "]");
        }

    }

    private void parseHeader() throws IOException, DcloudInvalidDataException {

        // Get header length
        final byte[] headerLen = new byte[2];
        if (this.read(headerLen) != headerLen.length) {
            throw new DcloudInvalidDataException("INVALID file, no more bytes available to fetch header length");
        }
        final int headerLenInt = Integer.parseInt(Converter.convertSignedByteToHexString(headerLen), 16);
        if (headerLenInt > DcloudHeader.HEADER_MAX_LEN) {
            throw new DcloudInvalidDataException("INVALID file, header length=[" + headerLenInt
                    + "] exceeds HEADER_MAX_LEN=[" + DcloudHeader.HEADER_MAX_LEN + "]");
        }

        // Get header
        final byte[] headerBytes = new byte[headerLenInt];
        if (this.read(headerBytes) != headerBytes.length) {
            throw new DcloudInvalidDataException("INVALID file, no more bytes available to fetch header");
        }
        this.header = DcloudHeader.parseHeader(headerBytes);

    }

}
