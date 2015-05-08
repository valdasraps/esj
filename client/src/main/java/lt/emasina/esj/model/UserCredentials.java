package lt.emasina.esj.model;


/**
 * UserCredentials
 *
 * @author Stasys
 */
public class UserCredentials {

    private final String login;
    private final String password;
    
    /**
     * Constructor with required values.
     * 
     * @param login
     * @param password
     */
    public UserCredentials(String login, String password) {
        super();
        this.login = login;
        this.password = password;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof UserCredentials))
            return false;
        UserCredentials other = (UserCredentials) obj;
        if (login == null) {
            if (other.login != null)
                return false;
        } else if (!login.equals(other.login))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        return true;
    }

}
