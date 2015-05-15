package lt.emasina.esj.model.converter;

import org.json.JSONObject;

import com.google.protobuf.ByteString;

/**
 * Converts a JSON object into protobuf's ByteString.
 */
public final class JsonToByteStringConverter implements ObjectToByteStringConverter<JSONObject> {

    private final StringToByteStringConverter delegate;
    
    /** 
     * Default constructor.
     */
    public JsonToByteStringConverter() {
        super();
        delegate = new StringToByteStringConverter();
    }
    
    @Override
    public final int getContentType() {
        return JSON_DATA_TYPE;
    }

    @Override
    public final ByteString convert(final JSONObject obj) {
        return delegate.convert(obj);
    }

}
