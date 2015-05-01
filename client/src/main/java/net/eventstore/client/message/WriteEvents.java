package net.eventstore.client.message;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.eventstore.client.Settings;
import net.eventstore.client.model.Event;
import net.eventstore.client.model.ExpectedVersion;
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
public class WriteEvents extends Message {

    private static final Logger log = Logger.getLogger(WriteEvents.class);
    
    private final String streamId;
    private final ExpectedVersion expectedVersion;
    private final Event[] events;

    public WriteEvents(String streamId, Event... events) {
        this(streamId, ExpectedVersion.Any, null, events);
    }

    public WriteEvents(String streamId, ExpectedVersion expectedVersion, UserCredentials user, Event... events) {
        super(TcpCommand.WriteEvents, user);
        this.streamId = streamId;
        this.expectedVersion = expectedVersion;
        this.events = events;
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.WriteEvents.Builder web = ClientMessageDtos.WriteEvents.newBuilder();
        web.setEventStreamId(streamId);
        web.setExpectedVersion(expectedVersion.getMask());
        web.setRequireMaster(settings.isRequireMaster());

        List<ClientMessageDtos.NewEvent> newEvents = new ArrayList<>();

        for (Event e : events) {
            newEvents.add(e.getMessageEvent());
        }

        web.addAllEvents(newEvents);

        return web.build();
    }

}
