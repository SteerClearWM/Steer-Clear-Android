package Steer.Clear.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import Steer.Clear.MainApp;
import Steer.Clear.R;
import Steer.Clear.event.EventLogout;
import Steer.Clear.util.TextUtils;

public class ViewHeader extends AppCompatTextView {

    private Paint borderPaint;
    private Drawable rightDrawable;

    @Inject EventBus bus;

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

    private void init() {
        ((MainApp) getContext().getApplicationContext()).getApplicationComponent().inject(this);
        setTypeface(TextUtils.getStaticTypeFace(getContext(), TextUtils.FONT_NAME));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.header_text_size));
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setBackgroundColor(getResources().getColor(R.color.primary_dark));

        borderPaint = new Paint();
        borderPaint.setColor(getResources().getColor(R.color.accent));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeWidth(20f);

        if (getCompoundDrawables()[2] != null) {
            rightDrawable = getCompoundDrawables()[2];
            setCompoundDrawablePadding(rightDrawable.getIntrinsicWidth());
            setPadding(rightDrawable.getIntrinsicWidth() * 3, getPaddingTop(),
                    rightDrawable.getIntrinsicWidth(), getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), borderPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (rightDrawable != null) {
                if (event.getX() > getWidth() - getPaddingRight() - rightDrawable.getIntrinsicWidth()) {
                    bus.post(new EventLogout());
                }
            }
        }

        return super.onTouchEvent(event);
    }
}
