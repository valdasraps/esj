package net.eventstore.client.operation;

import net.eventstore.client.ResponseReceiver;
import net.eventstore.client.model.RequestResponseOperation;
import net.eventstore.client.message.WriteEvents;
import net.eventstore.client.message.WriteEventsCompleted;
import net.eventstore.client.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 * @author Stasys
 */
public class AppendToStreamOperation extends RequestResponseOperation<WriteEvents, WriteEventsCompleted> {

    public AppendToStreamOperation(TcpConnection connection, WriteEvents request, ResponseReceiver receiver) {
        super(connection, request, new WriteEventsCompleted(), receiver);
    }

}
