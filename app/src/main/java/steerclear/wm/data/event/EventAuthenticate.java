package steerclear.wm.data.event;

/**
 * Created by Miles Peele on 8/20/2015.
 */
public class EventAuthenticate {

    public String username;
    public String password;
    public String phone;

    public EventAuthenticate(String username, String password, String phone) {
        this.username = username;
        this.password = password;
        this.phone = phone;
    }

    public EventAuthenticate(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
