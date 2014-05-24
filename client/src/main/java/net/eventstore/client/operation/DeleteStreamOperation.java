package net.eventstore.client.operation;

import net.eventstore.client.ResponseReceiver;
import net.eventstore.client.model.RequestResponseOperation;
import net.eventstore.client.message.DeleteStream;
import net.eventstore.client.message.DeleteStreamCompleted;
import net.eventstore.client.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class DeleteStreamOperation extends RequestResponseOperation<DeleteStream, DeleteStreamCompleted> {

    public DeleteStreamOperation(TcpConnection connection, DeleteStream request, ResponseReceiver receiver) {
        super(connection, request, new DeleteStreamCompleted(), receiver);
    }

}
