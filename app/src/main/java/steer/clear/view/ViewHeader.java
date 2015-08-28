package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import steer.clear.R;
import steer.clear.util.Utils;

/**
 * Created by Miles Peele on 8/20/2015.
 */
public class ViewHeader extends TextView {

    private Paint borderPaint;

    public ViewHeader(Context context) {
        super(context);
        init();
    }

    public ViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(Utils.getStaticTypeFace(getContext(), "Avenir.otf"));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.header_text_size));
        setGravity(Gravity.CENTER);
        setBackgroundColor(getResources().getColor(R.color.wm_green));

        borderPaint = new Paint();
        borderPaint.setColor(getResources().getColor(R.color.spirit_gold));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeWidth(20f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), borderPaint);
    }
}
