package steerclear.wm.ui.activity;

import android.widget.EditText;

/**
 * Created by mbpeele on 4/4/16.
 */
interface IAuthenticateActivity {

    boolean validateUsername(EditText editText);

    boolean validatePassword(EditText editText);

    boolean validatePhoneNumber(EditText editText);

    String formatPhoneNumber(String phoneNumber);

    void login();

    void register();
}
