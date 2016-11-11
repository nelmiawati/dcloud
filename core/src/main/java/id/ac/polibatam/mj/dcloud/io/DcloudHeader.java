package id.ac.polibatam.mj.dcloud.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import id.ac.polibatam.mj.dcloud.util.Converter;

public class DcloudHeader implements Serializable, Cloneable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4262627594626902686L;

	private static final Logger LOG = Logger.getLogger(DcloudHeader.class);

	private static final byte[] DCLOUD_INDICATOR = { (byte) 0x64, (byte) 0x63, (byte) 0x6C, (byte) 0x6F, (byte) 0x75,
			(byte) 0x64 };

	public static final int HEADER_MAX_LEN = 270;

	private static final byte[] DCLOUD_VERSION = { (byte) 0x01, (byte) 0x00 };

	private static final int TAG_DISPERSAL_IDX_VALUE_MIN = 0;

	private static final int TAG_DISPERSAL_IDX_VALUE_MAX = 255;

	private static final int TAG_THRESHOLD_VALUE_MIN = 1;

	private static final int TAG_THRESHOLD_VALUE_MAX = 254;

	private static final int TAG_PADD_LEN_VALUE_MIN = 0;

	private static final int TAG_PADD_LEN_VALUE_MAX = 255;

	private enum HeaderTag {

		TAG_DCLOUD_VERSION(0x20), TAG_V_SECRET_KEY(0x21), TAG_DISPERSAL_IDX(0x22), TAG_THRESHOLD(0x23), TAG_PADDING_LEN(
				0x24);

		private int tag;

		private static final Map<Integer, HeaderTag> HEADER_TAG_MAP = new HashMap<Integer, DcloudHeader.HeaderTag>();
		static {
			for (HeaderTag headerTag : HeaderTag.values()) {
				HEADER_TAG_MAP.put(Integer.valueOf(headerTag.getTag()), headerTag);
			}
		}

		private HeaderTag(final int tag) {
			this.tag = tag;
		}

		public int getTag() {
			return this.tag;
		}

		public static HeaderTag getHeaderTag(final int tag) {
			return HEADER_TAG_MAP.get(tag);
		}

	}

	private byte[] vSecretKeyDist = null;

	private int dispersalIdx = 0;

	private int m = 0;

	private int paddLen = 0;

	public DcloudHeader() {
	}

	public static byte[] getIndicator() {
		return DCLOUD_INDICATOR.clone();
	}

	public static byte[] getVersion() {
		return DCLOUD_VERSION.clone();
	}

	public byte[] getVSecretKeyDist() {
		return this.vSecretKeyDist;
	}

	public void setVSecretKeyDist(final byte[] vSecretKeyDist) {
		this.vSecretKeyDist = vSecretKeyDist;
		this.validateVSecretKeyDist();
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

	// public static DcloudHeader parseHeader(final byte[] header)
	// throws DcloudInvalidDataException, DcloudSystemInternalException {
	//
	// if (null == header) {
	// throw new DcloudInvalidDataException("INVALID header, null bytes");
	// }
	//
	// byte[] headerElm = null;
	// try {
	//
	// // Check header indicator
	// final byte[] dcloudIndicatorByte = DCLOUD_INDICATOR.getBytes("UTF-8");
	// if (!Arrays.equals(dcloudIndicatorByte, Arrays.copyOf(header,
	// dcloudIndicatorByte.length))) {
	// throw new DcloudInvalidDataException(
	// "INVALID header, bytes not started with " +
	// Arrays.toString(dcloudIndicatorByte));
	// }
	//
	// // Get header length, next 2bytes after header indicator
	// final byte[] headerLenByte = Arrays.copyOfRange(header,
	// dcloudIndicatorByte.length, 2);
	// final int headerLen =
	// Integer.parseInt(Converter.convertSignedByteToHexString(headerLenByte),
	// 16);
	// if (header.length != (dcloudIndicatorByte.length + headerLenByte.length +
	// headerLen)) {
	// throw new DcloudInvalidDataException("INVALID header, unmatch header
	// len=["
	// + Converter.convertSignedByteToHexString(headerLenByte) + "]");
	// }
	//
	// // Get header elements
	// headerElm = Arrays.copyOf(header, dcloudIndicatorByte.length + 2);
	//
	// } catch (UnsupportedEncodingException e) {
	// LOG.fatal(e.getMessage());
	// throw new DcloudSystemInternalException(e.getMessage(), e);
	// }
	//
	// LOG.trace("header=[" + Converter.convertSignedByteToHexString(header) +
	// "]");
	// LOG.trace("headerElm=[" +
	// Converter.convertSignedByteToHexString(headerElm) + "]");
	//
	// return DcloudHeader.parseHeaderElm(headerElm);
	// }

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
				final String hexStrTag = Converter.convertSignedByteToHexString(new byte[] { byteTag });
				final HeaderTag tag = HeaderTag.getHeaderTag(Converter.convertSignedByteToUnsignedByte(byteTag));
				if (null == tag) {
					throw new DcloudInvalidDataException("INVALID header; invalid tag=[" + hexStrTag + "]");
				}

				// Get length
				final byte byteLength = headerBytes[idx++];
				final String hexStrLength = Converter.convertSignedByteToHexString(new byte[] { byteLength });
				final int length = Converter.convertSignedByteToUnsignedByte(byteLength);

				// Get value
				if ((idx + length) > headerBytes.length) {
					throw new DcloudInvalidDataException("INVALID header; length of value for tag=[" + tag
							+ "] exceeds headerBytes=[" + Converter.convertSignedByteToHexString(headerBytes) + "]");
				}
				final byte[] value = Arrays.copyOfRange(headerBytes, idx, idx += length);

				// LOG
				LOG.trace("hexStrTag=[" + hexStrTag + "], tag=[" + tag + "], hexStrLength=[" + hexStrLength
						+ "], hexStrValue=[" + Converter.convertSignedByteToHexString(value) + "]");

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
				case TAG_V_SECRET_KEY: {
					header.setVSecretKeyDist(value);
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
				default:
					LOG.warn("NO interpretation for tag=[" + tag + "]");
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
			baos1.write(HeaderTag.TAG_V_SECRET_KEY.getTag());
			baos1.write(this.vSecretKeyDist.length);
			baos1.write(this.vSecretKeyDist);

			// 3 bytes dispersal IDA index TLV
			baos1.write(HeaderTag.TAG_DISPERSAL_IDX.getTag());
			baos1.write(1);
			baos1.write(this.dispersalIdx);

			// 3 bytes IDA threshold
			baos1.write(HeaderTag.TAG_THRESHOLD.getTag());
			baos1.write(1);
			baos1.write(this.m);

			// 3 bytes padding len
			baos1.write(HeaderTag.TAG_PADDING_LEN.getTag());
			baos1.write(1);
			baos1.write(this.paddLen);

			header = baos1.toByteArray();
			// baos1.flush();
			// baos1.reset();
			// String headerElmLength = Integer.toString(headerElm.length, 16);
			// while (headerElmLength.length() < 4) {
			// headerElmLength = "0".concat(headerElmLength);
			// }
			//
			// // dcoud indicator
			// baos1.write(DCLOUD_INDICATOR.getBytes("UTF-8"));
			//
			// // 2 bytes headerElm len
			// baos1.write(Converter.convertHexStringToSignedByte(headerElmLength));
			//
			// // header Elm
			// baos1.write(headerElm);
			//
			// header = baos1.toByteArray();
			//
			// } catch (UnsupportedEncodingException e) {
			// LOG.fatal(e.getMessage());
			// throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (IOException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} finally {

			if (null != baos1) {
				try {
					baos1.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage());
				}
			}

		}

		LOG.trace("header=[" + Converter.convertSignedByteToHexString(header) + "]");
		return header;
	}

	public void validate() {
		this.validateVSecretKeyDist();
		this.validateDispersalIdx();
		this.validateThreshold();
		this.validatePaddLen();
	}

	private void validateVSecretKeyDist() {
		if (null == this.vSecretKeyDist) {
			throw new DcloudInvalidDataRuntimeException("INVALID header, null vSecretKeyDist");
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

	@Override
	public Object clone() {
		final DcloudHeader clone = new DcloudHeader();
		clone.setVSecretKeyDist(this.vSecretKeyDist.clone());
		clone.setDispersalIdx(this.dispersalIdx);
		clone.setThreshold(this.m);
		clone.setPaddLen(this.paddLen);
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

		return Arrays.equals(this.vSecretKeyDist, other.getVSecretKeyDist())
				&& this.dispersalIdx == other.getDispersalIdx() && this.m == other.getThreshold()
				&& this.paddLen == other.getPaddLen();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(DCLOUD_VERSION, this.vSecretKeyDist, this.dispersalIdx, this.m, this.paddLen);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("version", Converter.convertSignedByteToHexString(DCLOUD_VERSION))
				.add("vSecretKeyDist", Converter.convertSignedByteToHexString(this.vSecretKeyDist))
				.add("dispersalIdx", this.dispersalIdx).add("threshold", this.m).add("paddLen", this.paddLen)
				.toString();
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
