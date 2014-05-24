package net.eventstore.client.model;

import com.google.protobuf.GeneratedMessage;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.eventstore.client.Settings;
import net.eventstore.client.tcp.TcpCommand;
import net.eventstore.client.tcp.TcpFlag;
import net.eventstore.client.tcp.TcpPackage;

/**
 * Message
 *
 * @author Stasys
 */
@Getter
@Setter
@RequiredArgsConstructor
public abstract class Message {

    // General properties
    protected final TcpCommand command;
    protected UUID correlationId;

    // Request properties
    protected UserCredentials user;

    // Response properties
    protected String message;

    public Message(TcpCommand command, UserCredentials user) {
        this.command = command;
        this.correlationId = UUID.randomUUID();
        this.user = user;
    }

    public GeneratedMessage getDto(Settings settings) {
        return null;
    }

    public void parse(byte[] data) throws ParseException {

    }

    public final TcpPackage getTcpPackage(Settings settings) {
        TcpFlag flag = (user == null ? TcpFlag.None : TcpFlag.Authenticated);
        GeneratedMessage dto = getDto(settings);
        return new TcpPackage(command, flag, correlationId, user, dto == null ? new byte[]{} : dto.toByteArray());
    }

    public final void parse(TcpPackage pckg) throws ParseException {
        parse(null, pckg);
    }

    public final void parse(Message request, TcpPackage pckg) throws ParseException {

        if (!command.equals(pckg.getCommand())) {
            throw new ParseException("Command does not match: expected %s got %s", command, pckg.getCommand());
        }

        if (request != null && !request.getCorrelationId().equals(pckg.getCorrelationId())) {
            throw new ParseException("Correlation ID does not match: expected %s got %s", request.getCorrelationId(), pckg.getCorrelationId());
        } else {
            this.correlationId = pckg.getCorrelationId();
        }

        this.user = pckg.getUser();
        parse(pckg.getData());
    }

    protected String toResultInfo() {
        return "Unimplemented!";
    }

}
