package net.eventstore.client.model;

import java.util.UUID;
import java.util.concurrent.Semaphore;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import net.eventstore.client.tcp.TcpPackage;
import net.eventstore.client.tcp.TcpConnection;

/**
 * Operation
 *
 * @author Stasys
 * @param <F> Forward (to send) message
 */
@Log4j
@Getter
public abstract class RequestOperation<F extends Message> extends ResponseOperation {

    private final TcpConnection connection;
    protected F request;

    public RequestOperation(TcpConnection connection, F request) {
        this.connection = connection;
        this.request = request;
    }

    //private final Semaphore processing = new Semaphore(0);

    public TcpPackage getRequestPackage() {
        return request.getTcpPackage(connection.getSettings());
    }

    public UUID getCorrelationId() {
        return request.getCorrelationId();
    }

    public void doneProcessing() {
        //processing.release();
    }

    public void send() {
        sendAsync();
        /*try {
            processing.acquire();
        } catch (InterruptedException ex) {
            log.warn("Processing was interrpupted", ex);
        }*/
    }

    public void sendAsync() {
        connection.send(this);
    }

}
