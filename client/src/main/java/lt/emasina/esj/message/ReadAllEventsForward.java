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
public class ReadAllEventsForward extends Message {

    private final int from;
    private final int maxCount;
    private final String streamId;
    private static boolean resolveLinkTos = false;

    public ReadAllEventsForward(String streamId, int from, int maxCount) {
        this(streamId, from, maxCount, new UserCredentials("admin", "admin"));
    }

    public ReadAllEventsForward(String streamId, int from, int maxCount, UserCredentials user) {
        super(TcpCommand.ReadStreamEventsForward, user);
        this.from = from;
        this.streamId = streamId;
        this.maxCount = maxCount;
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.ReadStreamEvents.Builder web = ClientMessageDtos.ReadStreamEvents.newBuilder();
        web.setEventStreamId(streamId);
        web.setFromEventNumber(from);
        web.setMaxCount(maxCount);
        web.setRequireMaster(settings.isRequireMaster());
        web.setResolveLinkTos(resolveLinkTos);

        return web.build();
    }

    /**
     * @return the from
     */
    public int getFrom() {
        return from;
    }

    /**
     * @return the maxCount
     */
    public int getMaxCount() {
        return maxCount;
    }

    /**
     * @return the streamId
     */
    public String getStreamId() {
        return streamId;
    }

}
