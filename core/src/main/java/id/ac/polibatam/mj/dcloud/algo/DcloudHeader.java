package id.ac.polibatam.mj.dcloud.algo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import id.ac.polibatam.mj.dcloud.exception.DcloudInvalidDataException;
import id.ac.polibatam.mj.dcloud.exception.DcloudSystemInternalException;
import id.ac.polibatam.mj.dcloud.util.Converter;

public class DcloudHeader {

	private static final Logger LOG = Logger.getLogger(DcloudHeader.class);

	public static final String DCLOUD_INDICATOR = "dcloud";

	private static final byte[] DCLOUD_VERSION = { 0x01, 0x00 };

	private static int TAG_DISPERSAL_IDX_VALUE_MAX = 255;

	private static int TAG_THRESHOLD_VALUE_MAX = 255;

	private enum HeaderTag {

		TAG_DCLOUD_VERSION(0x20), TAG_V_SECRET_KEY(0x21), TAG_DISPERSAL_IDX(0x22), TAG_THRESHOLD(0x23),;

		private int tag;

		private static Map<Integer, HeaderTag> HEADER_TAG_MAP;
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

	private byte[] vSecretKey = null;

	private int dispersalIdx = 0;

	private int m = 0;

	public DcloudHeader() {

	}

	public byte[] getVSecretKey() {
		return this.vSecretKey;
	}

	public void setVSecretKey(final byte[] vSecretKey) {
		this.vSecretKey = vSecretKey;
	}

	public int getDispersalIdx() {
		return this.dispersalIdx;
	}

	public void setDispersalIdx(final int dispersalIdx) throws DcloudInvalidDataException {
		if (dispersalIdx > TAG_DISPERSAL_IDX_VALUE_MAX) {
			throw new DcloudInvalidDataException("INVALID header, dispersalIdx=[] exceeds maximum accepted value=["
					+ TAG_DISPERSAL_IDX_VALUE_MAX + "]");
		}
		this.dispersalIdx = dispersalIdx;
	}

	public int getThreshold() throws DcloudInvalidDataException {
		if (dispersalIdx > TAG_THRESHOLD_VALUE_MAX) {
			throw new DcloudInvalidDataException(
					"INVALID header, threshold=[] exceeds maximum accepted value=[" + TAG_THRESHOLD_VALUE_MAX + "]");
		}
		return this.m;
	}

	public void setThreshold(final int m) {
		this.m = m;
	}

	public static DcloudHeader parseHeader(final byte[] header)
			throws DcloudInvalidDataException, DcloudSystemInternalException {

		if (null == header) {
			throw new DcloudInvalidDataException("INVALID null header");
		}

		byte[] headerElm = null;
		try {
			final byte[] dcloudIndicator = DCLOUD_INDICATOR.getBytes("UTF-8");
			if (!Arrays.equals(dcloudIndicator, Arrays.copyOf(header, dcloudIndicator.length))) {
				throw new DcloudInvalidDataException(
						"INVALID header not started with bytes=" + Arrays.toString(dcloudIndicator));
			}
		} catch (UnsupportedEncodingException e) {
			LOG.fatal(e.getMessage());
			throw new DcloudSystemInternalException(e.getMessage(), e);
		}
		return DcloudHeader.parseHeaderElm(headerElm);
	}

