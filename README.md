esj
===

Java client for EventStore (http://geteventstore.com)

<h3><a name="about" class="anchor" href="#about"><span class="octicon octicon-link"></span></a>About</h3>
<p>Event Store Java client. Designed to help Java programs easily communicate with Event Store database.</p>

<h3><a name="functionality" class="anchor" href="#functionality"><span class="octicon octicon-link"></span></a>Functionality</h3>
<p>Java client functionality:</p>
<ul>
	<li>Append event to stream</li>
	<li>Read from stream</li>
	<li>Subscribe to stream</li>
	<li>Delete stream</li>
	<li>Read events from stream</li>
</ul>

<h3><a name="code-example" class="anchor" href="#code-example"><span class="octicon octicon-link"></span></a>Code example</h3>
<p>Append to stream example:</p>
<pre><code>
import net.eventstore.client.EventStore;
import net.eventstore.client.model.Message;
...
EventStore es = new EventStore(InetAddress.getByName(HOSTNAME), PORTNUMBER);
...
es.appendToStream(STREAM_NAME, new ResponseReceiver() {
	@Override
	public void onResponseReturn(Message msg) {
		// do something
	},
	@Override
	public void onErrorReturn(Exception ex){
		// do something
	}
},EVENT);
...
es.close();

</code></pre>

<p>or in Java 7 manner:</p>

<pre><code>
import net.eventstore.client.EventStore;
import net.eventstore.client.model.Message;
...
try (EventStore es = new EventStore(InetAddress.getByName(HOSTNAME), PORTNUMBER)) {
    ...
    es.appendToStream(STREAM_NAME, new ResponseReceiver() {
	@Override
	public void onResponseReturn(Message msg) {
		// do something
	},
	@Override
	public void onErrorReturn(Exception ex){
		// do something
	}
    },EVENT);
    ...
}

</code></pre>
