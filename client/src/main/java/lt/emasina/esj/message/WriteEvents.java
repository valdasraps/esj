package lt.emasina.esj.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    private final int expectedVersion;
    private final Collection<Event> events;

    /**
     * Constructor with array of events. No special version is expected for 
     * the stream to write and no user credentials are set.
     * 
     * @param streamId 
     *            The unique stream identifier.
     * @param user
     *            User and password.
     * @param events
     *            Array of events to write.
     */
    public WriteEvents(String streamId, Event... events) {
        this(streamId, ExpectedVersion.Any, null, events);
    }

    /**
     * Constructor with list of events. No special version is expected for 
     * the stream to write and no user credentials are set.
     * 
     * @param streamId 
     *            The unique stream identifier.
     * @param user
     *            User and password.
     * @param events
     *            Array of events to write.
     */
    public WriteEvents(String streamId, Collection<Event> events) {
        this(streamId, ExpectedVersion.Any, null, events);
    }    
    
    /**
     * Constructor with array of events.
     * 
     * @param streamId 
     *            The unique stream identifier.
     * @param expectedVersion
     *            Stream is expected to have this version.
     * @param user
     *            User and password.
     * @param events
     *            Array of events to write.
     */
    public WriteEvents(String streamId, ExpectedVersion expectedVersion, UserCredentials user, Event... events) {
        this(streamId, expectedVersion, user, Arrays.asList(events));
    }

    /**
     * Constructor with list of events.
     * 
     * @param streamId 
     *            The unique stream identifier.
     * @param expectedVersion
     *            Stream is expected to have this version.
     * @param user
     *            User and password.
     * @param events
     *            List of events to write.
     */
    public WriteEvents(String streamId, ExpectedVersion expectedVersion, UserCredentials user, Collection<Event> events) {
        this(streamId, expectedVersion.getMask(), user, events);
    }
    
    /**
     * Constructor with list of events.
     * 
     * @param streamId 
     *            The unique stream identifier.
     * @param expectedVersion
     *            Stream is expected to have this version.
     * @param user
     *            User and password.
     * @param events
     *            List of events to write.
     */
    public WriteEvents(String streamId, int expectedVersion, UserCredentials user, Collection<Event> events) {
        super(TcpCommand.WriteEvents, user);
        this.streamId = streamId;
        this.expectedVersion = expectedVersion;
        this.events = events;
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.WriteEvents.Builder web = ClientMessageDtos.WriteEvents.newBuilder();
        web.setEventStreamId(streamId);
        web.setExpectedVersion(expectedVersion);
        web.setRequireMaster(settings.isRequireMaster());

        List<ClientMessageDtos.NewEvent> newEvents = new ArrayList<>();

        for (Event e : events) {
            newEvents.add(e.getMessageEvent());
        }

        web.addAllEvents(newEvents);

        return web.build();
    }

    /**
     * Returns the unique stream identifier.
     * 
     * @return The stream name.
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * Returns the expected version.
     * 
     * @return Stream is expected to have this version.
     */
    public int getExpectedVersion() {
        return expectedVersion;
    }

    /**
     * Returns an array of events.
     * 
     * @return The event array.
     */
    public Event[] getEvents() {
        return events.toArray(new Event[events.size()]);
    }

    /**
     * Returns a list of the events.
     * 
     * @return The event list.
     */
    public Collection<Event> getEventList() {
        return events;
    }
    
}
