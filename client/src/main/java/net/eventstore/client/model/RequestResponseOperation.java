package net.eventstore.client.model;

import net.eventstore.client.ResponseReceiver;
import net.eventstore.client.message.ExceptionMessage;
import net.eventstore.client.tcp.TcpCommand;
import net.eventstore.client.tcp.TcpConnection;
import net.eventstore.client.tcp.TcpPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation
 *
 * @author Stasys
 * @param <F> Forward (to send) message
 * @param <B> Backward (to receive on success) message
 */
public abstract class RequestResponseOperation<F extends Message, B extends Message> extends RequestOperation<F> {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseOperation.class);
    
    private final B response;
    private ExceptionMessage exception;
    private final ResponseReceiver receiver;

    public RequestResponseOperation(TcpConnection connection, F request, B response, ResponseReceiver receiver) {
        super(connection, request);
        this.response = response;
        this.receiver = receiver;
    }

    public void onError(Exception e) {
        receiver.onErrorReturn(e);
    }

    @Override
    public void setResponsePackage(TcpPackage pckg) {
        switch (pckg.getCommand()) {
            case BadRequest:
            case NotHandled:
            case NotAuthenticated:
                exception = new ExceptionMessage(pckg);
                break;
            default:
                try {
                    response.parse(getRequest(), pckg);
                } catch (ParseException ex) {
                    exception = new ExceptionMessage(pckg.getCommand(), ex.getMessage());
                }
        }

        if (exception != null) {
            log.debug(response.toResultInfo());
        } else {
            if (!response.getCommand().equals(TcpCommand.HeartbeatResponseCommand)) {
                log.debug(response.toResultInfo());
            }
        }

        doneProcessing();
        if (exception != null){
        	receiver.onErrorReturn(exception);
        }
        else {
            receiver.onResponseReturn(response);
        }

    }

    /**
     * @return the response
     */
    public B getResponse() {
        return response;
    }

    /**
     * @return the exception
     */
    public ExceptionMessage getException() {
        return exception;
    }

    /**
     * @return the receiver
     */
    public ResponseReceiver getReceiver() {
        return receiver;
    }

}
