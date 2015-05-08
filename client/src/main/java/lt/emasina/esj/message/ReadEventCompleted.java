package lt.emasina.esj.message;

import lt.emasina.esj.model.Message;
import lt.emasina.esj.model.ParseException;
import lt.emasina.esj.tcp.TcpCommand;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * WriteEventsCompleted
 *
 * @author Stasys
 */
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

    /**
     * @return the result
     */
    public ClientMessageDtos.ReadEventCompleted.ReadEventResult getResult() {
        return result;
    }

    /**
     * @return the responseData
     */
    public ByteString getResponseData() {
        return responseData;
    }

    /**
     * @return the eventType
     */
    public String getEventType() {
        return eventType;
    }

}
