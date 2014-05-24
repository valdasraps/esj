package net.eventstore.client.util;

import java.util.UUID;

/**
 * Bytes
 *
 * @author Stasys
 */
public class Bytes {

    /**
     * Appends two bytes array into one.
     *
     * @param a A byte[].
     * @param b A byte[].
     * @return A byte[].
     */
    public static byte[] append(byte[] a, byte[] b) {
        byte[] z = new byte[a.length + b.length];
        System.arraycopy(a, 0, z, 0, a.length);
        System.arraycopy(b, 0, z, a.length, b.length);
        return z;
    }

    /**
     * Returns a 8-byte array built from a long.
     *
     * @param n The number to convert.
     * @return A byte[].
     */
    public static byte[] toBytes(long n) {
        return toBytes(n, new byte[8]);
    }

    /**
     * Build a 8-byte array from a long. No check is performed on the array
     * length.
     *
     * @param n The number to convert.
     * @param b The array to fill.
     * @return A byte[].
     */
    public static byte[] toBytes(long n, byte[] b) {
        b[7] = (byte) (n);
        n >>>= 8;
        b[6] = (byte) (n);
        n >>>= 8;
        b[5] = (byte) (n);
        n >>>= 8;
        b[4] = (byte) (n);
        n >>>= 8;
        b[3] = (byte) (n);
        n >>>= 8;
        b[2] = (byte) (n);
        n >>>= 8;
        b[1] = (byte) (n);
        n >>>= 8;
        b[0] = (byte) (n);

        return b;
    }

    public static byte[] toBytes(UUID i) {
        return Bytes.append(Bytes.toBytes(i.getMostSignificantBits()), Bytes.toBytes(i.getLeastSignificantBits()));
    }

    //** Formatting and validation constants
    /**
     * Chars in a UUID String.
     */
    private static final int UUID_UNFORMATTED_LENGTH = 32;

    /**
     * Insertion points for dashes in the string format
     */
    private static final int FORMAT_POSITIONS[] = new int[]{8, 13, 18, 23};

    public static UUID fromBytes(byte[] b) {
        if (b.length != 16) {
            throw new IllegalArgumentException(String.format("Expected length 16, was %d", b.length));
        }
        StringBuilder sb = new StringBuilder(new String(Hex.encodeHex(b)));
        while (sb.length() != UUID_UNFORMATTED_LENGTH) {
            sb.insert(0, "0");
        }
        for (int fp : FORMAT_POSITIONS) {
            sb.insert(fp, '-');
        }
        return UUID.fromString(sb.toString());
    }

    public static int toInt(byte b) {
        return (int) b & 0xFF;
    }

    public static String toBinaryString(byte b) {
        return String.format("%8s", Integer.toBinaryString(toInt(b))).replace(' ', '0');
    }

    public static String debugString(byte[]  
        ... bs) {
        StringBuilder sbA = new StringBuilder();
        StringBuilder sbL = new StringBuilder();
        for (byte[] b1 : bs) {
            StringBuilder sb = new StringBuilder();
            for (byte b : b1) {
                sb.append(String.format("%02X ", b));
            }
            sbA.append("[").append(sb.toString().trim()).append("]");
            sbL.append("[").append(b1.length).append("]");
        }
        return String.format("Bytes (sizes: %s): %s", sbL.toString(), sbA.toString());
    }

}
