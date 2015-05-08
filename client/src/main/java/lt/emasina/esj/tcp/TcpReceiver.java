package lt.emasina.esj.tcp;

import static lt.emasina.esj.tcp.TcpConnection.HEADER_SIZE;

import java.io.IOException;
import java.io.InputStream;

import lt.emasina.esj.model.ParseException;
import lt.emasina.esj.model.ResponseOperation;
import lt.emasina.esj.util.Bytes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TcpReceiver class
 * @author Stasys
 */
public class TcpReceiver implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TcpReceiver.class);
    
    private final InputStream in;
    private final TcpSocketManager manager;
    
    /**
     * Constructor with mandatory data.
     * 
     * @param in
     * @param manager
     */
    public TcpReceiver(InputStream in, TcpSocketManager manager) {
        super();
        this.in = in;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            
            // Wait for the manager to lock itself
            while (!manager.getRunning().hasQueuedThreads()) Thread.sleep(10);
        
            while (manager.getRunning().hasQueuedThreads()) {

                // Receive header
                byte[] header = new byte[HEADER_SIZE];
                int headerLen = in.read(header, 0, HEADER_SIZE);

                if (HEADER_SIZE != headerLen) {
                    throw new IOException(String.format("Wrong header size received: expected %d, got %d", HEADER_SIZE, headerLen));
                }

                int expectLen = Bytes.toInt(header[0]) + Bytes.toInt(header[1]) + Bytes.toInt(header[2]) + Bytes.toInt(header[3]);

                // Receive package
                byte[] result = new byte[HEADER_SIZE + expectLen];
                System.arraycopy(header, 0, result, 0, HEADER_SIZE);
                int dataLen = in.read(result, HEADER_SIZE, expectLen);
                if (expectLen != dataLen) {
                    throw new IOException(String.format("Wrong data size received: expected %d, got %d. Received: %s", expectLen, dataLen, Bytes.debugString(result)));
                }

                if (log.isDebugEnabled()) {
                    log.debug("Received... {}", Bytes.debugString(result));
                }

                TcpPackage pckg = TcpPackage.fromBytes(TcpFramer.unframe(result));
                ResponseOperation op = manager.getReceiving().get(pckg.getCorrelationId());
                if (op != null){
                	if (op.hasSingleResponse == true){
                    	manager.getReceiving().remove(pckg.getCorrelationId());
                    }
                	else {
                		TcpCommand cmd = pckg.getCommand();
                		if (cmd.equals(TcpCommand.SubscriptionDropped)){
                			manager.getReceiving().remove(pckg.getCorrelationId());
                		}
                	}
                }
                if (log.isDebugEnabled()) {
                	log.debug("Remaining operations: {}", manager.getReceiving().size());
                }
                if (op == null) {
                    switch (pckg.getCommand()) {
                        case HeartbeatRequestCommand:
                            manager.respondHeartBeat(pckg.getCorrelationId());
                            break;
                        case CreateChunk:
                            // Silently ignore this (don't know what to do...)
                            break;
                        default:
                            throw new IOException(String.format("Have not found operation to return data to: %s, operation %s", 
                                                                pckg.getCorrelationId(),
                                                                pckg.getCommand()));
                    }
                } else {
                    op.setResponsePackage(pckg);
                }

            }

        } catch (InterruptedException ex) {
            // Ignore
        } catch (IOException | ParseException ex) {
            if (manager.getRunning().hasQueuedThreads()) {
                log.error("Error in receiver", ex);
                manager.getRunning().release();
            }
        }   
    }
    
}
