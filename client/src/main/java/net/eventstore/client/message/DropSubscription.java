package net.eventstore.client.message;

import com.google.protobuf.GeneratedMessage;
import java.util.UUID;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import net.eventstore.client.Settings;
import net.eventstore.client.model.Message;
import net.eventstore.client.model.UserCredentials;
import net.eventstore.client.tcp.TcpCommand;

/**
 * WriteEvents
 *
 * @author Stasys
 */
@Log4j
@Getter
public class DropSubscription extends Message {

    public DropSubscription(UUID correlationId) {
        this(correlationId, null);
    }

    public DropSubscription(UUID correlationId, UserCredentials user) {
        super(TcpCommand.UnsubscribeFromStream, user);
        this.setCorrelationId(correlationId);
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.UnsubscribeFromStream.Builder web = ClientMessageDtos.UnsubscribeFromStream.newBuilder();

        return web.build();
    }

}
