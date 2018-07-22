package id.ac.polibatam.mj.dcloud.util;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;

/**
 * Converter.
 *
 * @author mia
 */
public class Converter {

    public static String convertSignedByteToHexString(byte[] signedByte) {

        if (null == signedByte) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < signedByte.length; i++) {
            int intB = Converter.convertSignedByteToUnsignedByte(signedByte[i]);
            String hexB = Integer.toHexString(intB);
            if (hexB.length() == 2) {
                sb.append(hexB);
            } else {
                sb.append("0").append(hexB);
            }
        }
        return sb.toString().toUpperCase();
    }

    public static String convertUnsignedByteToHexString(int[] unsignedByte) {
        if (null == unsignedByte) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < unsignedByte.length; i++) {
            if (unsignedByte[i] < 0 || unsignedByte[i] > 255) {
                throw new DcloudInvalidDataRuntimeException("Invalid parameter: intB[" + i + "]=[" + unsignedByte[i]
                        + "] is out of range 0 <= unsignedByte <= 255");
            }
            String hexB = Integer.toHexString(unsignedByte[i]);
            if (hexB.length() == 2) {
                sb.append(hexB);
            } else {
                sb.append("0").append(hexB);
            }
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] convertHexStringToSignedByte(String hexString) {

        if (null == hexString) {
            return null;
        }
        hexString = hexString.replaceAll("\\s", "");
        hexString = hexString.replaceAll("-", "");
        if (hexString.length() % 2 != 0) {
            throw new DcloudInvalidDataRuntimeException("Invalid parameter: hexString length is odd");
        }

        byte[] signedByte = new byte[hexString.length() / 2];
        int ptr = 0;
        for (int i = 0; i < hexString.length(); i++) {
            signedByte[ptr++] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
            i++;
        }

        return signedByte;
    }

    public static int[][] convertSignedByteToUnsignedByte(byte[][] signedByte) {

        if (null == signedByte) {
            return null;
        }

        int[][] unsignedByte = new int[signedByte.length][];
        for (int k = 0; k < signedByte.length; k++) {
            unsignedByte[k] = Converter.convertSignedByteToUnsignedByte(signedByte[k]);
        }
        return unsignedByte;
    }

    public static int[] convertSignedByteToUnsignedByte(byte[] signedByte) {

        if (null == signedByte) {
            return null;
        }

        int[] unsignedByte = new int[signedByte.length];
        for (int k = 0; k < signedByte.length; k++) {
            unsignedByte[k] = Converter.convertSignedByteToUnsignedByte(signedByte[k]);
        }
        return unsignedByte;
    }

    public static int convertSignedByteToUnsignedByte(byte signedByte) {
        return (int) signedByte & 0x000000FF;
    }

    public static byte[][] convertUnsignedByteToSignedByte(int[][] unsignedByte) {

        if (null == unsignedByte) {
            return null;
        }

        byte[][] signedByte = new byte[unsignedByte.length][];
        for (int k = 0; k < unsignedByte.length; k++) {
            signedByte[k] = Converter.convertUnsignedByteToSignedByte(unsignedByte[k]);
        }
        return signedByte;
    }

    public static byte[] convertUnsignedByteToSignedByte(int[] unsignedByte) {

        if (null == unsignedByte) {
            return null;
        }

        byte[] signedByte = new byte[unsignedByte.length];
        for (int k = 0; k < unsignedByte.length; k++) {
            signedByte[k] = Converter.convertUnsignedByteToSignedByte(unsignedByte[k]);
        }
        return signedByte;
    }

    public static byte convertUnsignedByteToSignedByte(int unsignedByte) {

        if (unsignedByte < 0 || unsignedByte > 255) {
            throw new DcloudInvalidDataRuntimeException("Invalid parameter: i > 255");
        }

        return (byte) unsignedByte;
    }

}
