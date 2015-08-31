package steer.clear.view;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.lang.ref.WeakReference;

import steer.clear.R;
import steer.clear.adapter.AdapterAutoComplete;
import steer.clear.util.ErrorDialog;
import steer.clear.util.FontUtils;

public class ViewAutoComplete extends AutoCompleteTextView {

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;
    private static final int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private GoogleApiClient googleApiClient;

    private Drawable drawable;

    private AnimatorSet test;
    private boolean startRipple = false;
    private final static int DURATION = 2000;
    private float radius;
    private Paint ripplePaint;
    private final static int HALF_ALPHA = 128;

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
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ViewAutoComplete);
        int color = typedArray.getColor(R.styleable.ViewAutoComplete_highlightColor, -1);
        typedArray.recycle();

        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        setTextColor(Color.BLACK);
        setHintTextColor(Color.GRAY);
        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        setThreshold(2);
        setSingleLine(true);
        setGravity(Gravity.START | Gravity.CENTER);
        setHorizontallyScrolling(true);
        setEllipsize(TextUtils.TruncateAt.END);
        setTypeface(FontUtils.getStaticTypeFace(getContext(), "Avenir.otf"));
        setCompoundDrawablePadding(15);
        setSaveEnabled(true);

        drawable = getCompoundDrawables()[2];

        ripplePaint = new Paint();
        ripplePaint.setAntiAlias(true);
        ripplePaint.setStyle(Paint.Style.FILL);
        ripplePaint.setColor(color);
        ripplePaint.setAlpha(HALF_ALPHA);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (startRipple) {
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, ripplePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > getWidth() - getPaddingRight() - drawable.getIntrinsicWidth()) {
                setText("");
                mListener.clearClicked(this);
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void replaceText(CharSequence text) {
        setText(text);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        startRippleAnimation();
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    protected void handlerFilter(CharSequence msg, int delay) {
        super.performFiltering(msg, delay);
        stopRippleAnimation();
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            ErrorDialog.createFromHttpErrorCode(getContext(), 404).show();
        }
    }

    public void setAutoCompleteListener(AutoCompleteListener listener) {
        mListener = listener;
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

    public void startRippleAnimation() {
        startRipple = true;

        if (test == null) {
            test = new AnimatorSet();

            ObjectAnimator radius = ObjectAnimator.ofFloat(this, "radius", 0, getMeasuredWidth());
            radius.setDuration(DURATION);
            radius.setRepeatCount(ValueAnimator.INFINITE);

            ObjectAnimator alpha =  ObjectAnimator.ofObject(ripplePaint, "alpha",
                    new ArgbEvaluator(), HALF_ALPHA, 0);
            alpha.setDuration(DURATION);
            alpha.setRepeatCount(ValueAnimator.INFINITE);

            test.playTogether(radius, alpha);
            test.start();
        } else {
            if (!test.isRunning()) {
                test.start();
            }
        }
    }

    public void stopRippleAnimation() {
        new Handler().postDelayed(() -> startRipple = false, 350);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }
}