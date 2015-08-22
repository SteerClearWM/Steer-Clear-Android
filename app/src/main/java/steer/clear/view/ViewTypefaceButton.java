package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

import steer.clear.R;
import steer.clear.util.Utils;

/**
 * Created by Miles Peele on 8/22/2015.
 */
public class ViewTypefaceButton extends Button {

    private Paint borderPaint;

    public ViewTypefaceButton(Context context) {
        super(context);
        init();
    }

    public ViewTypefaceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewTypefaceButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewTypefaceButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(Utils.getStaticTypeFace(getContext(), "Avenir.otf"));
        setTextColor(Color.BLACK);
        setTextSize(15f);
        setGravity(Gravity.CENTER);
        setCompoundDrawablePadding(5);

        borderPaint = new Paint();
        borderPaint.setColor(getResources().getColor(R.color.wm_silver));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeWidth(10f);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), borderPaint);
    }
}
