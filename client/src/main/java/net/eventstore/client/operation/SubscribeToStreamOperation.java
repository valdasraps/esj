package net.eventstore.client.operation;

import java.util.Arrays;
import java.util.List;

import net.eventstore.client.MultipleResponsesReceiver;
import net.eventstore.client.model.Message;
import net.eventstore.client.model.RequestMultipleResponsesOperation;
import net.eventstore.client.message.StreamEventAppeared;
import net.eventstore.client.message.SubscribeToStream;
import net.eventstore.client.message.SubscriptionConfirmation;
import net.eventstore.client.message.SubscriptionDropped;
import net.eventstore.client.tcp.TcpConnection;

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
