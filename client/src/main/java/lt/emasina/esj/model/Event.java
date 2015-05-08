package lt.emasina.esj.model;

import java.util.UUID;

import lt.emasina.esj.message.ClientMessageDtos;
import lt.emasina.esj.util.Bytes;

import org.json.JSONObject;

import com.google.protobuf.ByteString;

/**
 * Event
 * @author Stasys
 * @param <D>
 * @param <M>
 */
public class Event <D,M> {

    private static final int JSON_DATA_TYPE = 1;
    private static final int NOT_JSON_DATA_TYPE = 0;
        
    private UUID id = UUID.randomUUID();
    private String type;
    private D data = null;
    private M metadata = null;
   
    public Event(String type) {
        this.type = type;
    }
    
    public ClientMessageDtos.NewEvent getMessageEvent() {
        return ClientMessageDtos.NewEvent.newBuilder()
            	.setEventId(ByteString.copyFrom(Bytes.toBytes(id)))
		        .setEventType(type)
		        .setData(data != null ? ByteString.copyFromUtf8(data.toString()) : ByteString.EMPTY)
		        .setMetadata(metadata != null ? ByteString.copyFromUtf8(metadata.toString()) : ByteString.EMPTY)
		        .setDataContentType(data != null && JSONObject.class.isAssignableFrom(data.getClass()) ? JSON_DATA_TYPE :  NOT_JSON_DATA_TYPE)
		        .setMetadataContentType(metadata != null && JSONObject.class.isAssignableFrom(metadata.getClass()) ? JSON_DATA_TYPE :  NOT_JSON_DATA_TYPE)
                .build();

    }

    /**
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the data
     */
    public D getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(D data) {
        this.data = data;
    }

    /**
     * @return the metadata
     */
    public M getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(M metadata) {
        this.metadata = metadata;
    }
    
}
