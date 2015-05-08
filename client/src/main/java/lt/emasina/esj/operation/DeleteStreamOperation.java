package lt.emasina.esj.operation;

import lt.emasina.esj.ResponseReceiver;
import lt.emasina.esj.model.RequestResponseOperation;
import lt.emasina.esj.message.DeleteStream;
import lt.emasina.esj.message.DeleteStreamCompleted;
import lt.emasina.esj.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class DeleteStreamOperation extends RequestResponseOperation<DeleteStream, DeleteStreamCompleted> {

    public DeleteStreamOperation(TcpConnection connection, DeleteStream request, ResponseReceiver receiver) {
        super(connection, request, new DeleteStreamCompleted(), receiver);
    }

}
