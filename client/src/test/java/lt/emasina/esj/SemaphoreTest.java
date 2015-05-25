package lt.emasina.esj;

import java.util.concurrent.Semaphore;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SemaphoreTest class
 * @author Stasys
 */
public class SemaphoreTest {
    
    private static final Logger log = LoggerFactory.getLogger(SemaphoreTest.class);
    
    @Test
    public void semTest() throws InterruptedException {
        final Semaphore s = new Semaphore(0);
        
        log.info("Blocked 1: {}", s.hasQueuedThreads());
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("Acquiring");
                    s.acquire();
                } catch (InterruptedException ex) {}
                log.info("Exiting");
            }
        };
        Thread t = new Thread(r);
        t.start();
        
        Thread.sleep(500);
        
        log.info("Blocked 2: {}", s.hasQueuedThreads());
        
        t.interrupt();
        Thread.sleep(500);
        
        log.info("Blocked 3: {}", s.hasQueuedThreads());
        
    }
    
}
