package lt.emasina.esj;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lt.emasina.esj.message.ClientMessageDtos.OperationResult;
import lt.emasina.esj.message.ClientMessageDtos.ReadEventCompleted.ReadEventResult;
import lt.emasina.esj.message.ReadEventCompleted;
import lt.emasina.esj.message.WriteEventsCompleted;
import lt.emasina.esj.model.Event;
import lt.emasina.esj.model.Message;
import lt.emasina.esj.model.converter.ByteArrayToByteStringConverter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventStoreIT {

    private static final byte[] EVENT_1 = "{ \"a\": 1 }".getBytes();

    private static final byte[] EVENT_2 = "{ \"b\": \"B\" }".getBytes();

    private static final byte[] EVENT_3 = "{ \"c\": true }".getBytes();

    private static final byte[] META = "{ \"ip\": \"127.0.0.1\" }".getBytes();

    private final static String HOSTNAME = "127.0.0.1";

    private final static int PORTNUMBER = 1113;

    private final static ByteArrayToByteStringConverter CONVERTER = new ByteArrayToByteStringConverter(
            true);

    private EventStore testee;

    @Before
    public void setup() throws IOException {
        testee = new EventStore(InetAddress.getByName(HOSTNAME), PORTNUMBER);
    }

    @After
    public void teardown() {
        testee = null;
    }

    @Test
    public void testAppendToStream_Success() {

        // PREPARE
        String streamId = "testAppendToStream_Success";
        UUID id = UUID.randomUUID();
        Event<byte[], byte[]> event1 = new Event<byte[], byte[]>(id, "MyEvent",
                EVENT_1, CONVERTER, META, CONVERTER);
        Event<byte[], byte[]> event2 = new Event<byte[], byte[]>(id, "MyEvent",
                EVENT_2, CONVERTER, META, CONVERTER);

        // TEST
        testee.appendToStream(streamId, new ResponseReceiver() {
            @Override
            public void onResponseReturn(Message msg) {
                // VERIFY
                WriteEventsCompleted completed = (WriteEventsCompleted) msg;
                assertThat(completed.getResult()).isEqualTo(
                        OperationResult.Success);
                assertThat(completed.getFirstEventNumber()).isEqualTo(0);
                assertThat(completed.getLastEventNumber()).isEqualTo(1);
            }

            @Override
            public void onErrorReturn(Exception ex) {
                failWithUnexpectedException(ex);
            }
        }, event1, event2);

    }

    @Test
    public void testAppendToStream_WrongExpectedVersion() {

        // PREPARE
        String streamId = "testAppendToStream_WrongExpectedVersion";
        List<Event> events = new ArrayList<Event>();
        events.add(new Event<byte[], byte[]>(UUID.randomUUID(), "MyEvent",
                EVENT_1, CONVERTER, META, CONVERTER));
        int expectedVersion = 123;

        // TEST
        testee.appendToStream(streamId, expectedVersion, new ResponseReceiver() {
            @Override
            public void onResponseReturn(Message msg) {
                // VERIFY
                WriteEventsCompleted completed = (WriteEventsCompleted) msg;
                assertThat(completed.getResult()).isEqualTo(
                        OperationResult.WrongExpectedVersion);
            }

            @Override
            public void onErrorReturn(Exception ex) {
                failWithUnexpectedException(ex);
            }
        }, events);

    }
    
    @Test
    public void testReadFromStream_Success() {

        // PREPARE
        String streamId = "testReadFromStream_Success";
        UUID id = UUID.randomUUID();
        Event<byte[], byte[]> event = new Event<byte[], byte[]>(id, "MyEvent",
                EVENT_1, CONVERTER, META, CONVERTER);
        testee.appendToStream(streamId, new ResponseReceiver() {
            @Override
            public void onResponseReturn(Message msg) {
                WriteEventsCompleted completed = (WriteEventsCompleted) msg;
                assertThat(completed.getResult()).isEqualTo(
                        OperationResult.Success);
            }

            @Override
            public void onErrorReturn(Exception ex) {
                failWithUnexpectedException(ex);
            }
        }, event);

        // TEST
        testee.readFromStream(streamId, 0, new ResponseReceiver() {
            @Override
            public void onResponseReturn(Message msg) {
                // VERIFY
                ReadEventCompleted completed = (ReadEventCompleted) msg;
                assertThat(completed.getResult()).isEqualTo(
                        ReadEventResult.Success);
                assertThat(completed.getResponseData().toByteArray())
                        .isEqualTo(EVENT_1);
                assertThat(completed.getResponseMeta().toByteArray())
                        .isEqualTo(META);
            }

            @Override
            public void onErrorReturn(Exception ex) {
                failWithUnexpectedException(ex);
            }
        });

    }

    @Test
    public void testReadFromStream_NoStream() {

        testee.readFromStream("non-existing-stream", 0, new ResponseReceiver() {
            @Override
            public void onResponseReturn(Message msg) {
                ReadEventCompleted completed = (ReadEventCompleted) msg;
                assertThat(completed.getResult()).isEqualTo(
                        ReadEventResult.NoStream);
            }

            @Override
            public void onErrorReturn(Exception ex) {
                failWithUnexpectedException(ex);
            }
        });

    }

    private void failWithUnexpectedException(Exception ex) {
        throw new RuntimeException("No exception was expected, but got one", ex);
    }

}
