package lt.emasina.esj.model.converter;

import com.google.protobuf.ByteString;

/**
 * Converts a byte array into protobuf's ByteString.
 */
public final class ByteArrayToByteStringConverter implements
        ObjectToByteStringConverter<byte[]> {

     private final int contentType;
            
     /**
      * Constructor with content-type.
      * 
      * @param json
      *            TRUE if the byte array contains JSON data.
      */
     public ByteArrayToByteStringConverter(boolean json) {
         super();
         if (json) {
             this.contentType = JSON_DATA_TYPE;
         } else {
             this.contentType = NOT_JSON_DATA_TYPE;
         }
     }

    @Override
    public final int getContentType() {
        return contentType;
    }

    @Override
    public final ByteString convert(final byte[] obj) {
        if (obj == null) {
            return ByteString.EMPTY;
        }
        return ByteString.copyFrom(obj);
    }

}
