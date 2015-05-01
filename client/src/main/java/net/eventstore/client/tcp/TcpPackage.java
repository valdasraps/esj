package net.eventstore.client.tcp;

import java.nio.charset.Charset;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.eventstore.client.model.ParseException;
import net.eventstore.client.model.UserCredentials;
import net.eventstore.client.util.Bytes;

import org.apache.log4j.Logger;

/**
 * TcpPackage
 * @author Stasys
 */
@Getter
@RequiredArgsConstructor
public class TcpPackage {

    private static final Logger log = Logger.getLogger(TcpPackage.class);
    
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private static final int COMMAND_OFFSET = 0;
    private static final int FLAGS_OFFSET = COMMAND_OFFSET + 1;
    private static final int CORRELATION_OFFSET = FLAGS_OFFSET + 1;
    private static final int AUTH_OFFSET = CORRELATION_OFFSET + 16;
    private static final int MANDATORY_SIZE = AUTH_OFFSET;
    
    private static final int UUID_SIZE = 16;
    
    private final TcpCommand command;
    private final TcpFlag flag;
    private final UUID correlationId;
    private final UserCredentials user;
    private final byte[] data;

    public byte[] AsByteArray() {
        if (flag.equals(TcpFlag.Authenticated) && user != null) {
            int loginLen = user.getLogin().getBytes(UTF8_CHARSET).length;
            int passLen = user.getPassword().getBytes(UTF8_CHARSET).length;
            if (loginLen > 255) throw new IllegalArgumentException(String.format("Login serialized length should be less than 256 bytes (but is {0}).", loginLen));
            if (passLen > 255) throw new IllegalArgumentException(String.format("Password serialized length should be less than 256 bytes (but is {0}).", passLen));

            byte[] res = new byte[MANDATORY_SIZE + 2 + loginLen + passLen + data.length];
            res[COMMAND_OFFSET] = command.getMask();
            res[FLAGS_OFFSET] = flag.getMask();
            System.arraycopy(Bytes.toBytes(correlationId), 0, res, CORRELATION_OFFSET, UUID_SIZE);

            res[AUTH_OFFSET] = (byte) loginLen;
            System.arraycopy(user.getLogin().getBytes(UTF8_CHARSET), 0, res, AUTH_OFFSET + 1, loginLen);
            res[AUTH_OFFSET + 1 + loginLen] = (byte) passLen;
            System.arraycopy(user.getPassword().getBytes(UTF8_CHARSET), 0, res, AUTH_OFFSET + 1 + loginLen + 1, passLen);

            System.arraycopy(data, 0, res, res.length - data.length, data.length);
            return res;
        } else {
            byte[] res = new byte[MANDATORY_SIZE + data.length];
            res[COMMAND_OFFSET] = command.getMask();
            res[FLAGS_OFFSET] = flag.getMask();
            System.arraycopy(Bytes.toBytes(correlationId), 0, res, CORRELATION_OFFSET, UUID_SIZE);
            System.arraycopy(data, 0, res, res.length - data.length, data.length);
            return res;
        }
    }
    
    public static TcpPackage fromBytes(byte[] data) throws ParseException {        
        if (data.length < MANDATORY_SIZE) {
            throw new ParseException("ArraySegment too short, length: {0}", data.length);
        }

        TcpCommand command = TcpCommand.valueOf(data[COMMAND_OFFSET]);
        TcpFlag flag = TcpFlag.valueOf(data[FLAGS_OFFSET]);

        byte[] uuidBytes = new byte[UUID_SIZE];
        System.arraycopy(data, CORRELATION_OFFSET, uuidBytes, 0, UUID_SIZE);
        UUID correlationId = Bytes.fromBytes(uuidBytes);

        UserCredentials user = null;
        
        int headerSize = MANDATORY_SIZE;
        if (flag.equals(TcpFlag.Authenticated)) {
            
            int loginLen = (int) data[AUTH_OFFSET];
            if (AUTH_OFFSET + 1 + loginLen + 1 >= data.length) {
                throw new ParseException("Login length is too big, it doesn't fit into TcpPackage.");
            }
            String login = new String(data, AUTH_OFFSET + 1, loginLen);
            int passLen = (int) data[AUTH_OFFSET + 1 + loginLen];
            if (AUTH_OFFSET + 1 + loginLen + 1 + passLen > data.length) {
                throw new ParseException("Password length is too big, it doesn't fit into TcpPackage.");
            }
            String pass = new String(data, AUTH_OFFSET + 1 + loginLen + 1, passLen);
            headerSize += 1 + loginLen + 1 + passLen;
            
            user = new UserCredentials(login, pass);
            
        }

        byte [] dtoBytes = new byte[data.length - headerSize];
        System.arraycopy(data, headerSize, dtoBytes, 0, data.length - headerSize);
        return new TcpPackage(command, flag, correlationId, user, dtoBytes);
    }

}
