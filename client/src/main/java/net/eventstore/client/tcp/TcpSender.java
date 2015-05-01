package net.eventstore.client.tcp;

import java.io.IOException;
import java.io.OutputStream;

import net.eventstore.client.model.RequestOperation;
import net.eventstore.client.model.ResponseOperation;
import net.eventstore.client.util.Bytes;

import org.apache.log4j.Logger;

/**
 * TcpSender class
 * @author Stasys
 */
public class TcpSender implements Runnable {

    private static final Logger log = Logger.getLogger(TcpSender.class);

    private final OutputStream out;
    private final TcpSocketManager manager;
    
    /**
     * Constructor with mandatory data.
     * 
     * @param out
     * @param manager
     */
    public TcpSender(OutputStream out, TcpSocketManager manager) {
        super();
        this.out = out;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {

            // Wait for the manager to lock itself
            while (!manager.getRunning().hasQueuedThreads()) Thread.sleep(10);
            
            while (manager.getRunning().hasQueuedThreads()) {
                
                RequestOperation op = manager.getSending().take();

                boolean waitingResponse = false;
                if (op instanceof ResponseOperation) {
                    ResponseOperation rop = (ResponseOperation) op;
                    manager.getReceiving().put(op.getCorrelationId(), rop);
                    waitingResponse = true;
                }

                TcpPackage pckg = op.getRequestPackage();

                byte[][] frames = TcpFramer.frame(pckg.AsByteArray());

                if (log.isDebugEnabled()) {
                    log.debug(String.format("Sending... %s", Bytes.debugString(frames)));
                }

                // Send package
                for (byte[] b : frames) out.write(b);
                out.flush();

                if (!waitingResponse) {
                    op.doneProcessing();
                }

            }
            
        } catch (InterruptedException ex) {
            // Ignore
        } catch (IOException ex) {
            if (manager.getRunning().hasQueuedThreads()) {
                log.error("Error in sender", ex);
                manager.getRunning().release();
            }
        }   
    }
    
}
