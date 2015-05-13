esj
===

Java client for EventStore (http://geteventstore.com)

[![Build Status](https://fuin-org.ci.cloudbees.com/job/esj/badge/icon)](https://fuin-org.ci.cloudbees.com/job/esj/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/lt.emasina/esj-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/lt.emasina/esj-client/)
[![LGPLv3 License](http://img.shields.io/badge/license-LGPLv3-blue.svg)](https://www.gnu.org/licenses/lgpl.html)
[![Java Development Kit 1.7](https://img.shields.io/badge/JDK-1.7-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)

<a href="http://valdasraps.github.io/esj/">Project website</a>

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
import lt.emasina.esj.EventStore;
import lt.emasina.esj.model.Message;
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
import lt.emasina.esj.EventStore;
import lt.emasina.esj.model.Message;
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
