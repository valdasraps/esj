package lt.emasina.esj.model;

import lt.emasina.esj.tcp.TcpPackage;

/**
 * Interface ResponseOperation
 *
 * @author Stasys
 */
public abstract class ResponseOperation {

    public boolean hasSingleResponse = true;

    public abstract void setResponsePackage(TcpPackage pckg);

}
