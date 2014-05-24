package net.eventstore.client.operation;

import net.eventstore.client.message.HeartBeat;
import net.eventstore.client.message.HeartBeatResponse;
import net.eventstore.client.model.RequestResponseOperation;
import net.eventstore.client.tcp.TcpConnection;

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
