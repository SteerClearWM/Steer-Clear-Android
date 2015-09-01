package steer.clear.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

import steer.clear.R;
import steer.clear.util.FontUtils;
import steer.clear.util.Logger;

/**
 * Created by Miles Peele on 8/22/2015.
 */
public class ViewTypefaceButton extends Button {

    private AnimatorSet pulse;
    private Paint borderPaint;

    private boolean shouldDrawBorder;
    private boolean isSelected;
    private int preferredTextColor;
    private static float strokeWidth;
    private int drawableColor;

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
            preferredTextColor = typedArray.getColor(R.styleable.ViewTypefaceButton_preferredTextColor, Color.WHITE);
            setTextColor(preferredTextColor);

            drawableColor = typedArray.getColor(R.styleable.ViewTypefaceButton_drawableColor,
                    Color.WHITE);

            float textSize = typedArray.getDimension(R.styleable.ViewTypefaceButton_preferredTextSize,
                    getResources().getDimension(R.dimen.view_typeface_button_text_size));
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            shouldDrawBorder = typedArray.getBoolean(R.styleable.ViewTypefaceButton_drawBorder,
                    false);

            typedArray.recycle();
        } else {
            setTextColor(Color.WHITE);
        }
        setTypeface(FontUtils.getStaticTypeFace(getContext(), "Avenir.otf"));
        setGravity(Gravity.CENTER);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(getResources().getColor(R.color.wm_silver));
        strokeWidth = getResources().getDimension(R.dimen.view_typeface_button_border_width);
        borderPaint.setStrokeWidth(strokeWidth);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shouldDrawBorder) {
            drawBorder(canvas);
        }
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawLine(getStartOfRect(), 0, getEndofRect(), 0, borderPaint);
        canvas.drawLine(getStartOfRect(), canvas.getHeight(), getEndofRect(), canvas.getHeight(), borderPaint);

        borderPaint.setStrokeWidth(strokeWidth / 2);
        canvas.drawLine(getEndofRect(), 0, getEndofRect(), canvas.getHeight(), borderPaint);
        borderPaint.setStrokeWidth(strokeWidth);
    }

    private boolean hasDrawableLeft() {
        return getCompoundDrawables()[0] != null;
    }

    private int getStartOfRect() {
        return hasDrawableLeft() ?
                getCompoundDrawables()[0].getIntrinsicWidth() :
                getMeasuredWidth() - getCompoundDrawables()[2].getIntrinsicWidth();
    }

    private int getEndofRect() {
        return hasDrawableLeft() ?
                getMeasuredWidth() : 0;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected() {
        if (!isSelected) {
            changeToDrawableColorBackground();
            isSelected = true;
        }
    }

    private void changeToDrawableColorBackground() {
        ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(),
                Color.WHITE, drawableColor)
                .setDuration(350)
                .start();
        animateTextColorChange(Color.BLACK, Color.WHITE);
    }

    private void animateTextColorChange(int startColor, int endColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimation.addUpdateListener(animator -> setTextColor((Integer) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    public void setNotSelected() {
        if (isSelected) {
            changeToWhiteBackground();
            isSelected = false;
        }
    }

    private void changeToWhiteBackground() {
        ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(),
                drawableColor, Color.WHITE)
                .setDuration(350)
                .start();
        animateTextColorChange(Color.WHITE, Color.BLACK);
    }

    public void shake() {
        ObjectAnimator.ofFloat(this, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0).start();
    }

    public boolean isPulseRunning() {
        return pulse != null && pulse.isRunning();
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
