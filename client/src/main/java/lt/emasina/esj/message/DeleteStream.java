package lt.emasina.esj.message;

import lt.emasina.esj.Settings;
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
public class DeleteStream extends Message {

    private final String streamId;
    private final int expectedVersion;

    public DeleteStream(String streamId) {
        this(streamId, ExpectedVersion.Any, null);
    }

    public DeleteStream(String streamId, ExpectedVersion expectedVersion, UserCredentials user) {
        this(streamId, expectedVersion.getMask(), user);
    }
    
    public DeleteStream(String streamId, int expectedVersion, UserCredentials user) {
        super(TcpCommand.DeleteStream, user);
        this.streamId = streamId;
        this.expectedVersion = expectedVersion;
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.DeleteStream.Builder web = ClientMessageDtos.DeleteStream.newBuilder();
        web.setEventStreamId(streamId);
        web.setExpectedVersion(expectedVersion);
        web.setRequireMaster(settings.isRequireMaster());
        web.setHardDelete(false);

        return web.build();
    }

    /**
     * @return the expectedVersion
     */
    public int getExpectedVersion() {
        return expectedVersion;
    }

}
