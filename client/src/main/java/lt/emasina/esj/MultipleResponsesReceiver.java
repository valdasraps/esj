package lt.emasina.esj;

import lt.emasina.esj.model.RequestMultipleResponsesOperation;

public interface MultipleResponsesReceiver extends ResponseReceiver {
	
	public void onReceiverCreated(RequestMultipleResponsesOperation op);
	
}
