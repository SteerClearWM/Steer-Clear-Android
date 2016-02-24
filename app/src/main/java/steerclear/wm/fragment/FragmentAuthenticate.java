package steerclear.wm.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import steerclear.wm.MainApp;
import steerclear.wm.R;
import steerclear.wm.activity.ActivityAuthenticate;
import steerclear.wm.event.EventAuthenticate;
import steerclear.wm.util.Datastore;
import steerclear.wm.util.Logg;
import steerclear.wm.util.ViewUtils;
import steerclear.wm.view.ViewTypefaceEditText;
import steerclear.wm.view.ViewTypefaceButton;
import steerclear.wm.view.ViewTypefaceTextView;

public class FragmentAuthenticate extends Fragment
        implements View.OnClickListener {

    private final static String USERNAME_KEY = "user";
    private final static String PASSWORD_KEY = "pass";

    @Bind(R.id.fragment_authenticate_root) LinearLayout root;
    @Bind(R.id.fragment_authenticate_logo) AppCompatImageView logo;
    @Bind(R.id.fragment_authenticate_username) ViewTypefaceEditText editUsername;
    @Bind(R.id.fragment_authenticate_password) ViewTypefaceEditText editPassword;
    @Bind(R.id.fragment_authenticate_phone) ViewTypefaceEditText editPhone;
    @Bind(R.id.fragment_authenticate_register_prompt) ViewTypefaceTextView prompt;
    @Bind(R.id.fragment_authenticate_button) ViewTypefaceButton button;

    private ObjectAnimator rotation;

    @Inject EventBus bus;
    @Inject Datastore store;

    public FragmentAuthenticate() {}

    public static FragmentAuthenticate newInstance() {
        return new FragmentAuthenticate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainApp) context.getApplicationContext()).getApplicationComponent().inject(this);
    }

    public boolean onBackPressed() {
        boolean isPhoneVisible = editPhone.getVisibility() == View.VISIBLE;
        if (isPhoneVisible) {
            ViewUtils.invisible(editPhone, ViewUtils.DEFAULT_VISBILITY_DURATION).start();
            ViewUtils.visible(prompt);
            button.animateTextColorChange(Color.TRANSPARENT, button.getCurrentTextColor());
            button.setText(R.string.fragment_authenticate_login_text);
        }

        return isPhoneVisible;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_authenticate, container, false);
        ButterKnife.bind(this, v);

        prompt.setText(createRegisterPromptSpan());

        editUsername.setText(store.getUsername());

        extendTouchArea();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            editUsername.setText(savedInstanceState.getString(USERNAME_KEY));
            editPassword.setText(savedInstanceState.getString(PASSWORD_KEY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(USERNAME_KEY, editUsername.getEnteredText());
        outState.putString(PASSWORD_KEY, editPassword.getEnteredText());
        super.onSaveInstanceState(outState);
    }

    @Override
    @OnClick({R.id.fragment_authenticate_button})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_authenticate_button:
                if (!isAnimating()) {
                    if (editPhone.getVisibility() != View.VISIBLE) {
                        if (validateUsername() && validatePassword()) {
                            bus.post(new EventAuthenticate(editUsername.getEnteredText(),
                                    editPassword.getEnteredText()));
                        }
                    } else {
                        boolean user = validateUsername();
                        boolean pass = validatePassword();
                        boolean phone = validatePhoneNumber();
                        if (user && pass && phone) {
                            bus.post(new EventAuthenticate(editUsername.getEnteredText(),
                                    editPassword.getEnteredText(), formatPhoneNumber()));
                        }
                    }
                }
                break;
        }
    }

    private SpannableString createRegisterPromptSpan() {
        prompt.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString styledString = new SpannableString(getResources().getString(R.string.fragment_authenticate_register_prompt));

        styledString.setSpan(clickableSpan, 23, styledString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        styledString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accent)),
                23, styledString.length(), 0);

        return styledString;
    }

    private boolean validateUsername() {
        boolean isLowerCase = editUsername.getEnteredText().matches("^[a-zA-Z0-9]*$");
        if (!isLowerCase) {
            editUsername.setError(getResources().getString(R.string.fragment_authenticate_username_fail));
        }
        return isLowerCase;
    }

    private boolean validatePassword() {
        boolean isEmpty = editPassword.getEnteredText().isEmpty();
        if (isEmpty) {
            editPassword.setError(getResources().getString(R.string.fragment_authenticate_password_fail));
        }
        return !isEmpty;
    }

    private boolean validatePhoneNumber() {
        String phone = editPhone.getEnteredText();
        boolean matches = phone.matches("([0-9]{10})");
        if (!matches) {
            editPhone.setError(getResources().getString(R.string.fragment_authenticate_phone_fail));
        }
        return matches;
    }

    private String formatPhoneNumber() {
        return "+1" + editPhone.getEnteredText();
    }

    public void toggleAnimation() {
        if (rotation == null) {
            rotation = ObjectAnimator.ofFloat(logo, View.ROTATION,
                    logo.getRotation(), logo.getRotation() + 360f);
            rotation.setRepeatMode(ValueAnimator.RESTART);
            rotation.setRepeatCount(ValueAnimator.INFINITE);
            rotation.setInterpolator(new LinearInterpolator());
            rotation.setDuration(1000);
            rotation.start();
        } else {
            if (rotation.isRunning()) {
                rotation.setRepeatCount(0);
                rotation.setRepeatMode(0);
            } else {
                rotation.start();
            }
        }
    }

    public boolean isAnimating() {
        return rotation != null && rotation.isRunning();
    }

    private void extendTouchArea() {
        root.post(new Runnable() {
            @Override
            public void run() {
                Rect delegateArea = new Rect();
                prompt.getHitRect(delegateArea);
                delegateArea.top += prompt.getHeight();
                delegateArea.bottom += prompt.getHeight();

                TouchDelegate touchDelegate = new TouchDelegate(delegateArea, prompt);

                root.setTouchDelegate(touchDelegate);
            }
        });
    }

    private final ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            ViewUtils.visible(editPhone);
            ViewUtils.invisible(prompt, ViewUtils.DEFAULT_VISBILITY_DURATION).start();
            button.animateTextColorChange(Color.TRANSPARENT, button.getCurrentTextColor());
            button.setText(R.string.fragment_authenticate_register_text);
        }
    };
}
