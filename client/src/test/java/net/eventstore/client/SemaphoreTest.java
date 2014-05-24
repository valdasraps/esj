package net.eventstore.client;

import java.util.concurrent.Semaphore;
import lombok.extern.log4j.Log4j;
import org.junit.Test;

/**
 * SemaphoreTest class
 * @author Stasys
 */
@Log4j
public class SemaphoreTest {
    
    @Test
    public void semTest() throws InterruptedException {
        final Semaphore s = new Semaphore(0);
        
        log.info(String.format("Blocked 1: %s", s.hasQueuedThreads()));
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    log.info(String.format("Acquiring"));
                    s.acquire();
                } catch (InterruptedException ex) {}
                log.info(String.format("Exiting"));
            }
        };
        Thread t = new Thread(r);
        t.start();
        
        Thread.sleep(500);
        
        log.info(String.format("Blocked 2: %s", s.hasQueuedThreads()));
        
        t.interrupt();
        Thread.sleep(500);
        
        log.info(String.format("Blocked 3: %s", s.hasQueuedThreads()));
        
    }
    
}
