package steer.clear.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import steer.clear.R;
import steer.clear.view.ViewEditText;
import steer.clear.view.ViewRectangleBackgroundButton;

/**
 * Created by Miles Peele on 7/25/2015.
 */
public class FragmentLogin extends Fragment implements View.OnClickListener {

    private final static String USERNAME_KEY = "user";
    private final static String PASSWORD_KEY = "pass";

    @InjectView(R.id.fragment_login_username) ViewEditText username;
    @InjectView(R.id.fragment_login_password) ViewEditText password;
    @InjectView(R.id.fragment_login_register) ViewRectangleBackgroundButton button;

    private ListenerForFragments listener;

    private static AnimatorSet animateIn;
    private static AnimatorSet animateOut;
    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();

    public FragmentLogin() {}

    public static FragmentLogin newInstance() {
        return new FragmentLogin();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ListenerForFragments) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        animateIn = new AnimatorSet();
        animateIn.playTogether(ObjectAnimator.ofFloat(getActivity(), "scaleX", 0.45f, 1),
                ObjectAnimator.ofFloat(getActivity(), "scaleY", 0.45f, 1),
                ObjectAnimator.ofFloat(getActivity(), "alpha", 0, 1));

        animateOut = new AnimatorSet();
        animateOut.playTogether(ObjectAnimator.ofFloat(getActivity(), "scaleX", 1, 0),
                ObjectAnimator.ofFloat(getActivity(), "scaleX", 1, 0),
                ObjectAnimator.ofFloat(getActivity(), "scaleY", 1, 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, v);
        username.setText("fuck");
        password.setText("food");
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
        Animator animator;
        if (enter) {
            animator = animateIn;
        } else {
            animator = animateOut;
        }

        animator.setDuration(750);
        animator.setInterpolator(INTERPOLATOR);
        getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return animator;
    }

    private boolean validateUsername() {
        String input = username.getEnteredText();
        return !input.isEmpty();
    }

    private boolean validatePassword() {
        String pass = password.getEnteredText();
        return !pass.isEmpty();
    }

    @Override
    @OnClick(R.id.fragment_login_register)
    public void onClick(View v) {
        if (validateUsername() && validatePassword()) {
            listener.authenticate(username.getEnteredText(), password.getEnteredText());
        } else {
            ObjectAnimator.ofFloat(button, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0).start();
        }
    }
}
