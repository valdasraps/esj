package lt.emasina.esj.operation;

import lt.emasina.esj.ResponseReceiver;
import lt.emasina.esj.model.RequestResponseOperation;
import lt.emasina.esj.message.WriteEvents;
import lt.emasina.esj.message.WriteEventsCompleted;
import lt.emasina.esj.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 * @author Stasys
 */
public class AppendToStreamOperation extends RequestResponseOperation<WriteEvents, WriteEventsCompleted> {

    public AppendToStreamOperation(TcpConnection connection, WriteEvents request, ResponseReceiver receiver) {
        super(connection, request, new WriteEventsCompleted(), receiver);
    }

}
