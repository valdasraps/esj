package lt.emasina.esj.operation;

import lt.emasina.esj.message.HeartBeat;
import lt.emasina.esj.message.HeartBeatResponse;
import lt.emasina.esj.model.RequestResponseOperation;
import lt.emasina.esj.tcp.TcpConnection;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class HeartBeatOperation extends RequestResponseOperation<HeartBeat, HeartBeatResponse> {

    public HeartBeatOperation(TcpConnection connection) {
        super(connection, new HeartBeat(), new HeartBeatResponse(), null);
    }

}
