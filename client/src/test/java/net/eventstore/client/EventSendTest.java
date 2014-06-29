package net.eventstore.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import net.eventstore.client.model.Event;
import net.eventstore.client.model.Message;

import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.protobuf.TextFormat.ParseException;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;

import static org.junit.Assert.assertEquals;

@Log4j
@RunWith(JUnit4.class)
public class EventSendTest {

    private final static String HOSTNAME = "127.0.0.1";
    private final static int PORTNUMBER = 1113;
    private final static String STREAM_NAME = "teststream5";
    
    private final Semaphore processing = new Semaphore(0);
	
    private static EventStore es;
    private Message receivedMessage;
    
    @BeforeClass
    public static void init() throws IOException {
        es = new EventStore(InetAddress.getByName(HOSTNAME), PORTNUMBER);
        log.getParent().setLevel(Level.ALL);
        
    }
    
    @AfterClass
    public static void deinit() throws Exception {
        es.close();
    }
    
    private void setMessage(Message msg){
    	log.debug("release");
		receivedMessage = msg;
		release();
    }
    
    private void release(){
		processing.release();
    }
    
    private void acquire(){
    	try {
			processing.acquire();
			log.debug("acquire");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private void makeMessageTest(Object actual, Object expected){
    	makeMessageTest(actual, expected, true);
    }
    
    private void makeMessageTest(Object actual, Object expected, boolean nullMessage){
    	log.debug(String.format("Equals: %s", actual.equals(expected)));
    	assertEquals(actual, expected);
    	if (nullMessage){
        	receivedMessage = null;
    	}
    }
    
    @Test
    public void writeMultipleEvents() throws Exception, ParseException {
    	final int WRITE_NUMBER = 10;
    	long startTime = System.currentTimeMillis();
    	for (int i=0; i<WRITE_NUMBER; i++){
        	es.appendToStream(STREAM_NAME, new ResponseReceiver() {
    			@Override
    			public void onResponseReturn(Message msg) {
    				log.debug("Response received");
    			}

				@Override
				public void onErrorReturn(Exception ex) {
					log.debug("Error while writing mutiple events: " + ex.getMessage());
				}
    		}, generateEvents());
        }
    	long endTime = System.currentTimeMillis();
    	long duration = endTime - startTime;
    	log.debug(String.format("Writing finished. Number of writes=%s, duration=%s", WRITE_NUMBER, duration));
    }
    
    int i = 0;
    @Test
    public void writeMultipleEventsInThreads() throws Exception, ParseException {
    	log.getParent().setLevel(Level.ERROR);
    	final int TOTAL_WRITES = 1000;
    	final int EVENTS_PER_THREAD = 10;
    	final long startTime = System.currentTimeMillis();
    	i = 0;
    	
    	int numberOfThreads = TOTAL_WRITES/EVENTS_PER_THREAD;
    	Thread[] threads = new Thread[numberOfThreads];
    	for (int y=0; y<numberOfThreads; y++){
    		Runnable runnable = new Runnable(){
    			@Override
    			public void run() {
    		    	//while (i<WRITE_NUMBER){
    				for (int z=0; z<EVENTS_PER_THREAD; z++){
    		        	es.appendToStream(STREAM_NAME, new ResponseReceiver() {
    		        		
    		    			@Override
    		    			public void onResponseReturn(Message msg) {
    		    				log.debug("Response returned="+i);
    		    				i++;
    		    				if (i == (TOTAL_WRITES-100)){
    		    			    	processing.release();
    		    				}
    		    			}

							@Override
							public void onErrorReturn(Exception ex) {
    		    				i++;
    		    				if (i == TOTAL_WRITES){
    		    			    	processing.release();
    		    				}
							}
    		    		}, generateEvents());
    		        }
    			}
        	};
        	threads[y] = new Thread(runnable);
    	}
    	for (int y=0; y<numberOfThreads; y++){
    		threads[y].start();
    	}
    	processing.acquire();
    	long endTime = System.currentTimeMillis();
    	long duration = endTime - startTime;
    	log.getParent().setLevel(Level.ALL);
    	log.debug(String.format("Writing finished. Number of writes=%s, duration=%s, threads=%s", TOTAL_WRITES, duration, threads.length));
    }
    
    public static Event[] generateEvents() {
        List<Event> events = new ArrayList<>();
        {
            Event<JSONObject, String> e = new Event("simple-test-add-event");
            JSONObject data = new JSONObject();
            try {
                data.put("a", 1);
            } catch (JSONException ex) {
                log.fatal("Error while generating events", ex);
            }
            e.setData(data);
            e.setMetadata("some metadata");
            events.add(e);
        }
        
        return events.toArray(new Event[]{});
    }
    
}
