package net.eventstore.client.model;

import com.google.protobuf.ByteString;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.eventstore.client.message.ClientMessageDtos;
import net.eventstore.client.util.Bytes;

import org.json.JSONObject;

/**
 * Event
 * @author Stasys
 * @param <D>
 * @param <M>
 */
@Setter @Getter
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
    
}
