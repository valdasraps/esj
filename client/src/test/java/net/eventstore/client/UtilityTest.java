package net.eventstore.client;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import net.eventstore.client.tcp.TcpCommand;
import net.eventstore.client.util.Bytes;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UtilityTest {

    private static final Logger log = Logger.getLogger(UtilityTest.class);
    
    @Test
    public void bitCheck() {
        for (TcpCommand c : TcpCommand.values()) {
            //log.info(String.format("%s: mask (byte) = %d (%s)", c, c.getMask(), Bytes.toBinaryString(c.getMask())));
        }
    }

    @Test
    public void uuidCheck() {
        UUID id = UUID.randomUUID();
        byte[] bytes = Bytes.toBytes(id);
        assertEquals(16, bytes.length);
        UUID id2 = Bytes.fromBytes(bytes);
        assertEquals(id, id2);
    }

}
