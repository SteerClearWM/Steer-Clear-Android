package steerclear.wm.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import icepick.State;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steerclear.wm.R;
import steerclear.wm.data.rx.ActivitySubscriber;
import steerclear.wm.ui.view.ViewTypefaceButton;
import steerclear.wm.ui.view.ViewTypefaceEditText;
import steerclear.wm.ui.view.ViewTypefaceTextView;
import steerclear.wm.util.ErrorUtils;
import steerclear.wm.util.ViewUtils;

public class AuthenticateActivity extends BaseActivity implements IAuthenticateActivity {

    private final static String RELOGIN = "relogin";

    @Bind(R.id.activity_authenticate_logo)
    ImageView logo;
    @Bind(R.id.activity_authenticate_username)
    ViewTypefaceEditText editUsername;
    @Bind(R.id.activity_authenticate_password)
    ViewTypefaceEditText editPassword;
    @Bind(R.id.activity_authenticate_phone)
    ViewTypefaceEditText editPhone;
    @Bind(R.id.activity_authenticate_register_prompt)
    ViewTypefaceTextView prompt;
    @Bind(R.id.activity_authenticate_button)
    ViewTypefaceButton button;

    public static Intent newIntent(Context context, boolean shouldLogin) {
        Intent intent = new Intent(context, AuthenticateActivity.class);
        intent.putExtra(RELOGIN, shouldLogin);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

//        if (getIntent().getBooleanExtra(RELOGIN, false)) {
//        }

        if (store.hasPreviousRideInfo()) {
            startActivity(EtaActivity.newIntent(this, store.getEta(), store.getCancelId()));
            return;
        }

        if (store.hasCookie()) {
            startActivity(HomeActivity.newIntent(this));
            return;
        }

        prompt.setText(createRegisterPromptSpan());

        editUsername.setText(store.getUsername());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean shouldRegister = editPhone.getVisibility() == View.VISIBLE;
                if (shouldRegister) {
                    register();
                } else {
                    login();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        boolean isPhoneVisible = editPhone.getVisibility() == View.VISIBLE;
        if (isPhoneVisible) {
            ViewUtils.invisible(editPhone, ViewUtils.DEFAULT_VISBILITY_DURATION).start();
            ViewUtils.visible(prompt);
            button.animateTextColorChange(Color.TRANSPARENT, button.getCurrentTextColor());
            button.setText(R.string.fragment_authenticate_login_text);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean validateUsername(EditText editText) {
        String text = editText.getText().toString();
        boolean isLowerCase = text.matches("^[a-zA-Z0-9]*$");
        if (!isLowerCase) {
            editText.setError(getResources().getString(R.string.fragment_authenticate_username_fail));
        }
        return isLowerCase;
    }

    @Override
    public boolean validatePassword(EditText editText) {
        String text = editText.getText().toString();
        boolean isEmpty = text.isEmpty();
        if (isEmpty) {
            editText.setError(getResources().getString(R.string.fragment_authenticate_password_fail));
        }
        return !isEmpty;
    }

    @Override
    public boolean validatePhoneNumber(EditText editText) {
        String text = editText.getText().toString();
        boolean matches = text.matches("([0-9]{10})");
        if (!matches) {
            editText.setError(getResources().getString(R.string.fragment_authenticate_phone_fail));
        }
        return matches;
    }

    @Override
    public void register() {
        boolean validateUsername = validateUsername(editUsername);
        boolean validatePassword = validatePassword(editPassword);
        boolean validatePhone = validatePhoneNumber(editPhone);
        if (validateUsername && validatePassword && validatePhone) {
            Subscriber<ResponseBody> registerSubscriber = new ActivitySubscriber<ResponseBody>(this) {
                @Override
                public void onCompleted() {
                    store.putUserHasRegistered();
                    store.putUsername(editUsername.getEnteredText());
                    login();
                }

                @Override
                public void onError(Throwable throwable) {
                    if (ErrorUtils.getErrorCode(throwable) == 409) {
                        onCompleted();
                    } else {
//                    authenticateFragment.toggleAnimation();
                        handleError(throwable, R.string.snackbar_invalid_creds);
                    }
                }

                @Override
                public void onNext(ResponseBody ResponseBody) {
                }

                @Override
                public void onStart() {
                    super.onStart();
//                authenticateFragment.toggleAnimation();
                }
            };

            helper.register(editUsername.getEnteredText(), editPassword.getEnteredText(), editPhone.getEnteredText())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(registerSubscriber);
        }
    }

    @Override
    public void login() {
        boolean validateUsername = validateUsername(editUsername);
        boolean validatePassword = validatePassword(editPassword);
        if (validateUsername && validatePassword) {
            Subscriber<ResponseBody> loginSubscriber = new ActivitySubscriber<ResponseBody>(this) {
                @Override
                public void onCompleted() {
                    startActivity(HomeActivity.newIntent(AuthenticateActivity.this));
                    finish();
                }

                @Override
                public void onError(Throwable throwable) {
//                authenticateFragment.toggleAnimation();
                    handleError(throwable, R.string.snackbar_invalid_creds);
                }

                @Override
                public void onNext(ResponseBody response) {
//                    for (Header header: response.getHeaders()) {
//                        if (header.getName().contains("Set-Cookie")) {
//                            store.putCookie(header.getValue());
//                        }
//                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
//                if (authenticateFragment != null && !authenticateFragment.isAnimating()) {
//                    authenticateFragment.toggleAnimation();
//                }
                }
            };

            helper.login(editUsername.getEnteredText(), editPassword.getEnteredText())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(loginSubscriber);
        }
    }

    @Override
    public String formatPhoneNumber(String phoneNumber) {
        return "+1" + phoneNumber;
    }

    private SpannableString createRegisterPromptSpan() {
        prompt.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString styledString = new SpannableString(getResources().getString(R.string.fragment_authenticate_register_prompt));

        styledString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ViewUtils.visible(editPhone);
                ViewUtils.invisible(prompt, ViewUtils.DEFAULT_VISBILITY_DURATION).start();
                button.animateTextColorChange(Color.TRANSPARENT, button.getCurrentTextColor());
                button.setText(R.string.fragment_authenticate_register_text);
            }
        }, 23, styledString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        styledString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accent)),
                23, styledString.length(), 0);

        return styledString;
    }
}
