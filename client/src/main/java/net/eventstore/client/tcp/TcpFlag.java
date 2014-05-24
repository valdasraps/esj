package net.eventstore.client.tcp;

import lombok.Getter;
import net.eventstore.client.util.Bytes;

/**
 * TcpFlag
 * @author Stasys
 */
public enum TcpFlag {

    None(0x00),
    Authenticated(0x01);
    
    @Getter
    private final byte mask;
    
    private TcpFlag(int mask) {
        this.mask = (byte) mask;
    }
    
    public static TcpFlag valueOf(byte mask) {
        for (TcpFlag f: TcpFlag.values()) {
            if (f.getMask() == mask) {
                return f;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown mask: %d (%s)", Bytes.toInt(mask), Bytes.toBinaryString(mask)));
    }
    
}