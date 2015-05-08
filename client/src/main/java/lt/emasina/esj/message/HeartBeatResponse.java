package lt.emasina.esj.message;

import lt.emasina.esj.model.Message;
import lt.emasina.esj.tcp.TcpCommand;

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
