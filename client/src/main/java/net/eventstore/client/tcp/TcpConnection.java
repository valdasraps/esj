package net.eventstore.client.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import net.eventstore.client.Settings;
import net.eventstore.client.model.RequestOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpConnection implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(TcpConnection.class);
    
    public static final int HEADER_SIZE = 4;

    private final Settings settings;
    private final TcpSocketManager manager;

    private final Future<?> managerTask;

    /**
     * Establish the connection to EventStore TCP socket
     * and launch writer/reader threads.
     * 
     * @param host server hostname
     * @param port server TCP port
     * @param settings additional connection settings
     * @param executor executor service
     * @throws IOException 
     */
    public TcpConnection(InetAddress host, int port, Settings settings, ExecutorService executor) throws IOException {
        this.settings = settings;
        this.manager = new TcpSocketManager(this, host, port, executor);
        this.managerTask = executor.submit(manager);
    }

    public void send(RequestOperation op) {
        this.manager.scheduleSend(op);
    }

    @Override
    public void close() throws IOException {
        log.debug("TcpConnection close");
        this.managerTask.cancel(true);
    }

    public boolean hasStarted() {
        Semaphore running = manager.getRunning();
        return (running != null) && (running.hasQueuedThreads() != false);
    }

    /**
     * @return the settings
     */
    public Settings getSettings() {
        return settings;
    }

}
