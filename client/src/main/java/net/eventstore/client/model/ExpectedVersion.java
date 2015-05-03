package net.eventstore.client.model;


/**
 * ExpectedVersion
 *
 * @author Stasys
 */
public enum ExpectedVersion {

    /// This write should not conflict with anything and should always succeed.
    Any(-2),
    /// The stream being written to should not yet exist. If it does exist treat that as a concurrency problem.
    NoStream(-1),
    /// The stream should exist and should be empty. If it does not exist or is not empty treat that as a concurrency problem.
    EmptyStream(-1);

    private final int mask;

    private ExpectedVersion(int mask) {
        this.mask = mask;
    }

    /**
     * @return the mask
     */
    public int getMask() {
        return mask;
    }

}
