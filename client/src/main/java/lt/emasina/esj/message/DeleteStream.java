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
    private final boolean hard;

    public DeleteStream(String streamId) {
        this(streamId, ExpectedVersion.Any.getMask(), false, null);
    }

    public DeleteStream(String streamId, ExpectedVersion expectedVersion, UserCredentials user) {
        this(streamId, expectedVersion.getMask(), false, user);
    }
    
    public DeleteStream(String streamId, int expectedVersion, boolean hard, UserCredentials user) {
        super(TcpCommand.DeleteStream, user);
        this.streamId = streamId;
        this.expectedVersion = expectedVersion;
        this.hard = hard;
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.DeleteStream.Builder web = ClientMessageDtos.DeleteStream.newBuilder();
        web.setEventStreamId(streamId);
        web.setExpectedVersion(expectedVersion);
        web.setRequireMaster(settings.isRequireMaster());
        web.setHardDelete(hard);

        return web.build();
    }

    /**
     * @return the expectedVersion
     */
    public int getExpectedVersion() {
        return expectedVersion;
    }

}
