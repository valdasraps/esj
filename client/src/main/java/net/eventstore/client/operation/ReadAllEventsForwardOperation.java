package net.eventstore.client.operation;

import net.eventstore.client.ResponseReceiver;
import net.eventstore.client.model.RequestResponseOperation;
import net.eventstore.client.message.ReadAllEventsForward;
import net.eventstore.client.message.ReadAllEventsForwardCompleted;
import net.eventstore.client.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class ReadAllEventsForwardOperation extends RequestResponseOperation<ReadAllEventsForward, ReadAllEventsForwardCompleted> {

    public ReadAllEventsForwardOperation(TcpConnection connection, ReadAllEventsForward request, ResponseReceiver receiver) {
        super(connection, request, new ReadAllEventsForwardCompleted(), receiver);
    }

}
