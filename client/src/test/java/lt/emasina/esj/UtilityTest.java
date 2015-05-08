package lt.emasina.esj;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import lt.emasina.esj.tcp.TcpCommand;
import lt.emasina.esj.util.Bytes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UtilityTest {

    @Test
    public void bitCheck() {
        for (TcpCommand c : TcpCommand.values()) {
            //log.info("{}: mask (byte) = {} ({})", c, c.getMask(), Bytes.toBinaryString(c.getMask()));
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
