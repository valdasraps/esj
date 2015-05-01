package net.eventstore.client.message;

import net.eventstore.client.model.Message;
import net.eventstore.client.model.ParseException;
import net.eventstore.client.tcp.TcpCommand;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * WriteEventsCompleted
 *
 * @author Stasys
 */
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

    /**
     * @return the lastCommitPosition
     */
    public long getLastCommitPosition() {
        return lastCommitPosition;
    }

    /**
     * @return the lastEventNumber
     */
    public int getLastEventNumber() {
        return lastEventNumber;
    }

}
