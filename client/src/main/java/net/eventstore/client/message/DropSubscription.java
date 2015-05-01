package net.eventstore.client.message;

import java.util.UUID;

import lombok.Getter;
import net.eventstore.client.Settings;
import net.eventstore.client.model.Message;
import net.eventstore.client.model.UserCredentials;
import net.eventstore.client.tcp.TcpCommand;

import org.apache.log4j.Logger;

import com.google.protobuf.GeneratedMessage;

/**
 * WriteEvents
 *
 * @author Stasys
 */
@Getter
public class DropSubscription extends Message {

    private static final Logger log = Logger.getLogger(DropSubscription.class);

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
