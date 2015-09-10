package steer.clear.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import steer.clear.R;
import steer.clear.util.FontUtils;

public class ViewTypefaceTextView extends TextView {

    private final static int BACKGROUND_ANIMATION = 350;

    private ObjectAnimator backgroundAnimator;
    private Paint circlePaint;

    private boolean shouldDrawCircle;
    private int previousBackgroundAnimatedColor;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewTypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setTypeface(FontUtils.getStaticTypeFace(getContext(), "Avenir.otf"));

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(getResources().getColor(R.color.wm_silver));
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
        previousBackgroundAnimatedColor = color;
        backgroundAnimator = ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(),
                Color.WHITE, color)
                .setDuration(BACKGROUND_ANIMATION);
        backgroundAnimator.start();
    }
}