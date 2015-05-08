package lt.emasina.esj.message;

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
public class ReadEvent extends Message {

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

    /**
     * @return the streamId
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * @return the eventNumber
     */
    public int getEventNumber() {
        return eventNumber;
    }

}
