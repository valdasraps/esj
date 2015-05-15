package lt.emasina.esj.model;

import java.util.UUID;

import lt.emasina.esj.message.ClientMessageDtos;
import lt.emasina.esj.model.converter.JsonToByteStringConverter;
import lt.emasina.esj.model.converter.ObjectToByteStringConverter;
import lt.emasina.esj.model.converter.StringToByteStringConverter;
import lt.emasina.esj.util.Bytes;

import org.json.JSONObject;

import com.google.protobuf.ByteString;

/**
 * Structure that holds an event and it's assigned meta data.
 * 
 * @author Stasys
 * @author Michael Schnell
 * 
 * @param <D>
 *            Type of the event object.
 * @param <M>
 *            Type of the meta data object.
 */
@SuppressWarnings("rawtypes")
public class Event<D, M> {

    /** Unique event identifier. */
    private UUID id;

    /** Type of the event. */
    private String type;

    /** Event object in it's object form (not serialized). */
    private D data = null;

    /** Meta data object in it's object form (not serialized) or <code>null</code>. */
    private M metadata = null;

    /** Converter that handles to-byte-array conversion for the event or <code>null</code>. */
    private ObjectToByteStringConverter dataConverter;

    /** Converter that handles to-byte-array conversion for the meta data or <code>null</code>. */
    private ObjectToByteStringConverter metaConverter;

    /**
     * Minimal constructor with type.
     * 
     * @param type
     *            Type of the event.
     */
    public Event(String type) {
        this(null, type, null, null, null, null);
    }

    /**
     * Constructor with all possible arguments.
     *
     * @param id 
     *            Unique event identifier.
     * @param type 
     *            Type of the event.
     * @param data 
     *            Event object in it's object form (not serialized).
     * @param dataConverter 
     *            Converter that handles to-byte-array conversion for the event or <code>null</code>.
     * @param metadata 
     *            Meta data object in it's object form (not serialized) or <code>null</code>.
     * @param metaConverter 
     *            Converter that handles to-byte-array conversion for the meta data or <code>null</code>.
     */
    public Event(UUID id, String type, D data,
            ObjectToByteStringConverter<D> dataConverter, M metadata,
            ObjectToByteStringConverter<M> metaConverter) {
        if (id == null) {
            this.id = UUID.randomUUID();
        } else {
            this.id = id;
        }
        this.type = type;
        this.data = data;
        this.dataConverter = dataConverter;
        this.metadata = metadata;
        this.metaConverter = metaConverter;
    }

    @SuppressWarnings("unchecked")
    public ClientMessageDtos.NewEvent getMessageEvent() {

        // Make sure the data converters are always set
        dataConverter = converter(dataConverter, data);
        metaConverter = converter(metaConverter, metadata);

        final ByteString dataBytes = dataConverter.convert(data);
        final ByteString metaBytes = metaConverter.convert(metadata);

        return ClientMessageDtos.NewEvent.newBuilder()
                .setEventId(ByteString.copyFrom(Bytes.toBytes(id)))
                .setEventType(type)
                .setData(dataBytes)
                .setMetadata(metaBytes)
                .setDataContentType(dataConverter.getContentType())
                .setMetadataContentType(metaConverter.getContentType()).build();

    }

    private static ObjectToByteStringConverter converter(ObjectToByteStringConverter converter, Object obj) {
        if (converter == null) {
            if (obj == null) {
                return new StringToByteStringConverter();
            }
            if (JSONObject.class.isAssignableFrom(obj.getClass())) {
                return new JsonToByteStringConverter();
            }
            return new StringToByteStringConverter();
        }
        return converter;
    }
    
    
    /**
     * Returns the unique event identifier.
     * 
     * @return The event ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the event identifier to a new value.
     * 
     * @param id
     *            The event ID to set.
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns the event type.
     * 
     * @return The name of the event.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the event type to a new value.
     * 
     * @param type
     *            The event name to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the event object in it's object form (not serialized).
     * 
     * @return The event object.
     */
    public D getData() {
        return data;
    }

    /**
     * Sets the event object to a new value.
     * 
     * @param data
     *            The event object in it's object form (not serialized).
     */
    public void setData(D data) {
        this.data = data;
    }

    /**
     * Returns the meta data object in it's object form (not serialized).
     * 
     * @return The meta data object.
     */
    public M getMetadata() {
        return metadata;
    }

    /**
     * Sets the meta data object to a new value.
     * 
     * @param metadata
     *            Meta data object in it's object form (not serialized).
     */
    public void setMetadata(M metadata) {
        this.metadata = metadata;
    }

}
