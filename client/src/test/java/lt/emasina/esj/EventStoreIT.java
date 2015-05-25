package lt.emasina.esj;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lt.emasina.esj.message.ClientMessageDtos.EventRecord;
import lt.emasina.esj.message.ClientMessageDtos.OperationResult;
import lt.emasina.esj.message.ClientMessageDtos.ReadEventCompleted.ReadEventResult;
import lt.emasina.esj.message.ClientMessageDtos.ReadStreamEventsCompleted.ReadStreamResult;
import lt.emasina.esj.message.ClientMessageDtos.ResolvedIndexedEvent;
import lt.emasina.esj.message.DeleteStreamCompleted;
import lt.emasina.esj.message.ReadAllEventsForwardCompleted;
import lt.emasina.esj.message.ReadEventCompleted;
import lt.emasina.esj.message.WriteEventsCompleted;
import lt.emasina.esj.model.Event;
import lt.emasina.esj.model.converter.ByteArrayToByteStringConverter;
import lt.emasina.esj.util.Bytes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.ByteString;

/**
 * Tests the {@link EventStore} class.
 */
@SuppressWarnings("rawtypes")
public class EventStoreIT {

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
        final String streamId = "testAppendToStream_Success";
        final List<Event> events = createEvents(2);

        // TEST
        TestResponseReceiver rr = new TestResponseReceiver();
        testee.appendToStream(streamId, rr, events);
        rr.waitForResult();

