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

    /**
     * @return the resolvedEvent
     */
    public ClientMessageDtos.ResolvedEvent getResolvedEvent() {
        return resolvedEvent;
    }

    /**
     * @param resolvedEvent the resolvedEvent to set
     */
    public void setResolvedEvent(ClientMessageDtos.ResolvedEvent resolvedEvent) {
        this.resolvedEvent = resolvedEvent;
    }

    /**
     * @return the responseData
     */
    public ByteString getResponseData() {
        return responseData;
    }

    /**
     * @param responseData the responseData to set
     */
    public void setResponseData(ByteString responseData) {
        this.responseData = responseData;
    }

    /**
     * @return the eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

}
