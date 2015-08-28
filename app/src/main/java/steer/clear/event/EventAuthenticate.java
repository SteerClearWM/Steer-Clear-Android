package steer.clear.event;

/**
 * Created by Miles Peele on 8/20/2015.
 */
public class EventAuthenticate {

    public String username;
    public String password;
    public String phone;
    public boolean registered;

    public EventAuthenticate(String username, String password, String phone, boolean registered) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.registered = registered;
    }
}
