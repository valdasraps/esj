package net.eventstore.client.message;

import net.eventstore.client.model.Message;
import net.eventstore.client.tcp.TcpCommand;

/**
 * HeartBeatResponse class
 *
 * @author Stasys
 */
public class HeartBeatResponse extends Message {

    public HeartBeatResponse() {
        super(TcpCommand.HeartbeatResponseCommand);
    }

}
