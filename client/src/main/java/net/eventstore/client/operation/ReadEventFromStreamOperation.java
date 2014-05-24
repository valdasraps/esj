package net.eventstore.client.operation;

import net.eventstore.client.ResponseReceiver;
import net.eventstore.client.model.RequestResponseOperation;
import net.eventstore.client.message.ReadEvent;
import net.eventstore.client.message.ReadEventCompleted;
import net.eventstore.client.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class ReadEventFromStreamOperation extends RequestResponseOperation<ReadEvent, ReadEventCompleted> {

    public ReadEventFromStreamOperation(TcpConnection connection, ReadEvent request, ResponseReceiver receiver) {
        super(connection, request, new ReadEventCompleted(), receiver);
    }

}