	public static DcloudHeader parseHeaderElm(final byte[] headerElm) throws DcloudInvalidDataException {

		final DcloudHeader header = new DcloudHeader();

		if (null != headerElm) {
			int idx = 0;
			while ((idx + 1) < headerElm.length) {

				final byte byteTag = headerElm[idx++];
				final String hexStrTag = Converter.convertSignedByteToHexString(new byte[] { byteTag });
				final int intTag = Converter.convertSignedByteToUnsignedByte(byteTag);
				final HeaderTag tag = HeaderTag.getHeaderTag(intTag);
				if (null == tag) {
					throw new DcloudInvalidDataException("INVALID header; invalid tag=[" + hexStrTag + "]");
				}

				final byte length = headerElm[idx++];

				final byte[] value = new byte[length];
				if ((idx + length) < headerElm.length) {
					System.arraycopy(headerElm, idx, value, 0, value.length);
				} else {
					throw new DcloudInvalidDataException("INVALID header; length of value for intTag=[" + tag
							+ "] exceeds headerElm=[" + Arrays.toString(headerElm) + "]");
				}

				LOG.debug("signedByteTag=[" + byteTag + "], hexStrTag=[" + hexStrTag + "], tag=[" + tag + "], length=["
						+ length + "], value=[" + Arrays.toString(value) + "]");

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
					header.setVSecretKey(value);
					break;
				}
				case TAG_DISPERSAL_IDX: {
					final int intDispersalIdx = Integer.parseInt(Converter.convertSignedByteToHexString(value), 16);
					if (intDispersalIdx > TAG_DISPERSAL_IDX_VALUE_MAX) {
						throw new DcloudInvalidDataException(
								"INVALID header, dispersalIdx=[] exceeds maximum accepted value=["
										+ TAG_DISPERSAL_IDX_VALUE_MAX + "]");
					} else {
						header.setDispersalIdx(intDispersalIdx);
					}
					break;
				}
				case TAG_THRESHOLD: {
					final int intThreshold = Integer.parseInt(Converter.convertSignedByteToHexString(value), 16);
					if (intThreshold > TAG_THRESHOLD_VALUE_MAX) {
						throw new DcloudInvalidDataException(
								"INVALID header, threshold=[] exceeds maximum accepted value=["
										+ TAG_THRESHOLD_VALUE_MAX + "]");
					} else {
						header.setThreshold(intThreshold);
					}
					break;
				}
				default:
					LOG.warn("NO interpretation for tag=[" + tag + "]");
				}
			}
		}

		return header;
	}

	public byte[] generateHeader() throws DcloudSystemInternalException {

		byte[] header = null;
		ByteArrayOutputStream baos1 = null;
		try {

			baos1 = new ByteArrayOutputStream();

			// 4 bytes dcloud version TLV
			baos1.write(HeaderTag.TAG_DCLOUD_VERSION.getTag());
			baos1.write((byte) DCLOUD_VERSION.length);
			baos1.write(DCLOUD_VERSION);

			// max 257 bytes IDA vSecretKey TLV
			if (null != this.vSecretKey) {
				baos1.write(HeaderTag.TAG_V_SECRET_KEY.getTag());
				baos1.write((byte) this.vSecretKey.length);
				baos1.write(this.vSecretKey);
			}

			// 3 bytes dispersal IDA index TLV
			if (this.dispersalIdx != 0) {
				baos1.write(HeaderTag.TAG_DISPERSAL_IDX.getTag());
				baos1.write(0x01);
				baos1.write(this.dispersalIdx);
			}

			// 3 bytes IDA threshold
			if (this.m != 0) {
				baos1.write(HeaderTag.TAG_THRESHOLD.getTag());
				baos1.write(0x01);
				baos1.write(this.m);
			}

			final byte[] headerElm = baos1.toByteArray();
			baos1.flush();
			baos1.reset();
			String headerElmLength = Integer.toString(headerElm.length, 16);
			while (headerElmLength.length() < 4) {
				headerElmLength = "0".concat(headerElmLength);
			}

			baos1.write(DCLOUD_INDICATOR.getBytes("UTF-8"));
			baos1.write(Converter.convertHexStringToSignedByte(headerElmLength));
			baos1.write(headerElm);
			header = baos1.toByteArray();

		} catch (UnsupportedEncodingException e) {
			LOG.fatal(e.getMessage());
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} catch (IOException e) {
			throw new DcloudSystemInternalException(e.getMessage(), e);
		} finally {

			if (null != baos1) {
				try {
					baos1.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage(), e);
				}
			}

		}

		return header;
	}

	public static void main(String[] args) {
		System.out.println(Integer.toString(126, 16));
		System.out.println(Integer.toString(127, 16));
		System.out.println(Integer.toString(128, 16));
		System.out.println(Integer.toString(255, 16));
		System.out.println(Integer.toString(256, 16));
		System.out.println(Integer.toString(257, 16));
	}

}
