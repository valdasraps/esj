package lt.emasina.esj.model;

import java.util.List;

import lt.emasina.esj.MultipleResponsesReceiver;
import lt.emasina.esj.message.ExceptionMessage;
import lt.emasina.esj.tcp.TcpCommand;
import lt.emasina.esj.tcp.TcpConnection;
import lt.emasina.esj.tcp.TcpPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation
 *
 * @author Stasys
 * @param <F> Forward (to send) message
 * @param <B> Backward (to receive on success) message
 */
public abstract class RequestMultipleResponsesOperation<F extends Message, B extends Message> extends RequestOperation<F> {

    private static final Logger log = LoggerFactory.getLogger(RequestMultipleResponsesOperation.class);
    
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
                        log.debug("Compare commands - {} = {}", current.getCommand(), pckg.getCommand());
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
            log.error("Exception occured. Command: {}, message: {}", exception.getCommand(), exception.getMessage());
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
     * @return the responses
     */
    public List<B> getResponses() {
        return responses;
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
    public MultipleResponsesReceiver getReceiver() {
        return receiver;
    }

}
