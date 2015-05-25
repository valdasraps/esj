package lt.emasina.esj;

import lt.emasina.esj.model.Message;

/**
 * Helper class to make threaded testing a little easier.
 */
public class TestResponseReceiver implements ResponseReceiver {

    private volatile boolean resultAvailable = false;

    private Exception ex;
    
    private Message msg;
    
    @Override
    public void onResponseReturn(Message msg) {
        try {
            this.msg = msg;
        } finally {
            resultAvailable = true;
        }
    }

    @Override
    public void onErrorReturn(Exception ex) {
        try {
            this.ex = ex;
        } finally {
            resultAvailable = true;
        }
    }

    public void waitForResult() {
        while (!resultAvailable) {
            sleep(10);
        }
        sleep(100);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the exception.
     * 
     * @return Exception or <code>null</code>.
     */
    public Exception getException() {
        return ex;
    }

    /**
     * Returns the message.
     * 
     * @return Message or <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public <T extends Message> T getMessage() {
        return (T) msg;
    }

}
