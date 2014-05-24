package net.eventstore.client.operation;

import java.util.UUID;
import net.eventstore.client.message.HeartBeatResponse;
import net.eventstore.client.model.RequestOperation;
import net.eventstore.client.tcp.TcpConnection;
import net.eventstore.client.tcp.TcpPackage;

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
