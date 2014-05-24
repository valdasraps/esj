package net.eventstore.client.message;

import com.google.protobuf.GeneratedMessage;
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

}
