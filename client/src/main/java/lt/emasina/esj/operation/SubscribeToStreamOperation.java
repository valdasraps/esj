package lt.emasina.esj.operation;

import java.util.Arrays;
import java.util.List;

import lt.emasina.esj.MultipleResponsesReceiver;
import lt.emasina.esj.model.Message;
import lt.emasina.esj.model.RequestMultipleResponsesOperation;
import lt.emasina.esj.message.StreamEventAppeared;
import lt.emasina.esj.message.SubscribeToStream;
import lt.emasina.esj.message.SubscriptionConfirmation;
import lt.emasina.esj.message.SubscriptionDropped;
import lt.emasina.esj.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class SubscribeToStreamOperation extends RequestMultipleResponsesOperation<SubscribeToStream, Message> {

    public SubscribeToStreamOperation(TcpConnection connection, SubscribeToStream request, MultipleResponsesReceiver receiver) {
        super(connection, request, Arrays.asList((Message) new SubscriptionConfirmation(), (Message) new StreamEventAppeared(), (Message) new SubscriptionDropped()), receiver);
    }

    private SubscribeToStreamOperation(TcpConnection connection, List<Message> responseMessages, SubscribeToStream request, MultipleResponsesReceiver receiver) {
        super(connection, request, responseMessages, receiver);
    }

}
