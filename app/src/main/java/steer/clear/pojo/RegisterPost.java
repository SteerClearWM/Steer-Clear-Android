package steer.clear.pojo;

/**
 * Created by milespeele on 8/8/15.
 */
public class RegisterPost {

    public String username;
    public String password;
    public String phone;

    public RegisterPost(String username, String password, String phone) {
        this.username = username;
        this.password = password;
        this.phone = phone;
    }
}
