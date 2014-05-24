package net.eventstore.client.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * UserCredentials
 *
 * @author Stasys
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class UserCredentials {

    private final String login;
    private final String password;

}
