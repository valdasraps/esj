package lt.emasina.esj.operation;

import lt.emasina.esj.ResponseReceiver;
import lt.emasina.esj.model.RequestResponseOperation;
import lt.emasina.esj.message.ReadEvent;
import lt.emasina.esj.message.ReadEventCompleted;
import lt.emasina.esj.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class ReadEventFromStreamOperation extends RequestResponseOperation<ReadEvent, ReadEventCompleted> {

    public ReadEventFromStreamOperation(TcpConnection connection, ReadEvent request, ResponseReceiver receiver) {
        super(connection, request, new ReadEventCompleted(), receiver);
    }

}
