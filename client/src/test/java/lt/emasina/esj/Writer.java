package lt.emasina.esj;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import lt.emasina.esj.EventStore;
import lt.emasina.esj.ResponseReceiver;
import lt.emasina.esj.model.Event;
import lt.emasina.esj.model.Message;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.TextFormat.ParseException;

public class Writer {

    private static final Logger log = LoggerFactory.getLogger(Writer.class);

    private final static String HOSTNAME = "127.0.0.1";
    private final static int PORTNUMBER = 1113;
    private final static String STREAM_NAME = "teststream21";

    private final Semaphore processing = new Semaphore(0);
    private EventStore es;

    int successes = 0;
    int fails = 0;

    static public void main(String args[]) {
        Writer writer = new Writer();
        try {
            writer.init();
            log.debug("Initialized. Will now start sending events.");
            writer.writeMultipleEvents();
            writer.deinit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        log.debug("Writing finished. Number of writes={} ({} successful, {} failed), duration={}",
                        TOTAL_WRITES, successes, fails, duration);
    }

    public void init() throws IOException {
        es = new EventStore(InetAddress.getByName(HOSTNAME), PORTNUMBER);
    }

    public void deinit() throws Exception {
        es.close();
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
