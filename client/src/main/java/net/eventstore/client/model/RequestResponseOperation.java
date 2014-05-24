package net.eventstore.client.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import net.eventstore.client.ResponseReceiver;
import net.eventstore.client.message.ExceptionMessage;
import net.eventstore.client.tcp.TcpCommand;
import net.eventstore.client.tcp.TcpPackage;
import net.eventstore.client.tcp.TcpConnection;

/**
 * Operation
 *
 * @author Stasys
 * @param <F> Forward (to send) message
 * @param <B> Backward (to receive on success) message
 */
@Log4j
@Getter
public abstract class RequestResponseOperation<F extends Message, B extends Message> extends RequestOperation<F> {

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

        if (log.isDebugEnabled()) {
            if (exception != null) {
                log.debug(response.toResultInfo());
            } else {
                if (!response.getCommand().equals(TcpCommand.HeartbeatResponseCommand)) {
                    log.debug(response.toResultInfo());
                }
            }
        }

        doneProcessing();
        if (receiver != null) {
            receiver.onResponseReturn(response);
        }

    }

}
