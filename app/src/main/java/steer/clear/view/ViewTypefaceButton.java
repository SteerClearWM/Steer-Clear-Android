package steer.clear.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

import steer.clear.R;
import steer.clear.util.FontUtils;

/**
 * Created by Miles Peele on 8/22/2015.
 */
public class ViewTypefaceButton extends Button {

    private AnimatorSet pulse;

    public ViewTypefaceButton(Context context) {
        super(context);
        init(null);
    }

    public ViewTypefaceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ViewTypefaceButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewTypefaceButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ViewTypefaceButton);
            int color = typedArray.getColor(R.styleable.ViewTypefaceButton_preferredTextColor, -1);
            if (color == -1) {
                setTextColor(Color.WHITE);
            } else {
                setTextColor(getResources().getColor(R.color.wm_green));
            }
            typedArray.recycle();
        } else {
            setTextColor(Color.WHITE);
        }
        setTypeface(FontUtils.getStaticTypeFace(getContext(), "Avenir.otf"));
        setTextSize(25f);
        setGravity(Gravity.CENTER);

        pulse = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, .9f);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, .9f);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        pulse.playTogether(scaleX, scaleY);
        pulse.setDuration(600);
    }

    public void shake() {
        ObjectAnimator.ofFloat(this, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0).start();
    }

    public boolean isPulseRunning() {
        return pulse.isRunning();
    }

    public void togglePulse() {
        if (pulse.isRunning()) {
            pulse.cancel();
            AnimatorSet normalize = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f);
            normalize.playTogether(scaleX, scaleY);
            normalize.start();
        } else {
            pulse.start();
        }
    }
}
