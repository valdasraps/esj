package net.eventstore.client.model;

import net.eventstore.client.tcp.TcpPackage;

/**
 * Interface ResponseOperation
 *
 * @author Stasys
 */
public abstract class ResponseOperation {

    public boolean hasSingleResponse = true;

    public abstract void setResponsePackage(TcpPackage pckg);

}
