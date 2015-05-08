package lt.emasina.esj.operation;

import lt.emasina.esj.ResponseReceiver;
import lt.emasina.esj.model.RequestResponseOperation;
import lt.emasina.esj.message.ReadAllEventsForward;
import lt.emasina.esj.message.ReadAllEventsForwardCompleted;
import lt.emasina.esj.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class ReadAllEventsForwardOperation extends RequestResponseOperation<ReadAllEventsForward, ReadAllEventsForwardCompleted> {

    public ReadAllEventsForwardOperation(TcpConnection connection, ReadAllEventsForward request, ResponseReceiver receiver) {
        super(connection, request, new ReadAllEventsForwardCompleted(), receiver);
    }

}
