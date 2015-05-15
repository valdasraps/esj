package lt.emasina.esj.model.converter;

import com.google.protobuf.ByteString;

/**
 * Converts any object into protobuf's ByteString using it's
 * <code>toString()</code> method.
 */
public final class StringToByteStringConverter implements
        ObjectToByteStringConverter<Object> {

    @Override
    public final int getContentType() {
        return NOT_JSON_DATA_TYPE;
    }

    @Override
    public final ByteString convert(final Object obj) {
        if (obj == null) {
            return ByteString.EMPTY;
        }
        return ByteString.copyFromUtf8(obj.toString());
    }

}
