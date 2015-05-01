package net.eventstore.client.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import lombok.Getter;
import net.eventstore.client.Settings;
import net.eventstore.client.model.RequestOperation;

import org.apache.log4j.Logger;

public class TcpConnection implements AutoCloseable {

    private static final Logger log = Logger.getLogger(TcpConnection.class);
    
    public static final int HEADER_SIZE = 4;

    @Getter
    private final Settings settings;
    private final TcpSocketManager manager;

    private final ExecutorService managerExecutor = Executors.newSingleThreadExecutor();

    public TcpConnection(InetAddress host, int port, Settings settings) throws IOException {
        this.settings = settings;
        this.manager = new TcpSocketManager(this, host, port);
        managerExecutor.submit(manager);
    }

    public void send(RequestOperation op) {
        this.manager.scheduleSend(op);
    }

    @Override
    public void close() throws IOException {
        log.debug("TcpConnection close");
        this.managerExecutor.shutdownNow();
        //manager.running.release();
    }

    public boolean hasStarted() {
        Semaphore running = manager.getRunning();
        return (running != null) && (running.hasQueuedThreads() != false);
    }

}
