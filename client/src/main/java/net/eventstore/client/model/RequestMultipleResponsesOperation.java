package net.eventstore.client.model;

import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import net.eventstore.client.MultipleResponsesReceiver;
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
public abstract class RequestMultipleResponsesOperation<F extends Message, B extends Message> extends RequestOperation<F> {

    private final List<B> responses;
    private B response;
    private ExceptionMessage exception;
    private final MultipleResponsesReceiver receiver;

    public RequestMultipleResponsesOperation(TcpConnection connection, F request, List<B> responses, MultipleResponsesReceiver receiver) {
        super(connection, request);
        this.responses = responses;
        this.receiver = receiver;
        this.receiver.onReceiverCreated(this);
        this.hasSingleResponse = false;
    }

    public void setRequest(F request) {
        this.request = request;
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
                    boolean didParse = false;
                    for (int i = 0; i < responses.size(); i++) {
                        B current = responses.get(i);
                        log.debug(String.format("Compare commands - %s = %s", current.getCommand(), pckg.getCommand()));
                        if (current.getCommand().equals(pckg.getCommand())) {
                            response = current;
                            response.parse(getRequest(), pckg);
                            didParse = true;
                            break;
                        }
                    }
                    if (didParse == false) {
                        exception = new ExceptionMessage(pckg.getCommand(), "Could not parse package.");
                    }
                } catch (ParseException ex) {
                    exception = new ExceptionMessage(pckg.getCommand(), ex.getMessage());
                }
        }

        if (exception != null) {
            log.error(String.format("Exception occured. Command: %s, message: %s", exception.getCommand(), exception.getMessage()));
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

}
