package steer.clear.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.event.EventAuthenticate;
import steer.clear.util.Logger;
import steer.clear.view.ViewAuthenticateEditText;
import steer.clear.view.ViewRectangleBackgroundButton;

/**
 * Created by Miles Peele on 7/25/2015.
 */
public class FragmentAuthenticate extends Fragment implements View.OnClickListener {

    private final static String USERNAME_KEY = "user";
    private final static String PASSWORD_KEY = "pass";
    private final static String REGISTERED_KEY = "registered";
    private final static int ANIMATION_DURATION = 1000;

    @Bind(R.id.fragment_authenticate_username) ViewAuthenticateEditText username;
    @Bind(R.id.fragment_authenticate_password) ViewAuthenticateEditText password;
    @Bind(R.id.fragment_authenticate_phone) ViewAuthenticateEditText phone;
    @Bind(R.id.fragment_authenticate_button) ViewRectangleBackgroundButton button;

    @Inject EventBus bus;

    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();

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
        username.setText("fuck");
        password.setText("food");
        if (phone != null) {
            phone.setText("2022819022");
        }
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
        AnimatorSet animator;
        if (enter) {
            animator = new AnimatorSet();
            animator.playTogether(ObjectAnimator.ofFloat(getActivity(), "scaleX", 0.45f, 1),
                    ObjectAnimator.ofFloat(getActivity(), "scaleY", 0.45f, 1),
                    ObjectAnimator.ofFloat(getActivity(), "alpha", 0, 1));
        } else {
            animator = new AnimatorSet();
            animator.playTogether(ObjectAnimator.ofFloat(getActivity(), "scaleX", 1, 0),
                    ObjectAnimator.ofFloat(getActivity(), "scaleX", 1, 0),
                    ObjectAnimator.ofFloat(getActivity(), "scaleY", 1, 0));
        }

        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(INTERPOLATOR);
        return animator;
    }

    public void stopTheRipple() {
        button.stopRippleAnimation();
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

    @Override
    @OnClick(R.id.fragment_authenticate_button)
    public void onClick(View v) {
        if (getArguments().getBoolean(REGISTERED_KEY)) {
            if (validateUsername() && validatePassword()) {
                button.startRippleAnimation();
                bus.post(new EventAuthenticate(username.getEnteredText(), password.getEnteredText(),
                        ""));
            } else {
                ObjectAnimator.ofFloat(button, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0).start();
            }
        } else {
            if (validateUsername() && validatePassword() && validatePhoneNumber()) {
                button.startRippleAnimation();
                bus.post(new EventAuthenticate(username.getEnteredText(), password.getEnteredText(),
                        formatPhoneNumber()));
            } else {
                ObjectAnimator.ofFloat(button, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0).start();
            }
        }
    }
}
