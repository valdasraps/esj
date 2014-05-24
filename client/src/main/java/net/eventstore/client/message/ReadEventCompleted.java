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
public class ReadEventCompleted extends Message {

    protected ClientMessageDtos.ReadEventCompleted.ReadEventResult result;
    private ByteString responseData;
    private String eventType;

    public ReadEventCompleted() {
        super(TcpCommand.ReadEventCompleted);
    }

    @Override
    public void parse(byte[] data) throws ParseException {
        try {
            ClientMessageDtos.ReadEventCompleted dto = ClientMessageDtos.ReadEventCompleted.parseFrom(data);
            this.result = dto.getResult();
            responseData = dto.getEvent().getEvent().getData();
            eventType = dto.getEvent().getEvent().getEventType();
        } catch (InvalidProtocolBufferException ex) {
            throw new ParseException(ex);
        }
    }

    @Override
    protected String toResultInfo() {
        return String.format("Received (%s). Data = %s. Event Type = %s.",
                this.getResult(),
                responseData != null ? responseData.toStringUtf8() : "no response data",
                eventType != null ? eventType : "no event type");
    }

}
