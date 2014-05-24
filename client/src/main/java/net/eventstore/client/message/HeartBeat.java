package net.eventstore.client.message;

import net.eventstore.client.model.Message;
import net.eventstore.client.tcp.TcpCommand;

/**
 * HeartBeat class
 *
 * @author Stasys
 */
public class HeartBeat extends Message {

    public HeartBeat() {
        super(TcpCommand.HeartbeatRequestCommand, null);
    }

}
