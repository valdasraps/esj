package net.eventstore.client.operation;

import net.eventstore.client.ResponseReceiver;
import net.eventstore.client.model.RequestResponseOperation;
import net.eventstore.client.message.DropSubscription;
import net.eventstore.client.message.SubscriptionDropped;
import net.eventstore.client.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class DropSubscriptionOperation extends RequestResponseOperation<DropSubscription, SubscriptionDropped> {

    public DropSubscriptionOperation(TcpConnection connection, DropSubscription request, ResponseReceiver receiver) {
        super(connection, request, new SubscriptionDropped(), receiver);
    }

}
