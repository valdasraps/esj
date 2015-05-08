package lt.emasina.esj.message;

import java.util.List;

import lt.emasina.esj.message.ClientMessageDtos.ReadStreamEventsCompleted.ReadStreamResult;
import lt.emasina.esj.message.ClientMessageDtos.ResolvedIndexedEvent;
import lt.emasina.esj.model.Message;
import lt.emasina.esj.model.ParseException;
import lt.emasina.esj.tcp.TcpCommand;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * WriteEventsCompleted
 *
 * @author Stasys
 */
public class ReadAllEventsForwardCompleted extends Message {

    private int eventsCount;
    private List<ResolvedIndexedEvent> eventList;
    private boolean endOfStream;
    private int lastEventNr;
    private int nexteventNr;
    private ReadStreamResult result;

    public ReadAllEventsForwardCompleted() {
        super(TcpCommand.ReadStreamEventsForwardCompleted);
    }

    @Override
    public void parse(byte[] data) throws ParseException {
        try {
            ClientMessageDtos.ReadStreamEventsCompleted dto = ClientMessageDtos.ReadStreamEventsCompleted.parseFrom(data);
            this.eventsCount = dto.getEventsCount();
            this.eventList = dto.getEventsList();
            this.endOfStream = dto.getIsEndOfStream();
            this.lastEventNr = dto.getLastEventNumber();
            this.nexteventNr = dto.getNextEventNumber();
            this.result = dto.getResult();
        } catch (InvalidProtocolBufferException ex) {
            throw new ParseException(ex);
        }
    }

    protected String toResultInfo() {
        return String.format("Received (%s). Event count = %s, endOfStream = %s, lastEventNr = %s, nexteventNr = %s",
                this.getResult(),
                eventsCount,
                endOfStream,
                lastEventNr,
                nexteventNr);
    }

    /**
     * @return the eventsCount
     */
    public int getEventsCount() {
        return eventsCount;
    }

    /**
     * @return the eventList
     */
    public List<ResolvedIndexedEvent> getEventList() {
        return eventList;
    }

    /**
     * @return the endOfStream
     */
    public boolean isEndOfStream() {
        return endOfStream;
    }

    /**
     * @return the lastEventNr
     */
    public int getLastEventNr() {
        return lastEventNr;
    }

    /**
     * @return the nexteventNr
     */
    public int getNexteventNr() {
        return nexteventNr;
    }

    /**
     * @return the result
     */
    public ReadStreamResult getResult() {
        return result;
    }

}
