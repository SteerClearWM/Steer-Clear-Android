package Steer.Clear.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import Steer.Clear.R;
import Steer.Clear.util.TextUtils;

public class ViewTypefaceTextView extends AppCompatTextView {

    private final static int BACKGROUND_ANIMATION = 350;

    private ObjectAnimator backgroundAnimator;
    private Paint circlePaint;

    private boolean shouldDrawCircle;

    public ViewTypefaceTextView(Context context) {
        super(context);
        init(null);
    }

    public ViewTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ViewTypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setTypeface(TextUtils.getStaticTypeFace(getContext(), TextUtils.FONT_NAME));

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(getResources().getColor(R.color.secondary_text));
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeJoin(Paint.Join.ROUND);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));

        if (attrs != null) {
            TypedArray typedArray =
                    getContext().obtainStyledAttributes(attrs, R.styleable.ViewTypefaceTextView);
            shouldDrawCircle = typedArray.getBoolean(R.styleable.ViewTypefaceTextView_drawCircle, false);
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (shouldDrawCircle) {
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2, circlePaint);
        }
    }

    public void animateBackgroundToColor(int color) {
        if (backgroundAnimator == null) {
            backgroundAnimator = ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(),
                    Color.WHITE, color)
                    .setDuration(BACKGROUND_ANIMATION);
            backgroundAnimator.start();
        } else {
            if (backgroundAnimator.isRunning()) {
                backgroundAnimator.start();
            }
        }
    }
}