package net.eventstore.client;

import lombok.Getter;
import lombok.Setter;

/**
 * Settings
 * @author Stasys
 */
@Setter @Getter
public class Settings {

    private boolean requireMaster = false;
    
}
