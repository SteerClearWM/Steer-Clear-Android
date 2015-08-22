package steer.clear.view;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import steer.clear.util.Logger;
import steer.clear.util.Utils;

/**
 * Created by Miles Peele on 7/21/2015.
 */
public class ViewRectangleBackgroundButton extends Button {

    private Paint curPaint;
    private Paint ripplePaint;
    private RectF rect;

    private final static int DURATION = 1500;
    private final static int HALF_ALPHA = 128;
    private final static float STROKE_WIDTH = 10f;
    private float radius;
    private boolean startRipple = false;

    public ViewRectangleBackgroundButton(Context context) {
        super(context);
        init();
    }

    public ViewRectangleBackgroundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewRectangleBackgroundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewRectangleBackgroundButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(Utils.getStaticTypeFace(getContext(), "Antipasto.otf"));
        curPaint = new Paint();
        curPaint.setAntiAlias(true);
        curPaint.setColor(Color.WHITE);
        curPaint.setStyle(Paint.Style.STROKE);
        curPaint.setStrokeJoin(Paint.Join.ROUND);
        curPaint.setStrokeCap(Paint.Cap.ROUND);
        curPaint.setStrokeWidth(STROKE_WIDTH);

        ripplePaint = new Paint();
        ripplePaint.setAntiAlias(true);
        ripplePaint.setStyle(Paint.Style.FILL);
        ripplePaint.setColor(Color.WHITE);
        ripplePaint.setAlpha(HALF_ALPHA);
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect = new RectF(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rect, 10, 10, curPaint);
        if (startRipple) {
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, ripplePaint);
        }
    }

    public void startRippleAnimation() {
        if (!startRipple) {
            startRipple = true;

            AnimatorSet test = new AnimatorSet();

            ObjectAnimator radius = ObjectAnimator.ofFloat(this, "radius", getMeasuredWidth());
            radius.setDuration(DURATION);
            radius.setRepeatCount(ValueAnimator.INFINITE);

            ObjectAnimator alpha =  ObjectAnimator.ofObject(ripplePaint, "alpha",
                    new ArgbEvaluator(), HALF_ALPHA, 0);
            alpha.setDuration(DURATION);
            alpha.setRepeatCount(ValueAnimator.INFINITE);

            test.playTogether(radius, alpha);
            test.start();
        }
    }

    public void stopRippleAnimation() {
        startRipple = false;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

}
