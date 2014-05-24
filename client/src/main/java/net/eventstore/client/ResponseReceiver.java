package net.eventstore.client;

import net.eventstore.client.model.Message;

public interface ResponseReceiver {

	public void onResponseReturn(Message msg);
	
	public void onErrorReturn(Exception ex);
	
}
