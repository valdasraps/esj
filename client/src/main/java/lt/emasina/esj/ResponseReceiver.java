package lt.emasina.esj;

import lt.emasina.esj.model.Message;

public interface ResponseReceiver {

	public void onResponseReturn(Message msg);
	
	public void onErrorReturn(Exception ex);
	
}
