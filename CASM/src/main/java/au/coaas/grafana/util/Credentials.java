package au.coaas.grafana.util;

import java.io.Serializable;

/**
 * @author shakthi
 */
public class Credentials implements Serializable {
    private String username;
    private String password;

    public String getUserName() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
