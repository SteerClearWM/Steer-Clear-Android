package steer.clear.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.event.EventAuthenticate;
import steer.clear.event.EventGoToRegister;
import steer.clear.view.ViewAuthenticateEditText;
import steer.clear.view.ViewTypefaceButton;

public class FragmentAuthenticate extends Fragment implements View.OnClickListener {

    private final static String USERNAME_KEY = "user";
    private final static String PASSWORD_KEY = "pass";
    private final static String REGISTERED_KEY = "registered";
    private final static int ANIMATION_DURATION = 1300;

    @Bind(R.id.fragment_authenticate_username) ViewAuthenticateEditText username;
    @Bind(R.id.fragment_authenticate_password) ViewAuthenticateEditText password;
    @Nullable @Bind(R.id.fragment_authenticate_phone) ViewAuthenticateEditText phone;
    @Nullable @Bind(R.id.fragment_authenticate_register_prompt) TextView prompt;
    @Bind(R.id.fragment_authenticate_button) ViewTypefaceButton button;

    @Inject EventBus bus;

    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private AnimatorSet pulse;

    public FragmentAuthenticate() {}

    public static FragmentAuthenticate newInstance(boolean hasRegistered) {
        FragmentAuthenticate fragmentAuthenticate = new FragmentAuthenticate();
        Bundle args = new Bundle();
        args.putBoolean(REGISTERED_KEY, hasRegistered);
        fragmentAuthenticate.setArguments(args);
        return fragmentAuthenticate;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainApp) activity.getApplication()).getApplicationComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (getArguments().getBoolean(REGISTERED_KEY)) ?
                inflater.inflate(R.layout.fragment_authenticate_login, container, false) :
                inflater.inflate(R.layout.fragment_authenticate_register, container, false);
        ButterKnife.bind(this, v);

        if (prompt != null) { prompt.setText(createSpan()); }

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            username.setText(savedInstanceState.getString(USERNAME_KEY));
            password.setText(savedInstanceState.getString(PASSWORD_KEY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(USERNAME_KEY, username.getEnteredText());
        outState.putString(PASSWORD_KEY, password.getEnteredText());
        super.onSaveInstanceState(outState);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        AnimatorSet animator = new AnimatorSet();
        if (enter) {
            animator.playTogether(ObjectAnimator.ofFloat(getActivity(), "scaleX", 0, 1),
                    ObjectAnimator.ofFloat(getActivity(), "scaleY", 0, 1),
                    ObjectAnimator.ofFloat(getActivity(), "alpha", 0, 1));
        } else {
            animator.playTogether(ObjectAnimator.ofFloat(getActivity(), "scaleX", 1, 0),
                    ObjectAnimator.ofFloat(getActivity(), "alpha", 1, 0),
                    ObjectAnimator.ofFloat(getActivity(), "scaleY", 1, 0));
        }

        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(INTERPOLATOR);
        return animator;
    }

    public void togglePulse() {
        button.togglePulse();
    }

    private SpannableString createSpan() {
        prompt.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString styledString = new SpannableString(getResources().getString(R.string.fragment_authenticate_register_prompt));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                bus.post(new EventGoToRegister());
            }

            @Override
            public void updateDrawState(TextPaint ds) { ds.setUnderlineText(false); }
        };
        styledString.setSpan(clickableSpan, 23, styledString.length(), 0);

        styledString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.spirit_gold)),
                23, styledString.length(), 0);
        return styledString;
    }

    private boolean validateUsername() {
        return !username.getEnteredText().isEmpty();
    }

    private boolean validatePassword() {
        return !password.getEnteredText().isEmpty();
    }

    private boolean validatePhoneNumber() {
        return phone.getEnteredText().matches("([0-9]{10})");
    }

    private String formatPhoneNumber() {
        return "+1" + phone.getEnteredText();
    }

    public String getUsername() { return username.getEnteredText(); }

    public String getPassword() { return password.getEnteredText(); }

    @Override
    @OnClick(R.id.fragment_authenticate_button)
    public void onClick(View v) {
        if (!pulse.isRunning()) {
            if (getArguments().getBoolean(REGISTERED_KEY)) {
                if (validateUsername() && validatePassword()) {
                    togglePulse();
                    bus.post(new EventAuthenticate(username.getEnteredText(), password.getEnteredText(),
                            "", getArguments().getBoolean(REGISTERED_KEY)));
                } else {
                    button.shake();
                    Snackbar.make(getView(), getResources().getString(R.string.fragment_authenticate_error_login),
                            Snackbar.LENGTH_SHORT).show();
                }
            } else {
                if (validateUsername() && validatePassword() && validatePhoneNumber()) {
                    togglePulse();
                    bus.post(new EventAuthenticate(username.getEnteredText(), password.getEnteredText(),
                            formatPhoneNumber(), getArguments().getBoolean(REGISTERED_KEY)));
                } else {
                    button.shake();
                    Snackbar.make(getView(), getResources().getString(R.string.fragment_authenticate_error_register),
                            Snackbar.LENGTH_SHORT);
                }
            }
        }
    }
}
