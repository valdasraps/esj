package net.eventstore.client;

import net.eventstore.client.model.RequestMultipleResponsesOperation;

public interface MultipleResponsesReceiver extends ResponseReceiver {
	
	public void onReceiverCreated(RequestMultipleResponsesOperation op);
	
}
