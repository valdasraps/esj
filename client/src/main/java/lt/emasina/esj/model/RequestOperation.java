package lt.emasina.esj.model;

import java.util.UUID;

import lt.emasina.esj.tcp.TcpConnection;
import lt.emasina.esj.tcp.TcpPackage;

/**
 * Operation
 *
 * @author Stasys
 * @param <F> Forward (to send) message
 */
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

    /**
     * @return the connection
     */
    public TcpConnection getConnection() {
        return connection;
    }

    /**
     * @return the request
     */
    public F getRequest() {
        return request;
    }

}
