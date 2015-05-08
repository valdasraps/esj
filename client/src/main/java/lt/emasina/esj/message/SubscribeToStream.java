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
public class SubscribeToStream extends Message {

    private final String streamId;
    private static boolean resolveLinkTos = false;

    public SubscribeToStream(String streamId) {
        this(streamId, null);
    }

    public SubscribeToStream(String streamId, UserCredentials user) {
        super(TcpCommand.SubscribeToStream, user);
        this.streamId = streamId;
    }

    @Override
    public GeneratedMessage getDto(Settings settings) {
        ClientMessageDtos.SubscribeToStream.Builder web = ClientMessageDtos.SubscribeToStream.newBuilder();
        web.setEventStreamId(streamId);
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
     * @return the resolveLinkTos
     */
    public static boolean isResolveLinkTos() {
        return resolveLinkTos;
    }

}
