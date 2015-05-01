package net.eventstore.client.message;

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
public class ReadEvent extends Message {

    private static final Logger log = Logger.getLogger(ReadEvent.class);

    private final String streamId;
    private final int eventNumber;
    private static boolean resolveLinkTos = false;

    public ReadEvent(String streamId, int eventNumber) {
        this(streamId, eventNumber, null);
    }

    public ReadEvent(String streamId, int eventNumber, UserCredentials user) {
        super(TcpCommand.ReadEvent, user);
        this.streamId = streamId;
        this.eventNumber = eventNumber;
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.ReadEvent.Builder web = ClientMessageDtos.ReadEvent.newBuilder();
        web.setEventStreamId(streamId);
        web.setEventNumber(eventNumber);
        web.setRequireMaster(settings.isRequireMaster());
        web.setResolveLinkTos(resolveLinkTos);

        return web.build();
    }

}
