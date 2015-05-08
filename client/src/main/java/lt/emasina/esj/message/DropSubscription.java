package lt.emasina.esj.message;

import java.util.UUID;

import lt.emasina.esj.Settings;
import lt.emasina.esj.model.Message;
import lt.emasina.esj.model.UserCredentials;
import lt.emasina.esj.tcp.TcpCommand;

import com.google.protobuf.GeneratedMessage;

/**
 * WriteEvents
 *
 * @author Stasys
 */
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
