package net.eventstore.client.tcp;

import net.eventstore.client.model.ParseException;
import static net.eventstore.client.tcp.TcpConnection.HEADER_SIZE;
import net.eventstore.client.util.Bytes;

/**
 * TcpFramer class
 * @author Stasys
 */
public class TcpFramer {

    public static byte[][] frame(byte [] data) {
        int len = data.length;
        return new byte [][] {
            { (byte) len, (byte)(len >> 8), (byte)(len >> 16), (byte)(len >> 24) }, 
            data };
    }
    
    public static byte[] unframe(byte [] data) throws ParseException {
        if (data.length < HEADER_SIZE) {
            throw new ParseException("Data buffer (%d) smaller than header (%d)", data.length, HEADER_SIZE);
        }
        int rawlen = Bytes.toInt(data[0]) + Bytes.toInt(data[1]) + Bytes.toInt(data[2]) + Bytes.toInt(data[3]);
        if (data.length < HEADER_SIZE + rawlen) {
            throw new ParseException("Data buffer (%d) smaller than header (%d) + message (%d) length", data.length, HEADER_SIZE, rawlen);
        }
        byte[] raw = new byte[rawlen];
        System.arraycopy(data, HEADER_SIZE, raw, 0, rawlen);
        return raw;
    }
    
}
