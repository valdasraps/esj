package lt.emasina.esj.message;

import lt.emasina.esj.message.ClientMessageDtos.SubscriptionDropped.SubscriptionDropReason;
import lt.emasina.esj.model.Message;
import lt.emasina.esj.model.ParseException;
import lt.emasina.esj.tcp.TcpCommand;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * WriteEventsCompleted
 *
 * @author Stasys
 */
public class SubscriptionDropped extends Message {

    private SubscriptionDropReason dropReason;

    public SubscriptionDropped() {
        super(TcpCommand.SubscriptionDropped);
    }

    @Override
    public void parse(byte[] data) throws ParseException {
        try {
            ClientMessageDtos.SubscriptionDropped dto = ClientMessageDtos.SubscriptionDropped.parseFrom(data);
            dropReason = dto.getReason();
        } catch (InvalidProtocolBufferException ex) {
            throw new ParseException(ex);
        }
    }

    private String getReason() {
        String reason = "";
        switch (dropReason) {
            case Unsubscribed:
                reason = "Unsubscribed";
                break;
            case AccessDenied:
                reason = "Access denied";
                break;
            default:
                reason = "Unknown";
                break;
        }
        return reason;
    }

    protected String toResultInfo() {
        return String.format("Reason = %s.", getReason());
    }

    /**
     * @return the dropReason
     */
    public SubscriptionDropReason getDropReason() {
        return dropReason;
    }

}
