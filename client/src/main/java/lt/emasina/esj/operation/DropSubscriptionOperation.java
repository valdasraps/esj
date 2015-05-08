package lt.emasina.esj.operation;

import lt.emasina.esj.ResponseReceiver;
import lt.emasina.esj.model.RequestResponseOperation;
import lt.emasina.esj.message.DropSubscription;
import lt.emasina.esj.message.SubscriptionDropped;
import lt.emasina.esj.tcp.TcpConnection;

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
