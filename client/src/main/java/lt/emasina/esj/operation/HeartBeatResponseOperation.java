package lt.emasina.esj.operation;

import java.util.UUID;
import lt.emasina.esj.message.HeartBeatResponse;
import lt.emasina.esj.model.RequestOperation;
import lt.emasina.esj.tcp.TcpConnection;
import lt.emasina.esj.tcp.TcpPackage;

/**
 * AppendToStreamOperation
 *
 * @author Stasys
 */
public class HeartBeatResponseOperation extends RequestOperation<HeartBeatResponse> {

    public HeartBeatResponseOperation(TcpConnection connection, UUID correlationId) {
        super(connection, new HeartBeatResponse());
        this.getRequest().setCorrelationId(correlationId);
    }

    @Override
    public void setResponsePackage(TcpPackage pckg) {

    }

}
