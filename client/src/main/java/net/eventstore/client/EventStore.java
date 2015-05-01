package net.eventstore.client;

import java.io.IOException;
import java.net.InetAddress;

import net.eventstore.client.message.DeleteStream;
import net.eventstore.client.message.DropSubscription;
import net.eventstore.client.message.ReadAllEventsForward;
import net.eventstore.client.message.ReadEvent;
import net.eventstore.client.message.SubscribeToStream;
import net.eventstore.client.message.WriteEvents;
import net.eventstore.client.model.Event;
import net.eventstore.client.model.RequestMultipleResponsesOperation;
import net.eventstore.client.operation.AppendToStreamOperation;
import net.eventstore.client.operation.DeleteStreamOperation;
import net.eventstore.client.operation.ReadAllEventsForwardOperation;
import net.eventstore.client.operation.ReadEventFromStreamOperation;
import net.eventstore.client.operation.SubscribeToStreamOperation;
import net.eventstore.client.tcp.TcpConnection;

import org.apache.log4j.Logger;

public class EventStore implements AutoCloseable {

    private static final Logger log = Logger.getLogger(EventStore.class);
    
    private static final int TRY_CONNECT_COUNT = 20;
    private static final int CONNECT_DELAY = 100;

    private final TcpConnection connection;

    public EventStore(InetAddress host, int port) throws IOException {
        this(host, port, new Settings());
    }

    public EventStore(InetAddress host, int port, Settings settings) throws IOException {
        this.connection = new TcpConnection(host, port, settings);

        if (hasConnectionStarted() == false) {
            log.error("Connection could not be established");
            try {
                this.connection.close();
            } catch (IOException e) { /* Ignore - no connection */ }
            throw new IOException("Could not establish connection");
        }
    }

    private boolean hasConnectionStarted() {
        boolean hasStarted = false;
        for (int i = 0; i < TRY_CONNECT_COUNT; i++) {
            try {
                
                Thread.sleep(CONNECT_DELAY);
                
            } catch (InterruptedException e) {
                log.error("Error while establishing connection", e);
            }
            hasStarted = connection.hasStarted();
            if (hasStarted) {
                log.debug(String.format("Connection started after %s ms", (i + 1) * CONNECT_DELAY));
                break;
            }
        }
        return hasStarted;
    }

    public void appendToStream(String streamId, ResponseReceiver receiver, Event... events) {
        WriteEvents writer = new WriteEvents(streamId, events);
        AppendToStreamOperation op = new AppendToStreamOperation(connection, writer, receiver);
        op.send();
    }

    public void readFromStream(String streamId, int eventNumber, ResponseReceiver receiver) {
        ReadEvent reader = new ReadEvent(streamId, eventNumber);
        ReadEventFromStreamOperation op = new ReadEventFromStreamOperation(connection, reader, receiver);
        op.send();
    }

    public void dropSubscription(RequestMultipleResponsesOperation op) {
        DropSubscription subscriber = new DropSubscription(op.getCorrelationId());
        op.setRequest(subscriber);
        op.send();
    }

    public void subscribeToStream(String streamId, MultipleResponsesReceiver receiver) {
        SubscribeToStream subscriber = new SubscribeToStream(streamId);
        SubscribeToStreamOperation op = new SubscribeToStreamOperation(connection, subscriber, receiver);
        op.send();
    }

    public void deleteStream(String streamId, ResponseReceiver receiver) {
        DeleteStream subscriber = new DeleteStream(streamId);
        DeleteStreamOperation op = new DeleteStreamOperation(connection, subscriber, receiver);
        op.send();
    }

    public void readAllEventsForward(String streamId, int from, int maxCount, ResponseReceiver receiver) {
        ReadAllEventsForward subscriber = new ReadAllEventsForward(streamId, from, maxCount);
        ReadAllEventsForwardOperation op = new ReadAllEventsForwardOperation(connection, subscriber, receiver);
        op.send();
    }

    @Override
    public void close() throws Exception {
        log.debug("EventStore close");
        connection.close();
    }

    /**
     * @return the connection
     */
    public TcpConnection getConnection() {
        return connection;
    }

}
