package Steer.Clear.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import Steer.Clear.R;
import Steer.Clear.util.TextUtils;
import Steer.Clear.util.ViewUtils;

public class ViewTypefaceButton extends AppCompatButton {

    private Paint borderPaint;

    private boolean shouldDrawBorder;
    private float strokeWidth;
    private boolean isChosen;
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

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ViewTypefaceButton);

            setTextColor(typedArray.getColor(R.styleable.ViewTypefaceButton_preferredTextColor, Color.WHITE));

            setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimension(R.styleable.ViewTypefaceButton_preferredTextSize,
                    getResources().getDimension(R.dimen.view_typeface_button_text_size)));

            drawableColor = typedArray.getColor(R.styleable.ViewTypefaceButton_drawableColor,
                    Color.WHITE);

            shouldDrawBorder = typedArray.getBoolean(R.styleable.ViewTypefaceButton_drawBorder,
                    false);

            typedArray.recycle();
        } else {
            setTextColor(Color.WHITE);
        }
        setTypeface(TextUtils.getStaticTypeFace(getContext(), TextUtils.FONT_NAME));

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(getResources().getColor(R.color.secondary_text));

        strokeWidth = getResources().getDimension(R.dimen.view_typeface_button_border_width);
        borderPaint.setStrokeWidth(strokeWidth);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
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

    public void animateTextColorChange(int... colors) {
        ValueAnimator colorAnimation = ValueAnimator.ofInt(colors);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.addUpdateListener(animator -> setTextColor((Integer) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setSelected() {
        if (!isChosen) {
            changeToDrawableColorBackground();
            isChosen = true;
        }
    }

    public void setNotSelected() {
        if (isChosen) {
            changeToWhiteBackground();
            isChosen = false;
        }
    }

    private void changeToDrawableColorBackground() {
        ObjectAnimator.ofObject(this, ViewUtils.BACKGROUND_COLOR, new ArgbEvaluator(),
                Color.WHITE, drawableColor)
                .setDuration(350)
                .start();
        animateTextColorChange(Color.BLACK, Color.WHITE);
    }

    private void changeToWhiteBackground() {
        ObjectAnimator.ofObject(this, ViewUtils.BACKGROUND_COLOR, new ArgbEvaluator(),
                drawableColor, Color.WHITE)
                .setDuration(350)
                .start();
        animateTextColorChange(Color.WHITE, Color.BLACK);
    }
}
