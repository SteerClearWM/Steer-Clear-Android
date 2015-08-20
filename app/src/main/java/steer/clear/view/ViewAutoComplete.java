package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

import steer.clear.R;
import steer.clear.adapter.AdapterAutoComplete;
import steer.clear.util.Logger;

public class ViewAutoComplete extends AutoCompleteTextView {

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;
    private static final int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;

    private Paint rectPaint;
    private Drawable cancelDrawable;

    private MyHandler mHandler = new MyHandler(this);
    private final static class MyHandler extends Handler {
        private final WeakReference<ViewAutoComplete> ref;

        public MyHandler(ViewAutoComplete view) {
            ref = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ref.get().handlerFilter((CharSequence) msg.obj, msg.arg1);
        }
    }

    private AutoCompleteListener mListener;
    public interface AutoCompleteListener {
        void clearClicked(View v);
    }

    public ViewAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ViewAutoComplete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewAutoComplete(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        setTextColor(Color.BLACK);
        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        setThreshold(2);
        setSingleLine(true);
        setGravity(GravityCompat.START);
        setHorizontallyScrolling(true);
        setEllipsize(TextUtils.TruncateAt.END);
        setBackgroundColor(Color.WHITE);

        cancelDrawable = getCompoundDrawables()[0];

//        getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        rectPaint = new Paint();
        rectPaint.setColor(Color.YELLOW);
        rectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        rectPaint.setAntiAlias(true);
        rectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), rectPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() <= (getLeft() + getPaddingLeft() + cancelDrawable.getIntrinsicWidth())) {
                mListener.clearClicked(this);
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    @Override
    protected void replaceText(CharSequence text) {
        float viewWidth = getMeasuredWidth();
        float textWidth = getPaint().measureText((String) text);
        if (textWidth > viewWidth) {
            setText(TextUtils.ellipsize(text, getPaint(),
                    viewWidth - viewWidth / 4,
                    TextUtils.TruncateAt.END, true, null));
        } else {
            setText(text);
        }
    }

    public void setAutoCompleteListener(AutoCompleteListener listener) {
        mListener = listener;
    }

    protected void handlerFilter(CharSequence msg, int delay) {
        super.performFiltering(msg, delay);
    }

    public void setTextNoFilter(String text, boolean toFilter) {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            setText(text, toFilter);
        } else {
            AdapterAutoComplete test = (AdapterAutoComplete) getAdapter();
            setAdapter(null);
            setText(text);
            setAdapter(test);
        }
    }

}