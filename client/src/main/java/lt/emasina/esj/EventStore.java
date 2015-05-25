package lt.emasina.esj;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lt.emasina.esj.message.DeleteStream;
import lt.emasina.esj.message.DropSubscription;
import lt.emasina.esj.message.ReadAllEventsForward;
import lt.emasina.esj.message.ReadEvent;
import lt.emasina.esj.message.SubscribeToStream;
import lt.emasina.esj.message.WriteEvents;
import lt.emasina.esj.model.Event;
import lt.emasina.esj.model.ExpectedVersion;
import lt.emasina.esj.model.RequestMultipleResponsesOperation;
import lt.emasina.esj.model.UserCredentials;
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

    /** This write should not conflict with anything and should always succeed. */
    public final static int VERSION_ANY = -2;
    
    /** The stream being written to should not yet exist. If it does exist treat that as a concurrency problem. */
    public final static int VERSION_NO_STREAM = -1;
    
    /** The stream should exist and should be empty. If it does not exist or is not empty treat that as a concurrency problem. */
    public final static int VERSION_EMPTY_STREAM = 0;
    
    private static final Logger log = LoggerFactory.getLogger(EventStore.class);
    
    private static final int NUMBER_OF_THREADS_REQUIRED = 3;
    private static final int TRY_CONNECT_COUNT = 20;
    private static final int CONNECT_DELAY = 100;

    private final TcpConnection connection;
    private final ExecutorService executor;
    private final UserCredentials userCredentials;
    private boolean localExecutor = false;
    
    /**
     * Create EvenStore client object to access EventStore functionality.
     * <ul>
     * <li>No special settings.</li>
     * <li>A fixed thread pool will used to handle the connection created with {@link #NUMBER_OF_THREADS_REQUIRED} threads.</li>
     * <li>No user credentials are set.</li>
     * </ul>
     * 
     * @param host 
     *            Store server host.
     * @param port 
     *            Store server TCP port (i.e. 1113).
     *            
     * @throws IOException Connecting to the event stored failed.
     */
    public EventStore(InetAddress host, int port) throws IOException {
        this(host, port, new Settings(), Executors.newFixedThreadPool(NUMBER_OF_THREADS_REQUIRED), null);
        this.localExecutor = true;
    }
    
    /**
     * Create EvenStore client object to access EventStore functionality.
     * <ul>
     * <li>A fixed thread pool will used to handle the connection created with {@link #NUMBER_OF_THREADS_REQUIRED} threads.</li>
     * <li>No user credentials are set.</li>
     * </ul>
     * 
     * @param host
     *            Store server host.
     * @param port
     *            Store server TCP port (i.e. 1113).
     * @param settings
     *            Additional settings.
     * 
     * @throws IOException Connecting to the event stored failed.
     */
    public EventStore(InetAddress host, int port, Settings settings) throws IOException {
        this(host, port, settings, Executors.newFixedThreadPool(NUMBER_OF_THREADS_REQUIRED), null);
        this.localExecutor = true;
    }

    /**
     * Create EvenStore client object to access EventStore functionality.
     * <ul>
     * <li>No special settings.</li>
     * <li>No user credentials are set.</li>
     * </ul>
     * 
     * @param host 
     *            Store server host.
     * @param port
     *            Store server TCP port (i.e. 1113).
     * @param executor
     *            Thread executor service (for EE integration).
     * 
     * @throws IOException Connecting to the event stored failed.
     */
    public EventStore(InetAddress host, int port, ExecutorService executor) throws IOException {
        this(host, port, new Settings(), executor, null);
    }

    /**
     * Create EvenStore client object to access EventStore functionality.
     * 
     * @param host 
     *            Store server host.
     * @param port 
     *            Store server TCP port (i.e. 1113).
     * @param settings 
     *            Additional settings.
     * @param executor 
     *            Thread executor service (for EE integration).
     * @param userCredentials
     *            User and password.
     * 
     * @throws IOException Connecting to the event stored failed.
     */
    public EventStore(InetAddress host, int port, Settings settings, ExecutorService executor, UserCredentials userCredentials) throws IOException {
        this.connection = new TcpConnection(host, port, settings, executor);
        this.executor = executor;
        this.userCredentials = userCredentials;

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

    /**
     * Writes a number of events to a stream without verifying the current version.
     * 
     * @param streamId
     *            The stream to write to.
     * @param receiver 
     *            Listener that gets notified about the result.
     * @param events 
     *            Events to write.
     */
    public void appendToStream(String streamId, ResponseReceiver receiver, Event... events) {
        appendToStream(streamId, receiver, Arrays.asList(events));
    }
    
    /**
     * Writes a number of events to a stream without verifying the current version.
     * 
     * @param streamId
     *            The stream to write to.
     * @param receiver 
     *            Listener that gets notified about the result.
     * @param events 
     *            Events to write.
     */
    public void appendToStream(String streamId, ResponseReceiver receiver, Collection<Event> events) {
        appendToStream(streamId, ExpectedVersion.Any, receiver, events);
    }
    
    /**
     * Writes a number of events to a stream if it has a given version.
     * 
     * @param streamId
     *            The stream to write to.
     * @param expectedVersion 
     *            Current version of the stream that is expected by the caller.
     * @param receiver 
     *            Listener that gets notified about the result.
     * @param events 
     *            Events to write.
     */
    public void appendToStream(String streamId, ExpectedVersion expectedVersion, ResponseReceiver receiver, Event... events) {
        appendToStream(streamId, expectedVersion, receiver, Arrays.asList(events));
    }
    
    /**
     * Writes a number of events to a stream if it has a given version.
     * 
     * @param streamId
     *            The stream to write to.
     * @param expectedVersion 
     *            Current version of the stream that is expected by the caller.
     * @param receiver 
     *            Listener that gets notified about the result.
     * @param events 
     *            Events to write.
     */
    public void appendToStream(String streamId, ExpectedVersion expectedVersion, ResponseReceiver receiver, Collection<Event> events) {
        WriteEvents writer = new WriteEvents(streamId, expectedVersion, userCredentials, events);
        AppendToStreamOperation op = new AppendToStreamOperation(connection, writer, receiver);
        op.send();
    }

    /**
     * Writes a number of events to a stream if it has a given version.
     * 
     * @param streamId
     *            The stream to write to.
     * @param expectedVersion 
     *            Current version of the stream that is expected by the caller.
     * @param receiver 
     *            Listener that gets notified about the result.
     * @param events 
     *            Events to write.
     */
    public void appendToStream(String streamId, int expectedVersion, ResponseReceiver receiver, Collection<Event> events) {
        WriteEvents writer = new WriteEvents(streamId, expectedVersion, userCredentials, events);
        AppendToStreamOperation op = new AppendToStreamOperation(connection, writer, receiver);
        op.send();
    }
    
    /**
     * Reads a single event from a stream.
     * 
     * @param streamId
     *            The stream to read from.
     * @param eventNumber
     *            The number of the event to read.
     * @param receiver 
     *            Listener that gets notified about the result.
     */
    public void readFromStream(String streamId, int eventNumber, ResponseReceiver receiver) {
        ReadEvent reader = new ReadEvent(streamId, eventNumber, userCredentials);
        ReadEventFromStreamOperation op = new ReadEventFromStreamOperation(connection, reader, receiver);
        op.send();
    }

    /**
     * Drops a subscription identified by a correlation ID.
     * 
     * @param op 
     *            Handles sending a message and receives the results. 
     */
    public void dropSubscription(RequestMultipleResponsesOperation op) {
        DropSubscription subscriber = new DropSubscription(op.getCorrelationId(), userCredentials);
        op.setRequest(subscriber);
        op.send();
    }

    /**
     * Subscribes to a scream to get notified about changes.
     * 
     * @param streamId
     *            The stream to subscribe to.
     * @param receiver 
     *            Listener that gets notified about the result.
     */
    public void subscribeToStream(String streamId, MultipleResponsesReceiver receiver) {
        SubscribeToStream subscriber = new SubscribeToStream(streamId, userCredentials);
        SubscribeToStreamOperation op = new SubscribeToStreamOperation(connection, subscriber, receiver);
        op.send();
    }

    /**
     * Soft deletes a stream without verifying the current version.
     * 
     * @param streamId 
     *            The stream to delete.
     * @param receiver 
     *            Listener that gets notified about the result.
     */
    public void deleteStream(String streamId, ResponseReceiver receiver) {
        deleteStream(streamId, ExpectedVersion.Any, receiver);
    }

    /**
     * Soft deletes a stream if it has a given version.
     * 
     * @param streamId 
     *            The stream to read from.
     * @param expectedVersion 
     *            Current version of the stream that is expected by the caller.
     * @param receiver 
     *            Listener that gets notified about the result.
     */
    public void deleteStream(String streamId, ExpectedVersion expectedVersion, ResponseReceiver receiver) {
        deleteStream(streamId, expectedVersion.getMask(), receiver);
    }

    /**
     * Soft deletes a stream if it has a given version.
     * 
     * @param streamId 
     *            The stream to read from.
     * @param expectedVersion 
     *            Current version of the stream that is expected by the caller.
     * @param receiver 
     *            Listener that gets notified about the result.
     */
    public void deleteStream(String streamId, int expectedVersion, ResponseReceiver receiver) {
        deleteStream(streamId, expectedVersion, false, receiver);
    }
    
    /**
     * Deletes a stream if it has a given version.
     * 
     * @param streamId 
     *            The stream to read from.
     * @param expectedVersion 
     *            Current version of the stream that is expected by the caller.
     * @param hard 
     *            TRUE for a hard delete or FALSE for a soft delete.
     * @param receiver 
     *            Listener that gets notified about the result.
     */
    public void deleteStream(String streamId, int expectedVersion, boolean hard, ResponseReceiver receiver) {
        DeleteStream subscriber = new DeleteStream(streamId, expectedVersion, hard, userCredentials);
        DeleteStreamOperation op = new DeleteStreamOperation(connection, subscriber, receiver);
        op.send();
    }
    
    /**
     * Reads count Events from an Event Stream forwards (e.g. oldest to newest)
     * starting from position start
     * 
     * @param streamId
     *            The stream to read from.
     * @param from
     *            The starting point to read from.
     * @param maxCount
     *            The count of items to read.
     * @param receiver 
     *            Listener that gets notified about the result.
     */
    public void readAllEventsForward(String streamId, int from, int maxCount, ResponseReceiver receiver) {
        ReadAllEventsForward subscriber = new ReadAllEventsForward(streamId, from, maxCount, userCredentials);
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
     * Returns the TCP connection used internally.
     * 
     * @return The connection.
     */
    public TcpConnection getConnection() {
        return connection;
    }

}
