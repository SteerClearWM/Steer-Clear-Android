package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import steer.clear.R;
import steer.clear.util.Utils;

/**
 * Created by Miles Peele on 7/19/2015.
 */
public class ViewEta extends TextView {

    private Paint curPaint;

    public ViewEta(Context context) {
        super(context);
        init();
    }

    public ViewEta(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewEta(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewEta(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(Utils.getStaticTypeFace(getContext(), "Avenir.otf"));
        curPaint = new Paint();
        curPaint.setAntiAlias(true);
        curPaint.setColor(getResources().getColor(R.color.quarter_opacity));
        curPaint.setStyle(Paint.Style.FILL);
        curPaint.setStrokeJoin(Paint.Join.ROUND);
        curPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2, curPaint);
    }
}
