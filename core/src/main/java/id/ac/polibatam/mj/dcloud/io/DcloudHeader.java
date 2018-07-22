package id.ac.polibatam.mj.dcloud.io;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import id.ac.polibatam.mj.dcloud.util.Converter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DcloudHeader implements Serializable, Cloneable {

    public static final int HEADER_MAX_LEN = 288;
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4262627594626902686L;
    private static final Logger LOG = Logger.getLogger(DcloudHeader.class);
    private static final byte[] DCLOUD_INDICATOR = {(byte) 0x64, (byte) 0x63, (byte) 0x6C, (byte) 0x6F, (byte) 0x75,
            (byte) 0x64};
    private static final byte[] DCLOUD_VERSION = {(byte) 0x01, (byte) 0x00};

    private static final int TAG_DISPERSAL_IDX_VALUE_MIN = 0;

    private static final int TAG_DISPERSAL_IDX_VALUE_MAX = 255;

    private static final int TAG_THRESHOLD_VALUE_MIN = 1;

    private static final int TAG_THRESHOLD_VALUE_MAX = 254;

    private static final int TAG_PADD_LEN_VALUE_MIN = 0;

    private static final int TAG_PADD_LEN_VALUE_MAX = 255;

    private static final int TAG_MD5_LEN_VALUE = 16;
    private byte[] vSecretShare = null;
    private int dispersalIdx = 0;
    private int m = 0;
    private int paddLen = 0;
    private byte[] md5 = null;

    public DcloudHeader() {
    }

    public static byte[] getIndicator() {
        return DCLOUD_INDICATOR.clone();
    }

    public static byte[] getVersion() {
        return DCLOUD_VERSION.clone();
    }

    public static DcloudHeader parseHeader(final byte[] headerBytes) throws DcloudInvalidDataException {

        DcloudHeader header = null;

        if (null != headerBytes) {

            header = new DcloudHeader();
            int idx = 0;
            while ((idx + 1) < headerBytes.length) { // Minimum valid headerElm
                // is
                // 1byte tag + 1byte len
                // Get TAG
                final byte byteTag = headerBytes[idx++];
                final String hexStrTag = Converter.convertSignedByteToHexString(new byte[]{byteTag});
                final HeaderTag tag = HeaderTag.getHeaderTag(Converter.convertSignedByteToUnsignedByte(byteTag));
                if (null == tag) {
                    throw new DcloudInvalidDataException("INVALID header; invalid tag=[" + hexStrTag + "]");
                }

                // Get length
                final byte byteLength = headerBytes[idx++];
                final String hexStrLength = Converter.convertSignedByteToHexString(new byte[]{byteLength});
                final int length = Converter.convertSignedByteToUnsignedByte(byteLength);

                // Get value
                if ((idx + length) > headerBytes.length) {
                    throw new DcloudInvalidDataException("INVALID header; length of value for tag=[" + tag
                            + "] exceeds headerBytes=[" + Converter.convertSignedByteToHexString(headerBytes) + "]");
                }
                final byte[] value = Arrays.copyOfRange(headerBytes, idx, idx += length);

                // LOG
                if (LOG.isTraceEnabled()) {
                    LOG.trace("hexStrTag=[" + hexStrTag + "], tag=[" + tag + "], hexStrLength=[" + hexStrLength
                            + "], hexStrValue=[" + Converter.convertSignedByteToHexString(value) + "]");
                }

                switch (tag) {
                    case TAG_DCLOUD_VERSION: {
                        if (!Arrays.equals(DCLOUD_VERSION, value)) {
                            throw new DcloudInvalidDataException(
                                    "INVALID header, actual version=[" + Converter.convertSignedByteToHexString(value)
                                            + "] is different from expected version=["
                                            + Converter.convertSignedByteToHexString(DCLOUD_VERSION) + "]");
                        }
                        break;
                    }
                    case TAG_V_SECRET_SHARE: {
                        header.setVSecretShare(value);
                        break;
                    }
                    case TAG_DISPERSAL_IDX: {
                        final int intDispersalIdx = Integer.parseInt(Converter.convertSignedByteToHexString(value), 16);
                        header.setDispersalIdx(intDispersalIdx);
                        break;
                    }
                    case TAG_THRESHOLD: {
                        final int intThreshold = Integer.parseInt(Converter.convertSignedByteToHexString(value), 16);
                        header.setThreshold(intThreshold);
                        break;
                    }
                    case TAG_PADDING_LEN: {
                        final int paddLen = Integer.parseInt(Converter.convertSignedByteToHexString(value), 16);
                        header.setPaddLen(paddLen);
                        break;
                    }
                    case TAG_MD5: {
                        header.setMd5(value);
                        break;
                    }
                    default:
                        if (LOG.isEnabledFor(Level.WARN)) {
                            LOG.warn("NO interpretation for tag=[" + tag + "]");
                        }
                }
            }

            if (idx != headerBytes.length) {
                throw new DcloudInvalidDataException("INVALID header, not following TLV format");
            }
        }

        try {
            header.validate();
        } catch (DcloudInvalidDataRuntimeException e) {
            throw new DcloudInvalidDataException(e.getMessage(), e);
        }

        return header;
    }

    public byte[] getVSecretShare() {
        return this.vSecretShare;
    }

    public void setVSecretShare(final byte[] vSecretShare) {
        this.vSecretShare = vSecretShare;
        this.validateVSecretShare();
    }

    public int getDispersalIdx() {
        return this.dispersalIdx;
    }

    public void setDispersalIdx(final int dispersalIdx) {
        this.dispersalIdx = dispersalIdx;
        this.validateDispersalIdx();
    }

    public int getThreshold() {
        return this.m;
    }

    public void setThreshold(final int threshold) {
        this.m = threshold;
        this.validateThreshold();
    }

    public int getPaddLen() {
        return this.paddLen;
    }

    public void setPaddLen(final int paddLen) {
        this.paddLen = paddLen;
        this.validatePaddLen();
    }

    public byte[] getMd5() {
        return this.md5;
    }

    public void setMd5(final byte[] md5) {
        this.md5 = md5;
    }

    public byte[] generateHeader() throws DcloudInvalidDataException, DcloudSystemInternalException {

        try {
            this.validate();
        } catch (DcloudInvalidDataRuntimeException e) {
            throw new DcloudInvalidDataException(e.getMessage(), e);
        }

        byte[] header = null;
        ByteArrayOutputStream baos1 = null;
        try {

            baos1 = new ByteArrayOutputStream();

            // 4 bytes dcloud version TLV
            baos1.write(HeaderTag.TAG_DCLOUD_VERSION.getTag());
            baos1.write(DCLOUD_VERSION.length);
            baos1.write(DCLOUD_VERSION);

            // max 257 bytes IDA vSecretKey TLV
            baos1.write(HeaderTag.TAG_V_SECRET_SHARE.getTag());
            baos1.write(this.vSecretShare.length);
            baos1.write(this.vSecretShare);

            // 3 bytes dispersal IDA index TLV
            baos1.write(HeaderTag.TAG_DISPERSAL_IDX.getTag());
            baos1.write((byte) 0x01);
            baos1.write(this.dispersalIdx);

            // 3 bytes IDA threshold
            baos1.write(HeaderTag.TAG_THRESHOLD.getTag());
            baos1.write((byte) 0x01);
            baos1.write(this.m);

            // 3 bytes padding len
            baos1.write(HeaderTag.TAG_PADDING_LEN.getTag());
            baos1.write((byte) 0x01);
            baos1.write(this.paddLen);

            // 18 bytes md5
            baos1.write(HeaderTag.TAG_MD5.getTag());
            baos1.write(this.md5.length);
            baos1.write(md5);

            header = baos1.toByteArray();

        } catch (IOException e) {
            throw new DcloudSystemInternalException(e.getMessage(), e);
        } finally {

            if (null != baos1) {
                try {
                    baos1.close();
                } catch (IOException e) {
                    if (LOG.isEnabledFor(Level.WARN)) {
                        LOG.warn(e.getMessage());
                    }
                }
            }

        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("header=[" + Converter.convertSignedByteToHexString(header) + "]");
        }
        return header;
    }

    public void validate() {
        this.validateVSecretShare();
        this.validateDispersalIdx();
        this.validateThreshold();
        this.validatePaddLen();
        this.validateMd5();
    }

    private void validateVSecretShare() {
        if (null == this.vSecretShare) {
            throw new DcloudInvalidDataRuntimeException("INVALID header, null vSecretShare");
        }
    }

    private void validateDispersalIdx() {
        if (this.dispersalIdx < TAG_DISPERSAL_IDX_VALUE_MIN || this.dispersalIdx > TAG_DISPERSAL_IDX_VALUE_MAX) {
            throw new DcloudInvalidDataRuntimeException(
                    "INVALID header, dispersalIdx=[" + this.dispersalIdx + "] out of range "
                            + TAG_DISPERSAL_IDX_VALUE_MIN + " <= dispersalIdx <= " + TAG_DISPERSAL_IDX_VALUE_MAX);
        }
    }

    private void validateThreshold() {
        if (this.m < TAG_THRESHOLD_VALUE_MIN || this.m > TAG_THRESHOLD_VALUE_MAX) {
            throw new DcloudInvalidDataRuntimeException("INVALID header, threshold=[" + this.m + "] out of range "
                    + TAG_THRESHOLD_VALUE_MIN + " <= threshold <= " + TAG_THRESHOLD_VALUE_MAX);
        }
    }

    private void validatePaddLen() {
        if (this.paddLen < TAG_PADD_LEN_VALUE_MIN || this.paddLen > TAG_PADD_LEN_VALUE_MAX) {
            throw new DcloudInvalidDataRuntimeException("INVALID header, paddLen=[" + this.paddLen + "] out of range "
                    + TAG_PADD_LEN_VALUE_MIN + " <= paddLen <= " + TAG_PADD_LEN_VALUE_MAX);
        }
    }

    private void validateMd5() {
        if (null == this.md5) {
            throw new DcloudInvalidDataRuntimeException("INVALID header, null md5");
        }
        if (md5.length != TAG_MD5_LEN_VALUE) {
            throw new DcloudInvalidDataRuntimeException("INVALID header, md5 length is not " + TAG_MD5_LEN_VALUE);
        }
    }

    @Override
    public Object clone() {
        final DcloudHeader clone = new DcloudHeader();
        clone.setVSecretShare(this.vSecretShare.clone());
        clone.setDispersalIdx(this.dispersalIdx);
        clone.setThreshold(this.m);
        clone.setPaddLen(this.paddLen);
        clone.setMd5(this.md5.clone());
        return clone;
    }

    @Override
    public boolean equals(final Object o) {

        if (null == o) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        DcloudHeader other = (DcloudHeader) o;

        return Arrays.equals(this.vSecretShare, other.getVSecretShare()) && this.dispersalIdx == other.getDispersalIdx()
                && this.m == other.getThreshold() && this.paddLen == other.getPaddLen()
                && Arrays.equals(this.md5, other.getMd5());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(DCLOUD_VERSION, this.vSecretShare, this.dispersalIdx, this.m, this.paddLen, this.md5);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("version", Converter.convertSignedByteToHexString(DCLOUD_VERSION))
                .add("vSecretKeyDist", Converter.convertSignedByteToHexString(this.vSecretShare))
                .add("dispersalIdx", this.dispersalIdx).add("threshold", this.m).add("paddLen", this.paddLen)
                .add("md5", Converter.convertSignedByteToHexString(this.md5)).toString();
    }

    private enum HeaderTag {

        TAG_DCLOUD_VERSION(0x20), TAG_V_SECRET_SHARE(0x21), TAG_DISPERSAL_IDX(0x22), TAG_THRESHOLD(
                0x23), TAG_PADDING_LEN(0x24), TAG_MD5(0x25);

        private static final Map<Integer, HeaderTag> HEADER_TAG_MAP = new HashMap<Integer, DcloudHeader.HeaderTag>();

        static {
            for (HeaderTag headerTag : HeaderTag.values()) {
                HEADER_TAG_MAP.put(Integer.valueOf(headerTag.getTag()), headerTag);
            }
        }

        private int tag;

        private HeaderTag(final int tag) {
            this.tag = tag;
        }

        public static HeaderTag getHeaderTag(final int tag) {
            return HEADER_TAG_MAP.get(tag);
        }

        public int getTag() {
            return this.tag;
        }

    }

    // public static void main(String[] args) throws Exception {
    //
    // System.out.println(Converter.convertSignedByteToHexString("dcloud".getBytes("UTF-8")));
    //
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // baos.write(129);
    // baos.write(254);
    // baos.write(255);
    // baos.write(256);
    // baos.write(257);
    // System.out.println(Arrays.toString(baos.toByteArray()));
    // System.out.println((byte) -1);
    // System.out.println((byte) 0);
    // System.out.println((byte) 1);
    // System.out.println((byte) 126);
    // System.out.println((byte) 127);
    // System.out.println((byte) 128);
    // System.out.println((byte) 129);
    // System.out.println((byte) 254);
    // System.out.println((byte) 255);
    // System.out.println((byte) 256);
    // System.out.println((byte) 257);
    // System.out.println(Integer.toString(126, 16));
    // System.out.println(Integer.toString(127, 16));
    // System.out.println(Integer.toString(128, 16));
    // System.out.println(Integer.toString(255, 16));
    // System.out.println(Integer.toString(256, 16));
    // System.out.println(Integer.toString(257, 16));
    // }

}
