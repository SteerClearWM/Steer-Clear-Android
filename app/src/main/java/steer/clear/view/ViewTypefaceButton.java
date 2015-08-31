package steer.clear.view;

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

/**
 * Created by Miles Peele on 8/22/2015.
 */
public class ViewTypefaceButton extends Button {

    private final static Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private AnimatorSet pulse;
    private ObjectAnimator openRect;
    private ObjectAnimator closeRect;
    private Paint borderPaint;
    private Paint rectPaint;

    private boolean isRectOpen;
    private boolean shouldDrawBorder;
    private boolean shouldFillButton;
    private int preferredTextColor;
    private int drawableColor;
    private float rectWidth;

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
                    getResources().getColor(R.color.wm_silver));

            float textSize = typedArray.getDimension(R.styleable.ViewTypefaceButton_preferredTextSize,
                    getResources().getDimension(R.dimen.view_typeface_button_text_size));
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            shouldDrawBorder = typedArray.getBoolean(R.styleable.ViewTypefaceButton_drawBorder,
                    false);

            shouldFillButton = typedArray.getBoolean(R.styleable.ViewTypefaceButton_fillOnClick,
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
        borderPaint.setStrokeWidth(15f);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(drawableColor);
        rectPaint.setStrokeWidth(5f);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setStrokeJoin(Paint.Join.ROUND);
        rectPaint.setStrokeCap(Paint.Cap.ROUND);
        rectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

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
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), borderPaint);
        }

        if (shouldFillButton) {
            if (hasDrawableLeft()) {
                canvas.drawRect(0, 0, rectWidth, canvas.getHeight(), rectPaint);
            } else {
                if (openRect != null) {
                    canvas.drawRect(rectWidth, 0, getStartOfRect(), canvas.getHeight(), rectPaint);
                }
            }
        }
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

    public void toggleRect() {
        if (isRectOpen) {
            closeRectAnimation();
        } else {
            startRectAnimation();
        }
    }

    public void startRectAnimation() {
        ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(),
                Color.WHITE, drawableColor)
                .setDuration(350)
                .start();
//        if (openRect == null) {
//            openRect = ObjectAnimator.ofFloat(this, "rectWidth", getStartOfRect(),
//                    getEndofRect());
//            openRect.setDuration(350);
//            openRect.setInterpolator(INTERPOLATOR);
//            openRect.start();
//            isRectOpen = true;
//        } else {
//            if (!openRect.isRunning()) {
//                openRect.start();
//                isRectOpen = true;
//            }
//        }
    }

    public void closeRectAnimation() {
        if (closeRect == null) {
            closeRect = ObjectAnimator.ofFloat(this, "rectWidth", getEndofRect(), getStartOfRect());
            closeRect.setDuration(350);
            closeRect.setInterpolator(INTERPOLATOR);
            closeRect.start();
            isRectOpen = false;
        } else {
            if (!closeRect.isRunning()) {
                closeRect.start();
                isRectOpen = false;
            }
        }
    }

    public float getRectWidth() {
        return rectWidth;
    }

    public void setRectWidth(float rectWidth) {
        this.rectWidth = rectWidth;
        invalidate();
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
