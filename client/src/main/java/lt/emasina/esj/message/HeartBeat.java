package lt.emasina.esj.message;

import lt.emasina.esj.model.Message;
import lt.emasina.esj.tcp.TcpCommand;

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
