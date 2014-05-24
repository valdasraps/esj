package net.eventstore.client.message;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import net.eventstore.client.model.ParseException;
import net.eventstore.client.model.Message;
import net.eventstore.client.tcp.TcpCommand;

/**
 * WriteEventsCompleted
 *
 * @author Stasys
 */
@Getter
public class StreamEventAppeared extends Message {

    private ClientMessageDtos.ResolvedEvent resolvedEvent;
    private ByteString responseData;
    private String eventType;

    public StreamEventAppeared() {
        super(TcpCommand.StreamEventAppeared);
    }

    @Override
    public void parse(byte[] data) throws ParseException {
        try {
            ClientMessageDtos.StreamEventAppeared dto = ClientMessageDtos.StreamEventAppeared.parseFrom(data);
            responseData = dto.getEvent().getEvent().getData();
            eventType = dto.getEvent().getEvent().getEventType();
            //this.message = dto.getMessage();
        } catch (InvalidProtocolBufferException ex) {
            throw new ParseException(ex);
        }
    }

    protected String toResultInfo() {
        return String.format("Data = %s. Event Type = %s.",
                responseData != null ? responseData.toStringUtf8() : "no response data",
                eventType != null ? eventType : "no event type");
    }

}
