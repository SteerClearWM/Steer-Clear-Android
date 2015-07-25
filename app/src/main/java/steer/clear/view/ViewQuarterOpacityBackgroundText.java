package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import steer.clear.R;
import steer.clear.util.Utils;

/**
 * Created by Miles Peele on 7/19/2015.
 */
public class ViewQuarterOpacityBackgroundText extends TextView {

    private Paint curPaint;
    private RectF rect;

    public ViewQuarterOpacityBackgroundText(Context context) {
        super(context);
        init();
    }

    public ViewQuarterOpacityBackgroundText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewQuarterOpacityBackgroundText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewQuarterOpacityBackgroundText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(Utils.getStaticTypeFace(getContext(), "Antipasto.otf"));
        curPaint = new Paint();
        curPaint.setAntiAlias(true);
        curPaint.setColor(getResources().getColor(R.color.quarter_opacity));
        curPaint.setStyle(Paint.Style.FILL);
        curPaint.setStrokeJoin(Paint.Join.ROUND);
        curPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), 10, 10, curPaint);
    }

}
