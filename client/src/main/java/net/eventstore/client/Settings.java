package net.eventstore.client;


/**
 * Settings
 * @author Stasys
 */
public class Settings {

    private boolean requireMaster = false;

    /**
     * @return the requireMaster
     */
    public boolean isRequireMaster() {
        return requireMaster;
    }

    /**
     * @param requireMaster the requireMaster to set
     */
    public void setRequireMaster(boolean requireMaster) {
        this.requireMaster = requireMaster;
    }
    
}
