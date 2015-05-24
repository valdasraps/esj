package lt.emasina.esj.message;

import java.util.UUID;

import lt.emasina.esj.message.ClientMessageDtos.EventRecord;
import lt.emasina.esj.model.Message;
import lt.emasina.esj.model.ParseException;
import lt.emasina.esj.tcp.TcpCommand;
import lt.emasina.esj.util.Bytes;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * WriteEventsCompleted
 *
 * @author Stasys
 */
public class ReadEventCompleted extends Message {

    protected ClientMessageDtos.ReadEventCompleted.ReadEventResult result;

    private String streamId;
    private UUID eventId;
    private int eventNumber;
    private String eventType;
    private int responseDataType;
    private ByteString responseData;
    private int responseMetaType;
    private ByteString responseMeta;

    public ReadEventCompleted() {
        super(TcpCommand.ReadEventCompleted);
    }

    @Override
    public void parse(byte[] data) throws ParseException {
        try {
            ClientMessageDtos.ReadEventCompleted dto = ClientMessageDtos.ReadEventCompleted.parseFrom(data);
            this.result = dto.getResult();
            final EventRecord event = dto.getEvent().getEvent();
            streamId = event.getEventStreamId();
            eventId = Bytes.fromBytes(event.getEventId().toByteArray());
            eventNumber = event.getEventNumber();
            responseDataType = event.getDataContentType();
            responseData = event.getData();
            responseMetaType = event.getMetadataContentType();
            responseMeta = event.getMetadata();
            eventType = event.getEventType();
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

    /**
     * @return the streamId
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * @return the eventId
     */
    public UUID getEventId() {
        return eventId;
    }

    /**
     * @return the eventNumber
     */
    public int getEventNumber() {
        return eventNumber;
    }

    /**
     * @return the responseDataType
     */
    public int getResponseDataType() {
        return responseDataType;
    }

    /**
     * @return the responseMetaType
     */
    public int getResponseMetaType() {
        return responseMetaType;
    }

    /**
     * @return the responseMeta
     */
    public ByteString getResponseMeta() {
        return responseMeta;
    }

}