        // VERIFY
        assertThat(rr.getException()).isNull();
        WriteEventsCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(OperationResult.Success);
        assertThat(completed.getFirstEventNumber()).isEqualTo(0);
        assertThat(completed.getLastEventNumber()).isEqualTo(events.size() - 1);

    }

    @Test
    public void testAppendToStream_WrongExpectedVersion() {

        // PREPARE
        String streamId = "testAppendToStream_WrongExpectedVersion";
        List<Event> events = createEvents(1);
        int expectedVersion = 123;

        // TEST
        TestResponseReceiver rr = new TestResponseReceiver();
        testee.appendToStream(streamId, expectedVersion, rr, events);
        rr.waitForResult();

        // VERIFY
        assertThat(rr.getException()).isNull();
        WriteEventsCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(
                OperationResult.WrongExpectedVersion);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReadFromStream_Success() {

        // PREPARE
        final String streamId = "testReadFromStream_Success";
        final Event<byte[], byte[]> event = createStreamWithEvents(streamId, 1)
                .get(0);

        // TEST
        TestResponseReceiver rr = new TestResponseReceiver();
        testee.readFromStream(streamId, 0, rr);
        rr.waitForResult();

        // VERIFY
        assertThat(rr.getException()).isNull();
        ReadEventCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(ReadEventResult.Success);
        assertThat(completed.getResponseData().toByteArray()).isEqualTo(
                event.getData());
        assertThat(completed.getResponseMeta().toByteArray()).isEqualTo(
                event.getMetadata());

    }

    @Test
    public void testReadAllEventsForward_Success() {

        // PREPARE
        final String streamId = "testReadAllEventsForward_Success";
        final List<Event> events = createStreamWithEvents(streamId, 9);

        // TEST first slice
        final int maxCount = 5;
        TestResponseReceiver rr = new TestResponseReceiver();
        testee.readAllEventsForward(streamId, 0, maxCount, rr);
        rr.waitForResult();

        // VERIFY first slice
        assertThat(rr.getException()).isNull();
        ReadAllEventsForwardCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(ReadStreamResult.Success);
        assertThat(completed.getNexteventNr()).isEqualTo(maxCount);
        assertThat(completed.getEventsCount()).isEqualTo(maxCount);
        assertThat(completed.isEndOfStream()).isFalse();
        assertThat(completed.getEventList()).hasSize(maxCount);
        for (int i = 0; i < maxCount; i++) {
            ResolvedIndexedEvent indexedEvent = completed.getEventList().get(i);
            EventRecord event = indexedEvent.getEvent();
            assertThat(uuid(event.getEventId())).isEqualTo(
                    events.get(i).getId());
        }

        // TEST second slice
        rr = new TestResponseReceiver();
        final int rest = 4;
        testee.readAllEventsForward(streamId, maxCount, rest + 1, rr);
        rr.waitForResult();

        // VERIFY second slice
        assertThat(rr.getException()).isNull();
        completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(ReadStreamResult.Success);
        assertThat(completed.getNexteventNr()).isEqualTo(maxCount + rest);
        assertThat(completed.getEventsCount()).isEqualTo(rest);
        assertThat(completed.isEndOfStream()).isFalse();
        assertThat(completed.getEventList()).hasSize(rest);
        for (int i = 0; i < rest; i++) {
            ResolvedIndexedEvent indexedEvent = completed.getEventList().get(i);
            EventRecord event = indexedEvent.getEvent();
            assertThat(uuid(event.getEventId())).isEqualTo(
                    events.get(i).getId());
        }

    }

    @Test
    public void testReadFromStream_NoStream() {

        // TEST
        TestResponseReceiver rr = new TestResponseReceiver();
        testee.readFromStream("non-existing-stream", 0, rr);
        rr.waitForResult();

        // VERIFY
        assertThat(rr.getException()).isNull();
        ReadEventCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(ReadEventResult.NoStream);

    }

    @Test
    public void testDeleteStream_Success() {

        // PREPARE
        String streamId = "testDeleteStream_Success";
        createStreamWithEvents(streamId, 1);

        // TEST
        TestResponseReceiver rr = new TestResponseReceiver();
        testee.deleteStream(streamId, EventStore.VERSION_ANY, true, rr);
        rr.waitForResult();

        // VERIFY
        assertThat(rr.getException()).isNull();
        DeleteStreamCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(OperationResult.Success);

    }

    @Test
    public void testDeleteStream_StreamDeleted() {

        // PREPARE
        String streamId = "testDeleteStream_StreamDeleted";
        createStreamWithEvents(streamId, 1);
        deleteStream(streamId);

        // TEST
        TestResponseReceiver rr = new TestResponseReceiver();
        testee.deleteStream(streamId, EventStore.VERSION_ANY, true, rr);
        rr.waitForResult();

        // VERIFY
        assertThat(rr.getException()).isNull();
        DeleteStreamCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(
                OperationResult.StreamDeleted);

    }

    private List<Event> createStreamWithEvents(final String streamId,
            final int noOfEvents) {

        final List<Event> events = createEvents(noOfEvents);

        TestResponseReceiver rr = new TestResponseReceiver();
        testee.appendToStream(streamId, rr, events);
        rr.waitForResult();

        assertThat(rr.getException()).isNull();
        WriteEventsCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(OperationResult.Success);
        assertThat(completed.getFirstEventNumber()).isEqualTo(0);
        assertThat(completed.getLastEventNumber()).isEqualTo(events.size() - 1);

        return events;
    }

    private List<Event> createEvents(final int noOfEvents) {
        final List<Event> events = new ArrayList<Event>();
        for (int i = 0; i < noOfEvents; i++) {
            byte[] EVENT = ("{ \"a\": " + i + " }").getBytes();
            byte[] META = "{ \"ip\": \"127.0.0.1\" }".getBytes();
            events.add(new Event<byte[], byte[]>(UUID.randomUUID(), "MyEvent",
                    EVENT, CONVERTER, META, CONVERTER));
        }
        return events;
    }

    private void deleteStream(String streamId) {

        TestResponseReceiver rr = new TestResponseReceiver();
        testee.deleteStream(streamId, EventStore.VERSION_ANY, true, rr);
        rr.waitForResult();

        assertThat(rr.getException()).isNull();
        DeleteStreamCompleted completed = rr.getMessage();
        assertThat(completed.getResult()).isEqualTo(OperationResult.Success);

    }

    private UUID uuid(ByteString id) {
        return Bytes.fromBytes(id.toByteArray());
    }

}
