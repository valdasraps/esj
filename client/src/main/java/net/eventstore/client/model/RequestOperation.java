package net.eventstore.client.model;

import java.util.UUID;

import lombok.Getter;
import net.eventstore.client.tcp.TcpConnection;
import net.eventstore.client.tcp.TcpPackage;

import org.apache.log4j.Logger;

/**
 * Operation
 *
 * @author Stasys
 * @param <F> Forward (to send) message
 */
@Getter
public abstract class RequestOperation<F extends Message> extends ResponseOperation {

    private static final Logger log = Logger.getLogger(RequestOperation.class);
    
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
