package lt.emasina.esj;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lt.emasina.esj.message.DeleteStream;
import lt.emasina.esj.message.DropSubscription;
import lt.emasina.esj.message.ReadAllEventsForward;
import lt.emasina.esj.message.ReadEvent;
import lt.emasina.esj.message.SubscribeToStream;
import lt.emasina.esj.message.WriteEvents;
import lt.emasina.esj.model.Event;
import lt.emasina.esj.model.RequestMultipleResponsesOperation;
import lt.emasina.esj.operation.AppendToStreamOperation;
import lt.emasina.esj.operation.DeleteStreamOperation;
import lt.emasina.esj.operation.ReadAllEventsForwardOperation;
import lt.emasina.esj.operation.ReadEventFromStreamOperation;
import lt.emasina.esj.operation.SubscribeToStreamOperation;
import lt.emasina.esj.tcp.TcpConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main ESJ Client entry point.
 * 
 * @author valdo
 */
public class EventStore implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(EventStore.class);
    
    private static final int NUMBER_OF_THREADS_REQUIRED = 3;
    private static final int TRY_CONNECT_COUNT = 20;
    private static final int CONNECT_DELAY = 100;

    private final TcpConnection connection;
    private final ExecutorService executor;
    private boolean localExecutor = false;
    
    /**
     * Create EvenStore client object to access EventStore functionality.
     * @param host store server host
     * @param port store server TCP port (i.e. 1113)
     * @throws IOException 
     */
    public EventStore(InetAddress host, int port) throws IOException {
        this(host, port, new Settings(), Executors.newFixedThreadPool(NUMBER_OF_THREADS_REQUIRED));
        this.localExecutor = true;
    }
    
    /**
     * Create EvenStore client object to access EventStore functionality.
     * @param host store server host
     * @param port store server TCP port (i.e. 1113)
     * @param settings additional settings
     * @throws IOException 
     */
    public EventStore(InetAddress host, int port, Settings settings) throws IOException {
        this(host, port, settings, Executors.newFixedThreadPool(NUMBER_OF_THREADS_REQUIRED));
        this.localExecutor = true;
    }

    /**
     * Create EvenStore client object to access EventStore functionality.
     * @param host store server host
     * @param port store server TCP port (i.e. 1113)
     * @param executor thread executor service (for EE integration)
     * @throws IOException 
     */
    public EventStore(InetAddress host, int port, ExecutorService executor) throws IOException {
        this(host, port, new Settings(), executor);
    }

    /**
     * Create EvenStore client object to access EventStore functionality.
     * @param host store server host
     * @param port store server TCP port (i.e. 1113)
     * @param settings additional settings
     * @param executor thread executor service (for EE integration)
     * @throws IOException 
     */
    public EventStore(InetAddress host, int port, Settings settings, ExecutorService executor) throws IOException {
        this.connection = new TcpConnection(host, port, settings, executor);
        this.executor = executor;

        if (hasConnectionStarted() == false) {
            log.error("Connection could not be established");
            try {
                this.close();
            } catch (Exception e) { /* Ignore - no connection */ }
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
                log.debug("Connection started after {} ms", (i + 1) * CONNECT_DELAY);
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
    public final void close() throws Exception {
        log.debug("EventStore close");
        connection.close();
        if (this.localExecutor) {
            this.executor.shutdownNow();
        }
    }

    /**
     * @return the connection
     */
    public TcpConnection getConnection() {
        return connection;
    }

}
