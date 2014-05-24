package net.eventstore.client.message;

import com.google.protobuf.GeneratedMessage;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import net.eventstore.client.Settings;
import net.eventstore.client.model.ExpectedVersion;
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
public class DeleteStream extends Message {

    private final String streamId;
    private final ExpectedVersion expectedVersion;

    public DeleteStream(String streamId) {
        this(streamId, ExpectedVersion.Any, null);
    }

    public DeleteStream(String streamId, ExpectedVersion expectedVersion, UserCredentials user) {
        super(TcpCommand.DeleteStream, user);
        this.streamId = streamId;
        this.expectedVersion = expectedVersion;
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.DeleteStream.Builder web = ClientMessageDtos.DeleteStream.newBuilder();
        web.setEventStreamId(streamId);
        web.setExpectedVersion(expectedVersion.getMask());
        web.setRequireMaster(settings.isRequireMaster());
        web.setHardDelete(false);

        return web.build();
    }

}
