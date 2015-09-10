package steer.clear.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

import steer.clear.R;
import steer.clear.util.FontUtils;
import steer.clear.util.Logger;

public class ViewFooter extends Button {

    private final static int BACKGROUND_ANIMATION = 1000;

    private ObjectAnimator objectAnimator;

    public ViewFooter(Context context) {
        super(context);
        init();
    }

    public ViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewFooter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(FontUtils.getStaticTypeFace(getContext(), "Avenir.otf"));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.footer_text_size));
        setBackgroundResource(R.drawable.footer_selector);
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);

        objectAnimator = ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(),
                getResources().getColor(R.color.wm_green), getResources().getColor(R.color.spirit_gold));
        objectAnimator.setDuration(BACKGROUND_ANIMATION);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

}
