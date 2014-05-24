package net.eventstore.client.message;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import net.eventstore.client.model.ParseException;
import net.eventstore.client.model.Message;
import net.eventstore.client.tcp.TcpCommand;

/**
 * WriteEventsCompleted
 *
 * @author Stasys
 */
@Getter
public class SubscriptionConfirmation extends Message {

    private long lastCommitPosition;
    private int lastEventNumber;

    public SubscriptionConfirmation() {
        super(TcpCommand.SubscriptionConfirmation);
    }

    @Override
    public void parse(byte[] data) throws ParseException {
        try {
            ClientMessageDtos.SubscriptionConfirmation dto = ClientMessageDtos.SubscriptionConfirmation.parseFrom(data);
            lastCommitPosition = dto.getLastCommitPosition();
            lastEventNumber = dto.getLastEventNumber();
            //this.firstEventNumber = dto.getFirstEventNumber();
            //this.lastEventNumber = dto.getLastEventNumber();
            //this.message = dto.getMessage();
        } catch (InvalidProtocolBufferException ex) {
            throw new ParseException(ex);
        }
    }

    protected String toResultInfo() {
        return String.format("LastCommitPosition = %s. LastEventNumber = %s.",
                lastCommitPosition,
                lastEventNumber);
    }

}
