package lt.emasina.esj.model;

import java.util.UUID;

import lt.emasina.esj.Settings;
import lt.emasina.esj.tcp.TcpCommand;
import lt.emasina.esj.tcp.TcpFlag;
import lt.emasina.esj.tcp.TcpPackage;

import com.google.protobuf.GeneratedMessage;

/**
 * Message
 *
 * @author Stasys
 */
public abstract class Message {

    // General properties
    protected final TcpCommand command;

    protected UUID correlationId;

    // Request properties
    protected UserCredentials user;

    // Response properties
    protected String message;

    /**
     * Constructor with required arguments.
     * 
     * @param command
     */
    public Message(TcpCommand command) {
        super();
        this.command = command;
    }
    
    public Message(TcpCommand command, UserCredentials user) {
        this.command = command;
        this.correlationId = UUID.randomUUID();
        this.user = user;
    }

    public GeneratedMessage getDto(Settings settings) {
        return null;
    }

    public void parse(byte[] data) throws ParseException {

    }

    public final TcpPackage getTcpPackage(Settings settings) {
        TcpFlag flag = (user == null ? TcpFlag.None : TcpFlag.Authenticated);
        GeneratedMessage dto = getDto(settings);
        return new TcpPackage(command, flag, correlationId, user, dto == null ? new byte[]{} : dto.toByteArray());
    }

    public final void parse(TcpPackage pckg) throws ParseException {
        parse(null, pckg);
    }

    public final void parse(Message request, TcpPackage pckg) throws ParseException {

        if (!command.equals(pckg.getCommand())) {
            throw new ParseException("Command does not match: expected %s got %s", command, pckg.getCommand());
        }

        if (request != null && !request.getCorrelationId().equals(pckg.getCorrelationId())) {
            throw new ParseException("Correlation ID does not match: expected %s got %s", request.getCorrelationId(), pckg.getCorrelationId());
        } else {
            this.correlationId = pckg.getCorrelationId();
        }

        this.user = pckg.getUser();
        parse(pckg.getData());
    }

    protected String toResultInfo() {
        return "Unimplemented!";
    }

    /**
     * @return the correlationId
     */
    public UUID getCorrelationId() {
        return correlationId;
    }

    /**
     * @param correlationId the correlationId to set
     */
    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * @return the user
     */
    public UserCredentials getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserCredentials user) {
        this.user = user;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the command
     */
    public TcpCommand getCommand() {
        return command;
    }

}
