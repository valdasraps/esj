package net.eventstore.client.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Level;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import net.eventstore.client.model.RequestOperation;
import net.eventstore.client.model.RequestResponseOperation;
import net.eventstore.client.model.ResponseOperation;
import net.eventstore.client.operation.HeartBeatResponseOperation;

/**
 * TcpProcessor
 *
 * @author Stasys
 */
@Log4j
@RequiredArgsConstructor
public class TcpSocketManager implements Runnable {

    @Getter
    private final TcpConnection connection;

    private final InetAddress host;
    private final int port;
    private Socket socket;
    public ExecutorService executor;

    @Getter
    private final Semaphore running = new Semaphore(0);

    @Getter
    private final BlockingQueue<RequestOperation> sending = new LinkedBlockingDeque<>();

    @Getter
    private final Map<UUID, ResponseOperation> receiving = new ConcurrentHashMap<>();

    public void scheduleSend(RequestOperation op) {
        this.sending.add(op);
    }

    public void respondHeartBeat(UUID correlationId) {
        this.sending.add(new HeartBeatResponseOperation(connection, correlationId));
    }

    @Override
    public void run() {
        try {
            log.getParent().setLevel(Level.FATAL);
            if (true) {

                socket = new Socket(host, port);

                if (log.isDebugEnabled()) {
                    log.debug(String.format("Socket opened %s:%d to %s:%d", socket.getLocalAddress(), socket.getLocalPort(), socket.getInetAddress(), socket.getPort()));
                }

                executor = Executors.newFixedThreadPool(2);
                executor.submit(new TcpSender(socket.getOutputStream(), this));
                executor.submit(new TcpReceiver(socket.getInputStream(), this));

                running.acquire();

                try {
                    log.debug("Closing socket...");
                    executor.shutdownNow();
                    // stop sending before stop receiving.
                    // stop all sending events
                    System.out.println("Is sending empty: " + sending.isEmpty());
                    while (sending.isEmpty() == false) {
                        RequestOperation op = sending.poll();
                        if (op instanceof RequestResponseOperation) {
                            RequestResponseOperation rro = (RequestResponseOperation) op;
                            rro.onError(new IOException());
                        }
                        op.doneProcessing();
                    }

                    // stop all receiving events
                    System.out.println("Is receiving empty: " + receiving.isEmpty());
                    for (Map.Entry<UUID, ResponseOperation> entry : receiving.entrySet()) {
                        ResponseOperation op = entry.getValue();
                        if (op instanceof RequestResponseOperation) {
                            RequestResponseOperation rro = (RequestResponseOperation) op;
                            rro.onError(new IOException());
                            rro.doneProcessing();
                        }

                    }
                    receiving.clear();

                    //executor.awaitTermination(WAIT_TO_TERMINATE_THREADS, TimeUnit.MILLISECONDS);
                    socket.close();
                } catch (IOException ex) {
                    log.warn("Error while closing socket", ex);
                }

                // Make sure that what was sent but not received would be re-sent
                // TODO implement
                /*
                 List<UUID> toRemove = new ArrayList<>();
                 for (UUID id: receiving.keySet()) {
                 ResponseOperation op = receiving.get(id);
                 if (op instanceof RequestOperation) {
                 sending.add((RequestOperation) op);
                 toRemove.add(id);
                 }
                 }
                
                 for (UUID id: toRemove) {
                 receiving.remove(id);
                 }
                 */
            }

        } catch (InterruptedException ex) {
            // Silently ignore
        	
            if ((executor != null) && (executor.isShutdown() == false)) {
                executor.shutdownNow();
            }
            if ((socket != null) && (socket.isClosed() == false)) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        } catch (IOException ex) {
            log.error("Error while opening connection", ex);
            /*try {
             connection.close();
             } catch (IOException e) {
             e.printStackTrace();
             }*/
        }
    }

}
