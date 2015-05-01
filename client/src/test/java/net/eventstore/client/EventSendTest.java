package net.eventstore.client;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import net.eventstore.client.model.Event;
import net.eventstore.client.model.Message;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.TextFormat.ParseException;

@RunWith(JUnit4.class)
public class EventSendTest {

    private static final Logger log = LoggerFactory
            .getLogger(EventSendTest.class);

    private final static String HOSTNAME = "127.0.0.1";
    private final static int PORTNUMBER = 1113;
    private final static String STREAM_NAME = "teststream23";

    private final Semaphore processing = new Semaphore(0);

    private static EventStore es;
    private Message receivedMessage;

    @BeforeClass
    public static void init() throws IOException {
        es = new EventStore(InetAddress.getByName(HOSTNAME), PORTNUMBER);

    }

    @AfterClass
    public static void deinit() throws Exception {
        es.close();
    }

    private void setMessage(Message msg) {
        log.debug("release");
        receivedMessage = msg;
        release();
    }

    private void release() {
        processing.release();
    }

    private void acquire() {
        try {
            processing.acquire();
            log.debug("acquire");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeMessageTest(Object actual, Object expected) {
        makeMessageTest(actual, expected, true);
    }

    private void makeMessageTest(Object actual, Object expected,
            boolean nullMessage) {
        log.debug(String.format("Equals: %s", actual.equals(expected)));
        assertEquals(actual, expected);
        if (nullMessage) {
            receivedMessage = null;
        }
    }

    int successes = 0;
    int fails = 0;

    @Test
    public void writeMultipleEvents() throws Exception, ParseException {
        final int TOTAL_WRITES = 100000;
        final long startTime = System.currentTimeMillis();
        successes = 0;
        fails = 0;

        for (int z = 0; z < TOTAL_WRITES; z++) {
            es.appendToStream(STREAM_NAME, new ResponseReceiver() {

                @Override
                public void onResponseReturn(Message msg) {
                    // log.debug("Response returned="+i);
                    successes++;
                    if ((successes + fails) == (TOTAL_WRITES)) {
                        processing.release();
                    }
                }

                @Override
                public void onErrorReturn(Exception ex) {
                    fails++;
                    if ((successes + fails) == TOTAL_WRITES) {
                        processing.release();
                    }
                }
            }, generateEvents());
        }

        processing.acquire();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.debug(String
                .format("Writing finished. Number of writes=%s (%s successful, %s failed), duration=%s",
                        TOTAL_WRITES, successes, fails, duration));
    }

    public static Event[] generateEvents() {
        List<Event> events = new ArrayList<>();
        {
            Event<JSONObject, String> e = new Event("simple-test-add-event");
            JSONObject data = new JSONObject();
            try {
                data.put("a", 1);
            } catch (JSONException ex) {
                log.error("Error while generating events", ex);
            }
            e.setData(data);
            e.setMetadata("some metadata");
            events.add(e);
        }

        return events.toArray(new Event[] {});
    }

}
