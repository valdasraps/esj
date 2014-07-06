package net.eventstore.client.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.eventstore.client.tcp.TcpCommand;
import net.eventstore.client.tcp.TcpPackage;

/**
 * ExceptionMessage
 *
 * @author Stasys
 */
@Getter
@RequiredArgsConstructor
public class ExceptionMessage extends Exception {

    private final TcpCommand command;
    private final String message;

    public ExceptionMessage(TcpPackage pckg) {
        this.command = pckg.getCommand();
        this.message = new String(pckg.getData());
    }

}
