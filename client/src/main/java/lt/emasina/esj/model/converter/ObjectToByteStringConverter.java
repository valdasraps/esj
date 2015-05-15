package lt.emasina.esj.model.converter;

import com.google.protobuf.ByteString;

/**
 * Converts an object into protobuf's ByteString.
 *
 * @param <INPUT> Type of the input.
 */
public interface ObjectToByteStringConverter<INPUT> {

    /** The data in the byte array is some other type. */
    public static final int NOT_JSON_DATA_TYPE = 0;

    /** The data in the byte array is of type JSON. */
    public static final int JSON_DATA_TYPE = 1;
    
    /**
     * Returns the type of the object.
     *  
     * @return Type of the byte array.
     */
    public int getContentType();
    
    /**
     * Converts the input object to a byte array. A <code>null</code> 
     * argument will return an empty array.
     * 
     * @param obj Object to convert or <code>null</code>.
     * 
     * @return Serialized object or an empty array - Never <code>null</code>.
     */
    public ByteString convert(INPUT obj);
    
}
