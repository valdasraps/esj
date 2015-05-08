package lt.emasina.esj.message;

import java.util.ArrayList;
import java.util.List;

import lt.emasina.esj.Settings;
import lt.emasina.esj.model.Event;
import lt.emasina.esj.model.ExpectedVersion;
import lt.emasina.esj.model.Message;
import lt.emasina.esj.model.UserCredentials;
import lt.emasina.esj.tcp.TcpCommand;

import com.google.protobuf.GeneratedMessage;

/**
 * WriteEvents
 *
 * @author Stasys
 */
public class WriteEvents extends Message {

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

    /**
     * @return the streamId
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * @return the expectedVersion
     */
    public ExpectedVersion getExpectedVersion() {
        return expectedVersion;
    }

    /**
     * @return the events
     */
    public Event[] getEvents() {
        return events;
    }

}
