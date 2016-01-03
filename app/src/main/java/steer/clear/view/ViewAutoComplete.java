package Steer.Clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import java.lang.ref.WeakReference;

import Steer.Clear.R;
import Steer.Clear.util.TextUtils;

public class ViewAutoComplete extends AutoCompleteTextView {

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;
    private static final int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;

    private Drawable clearDrawable;
    private Drawable blockDrawable;
    private Drawable[] mCompoundDrawables;

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
        setEllipsize(android.text.TextUtils.TruncateAt.END);
        setTypeface(TextUtils.getStaticTypeFace(getContext(), TextUtils.FONT_NAME));
        setCompoundDrawablePadding(15);

        blockDrawable = getCompoundDrawables()[0];
        clearDrawable = getCompoundDrawables()[2];

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setCancelDrawableVisible(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setCancelDrawableVisible(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > getWidth() - getPaddingRight() - clearDrawable.getIntrinsicWidth()) {
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
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    protected void handlerFilter(CharSequence msg, int delay) {
        super.performFiltering(msg, delay);
    }

    public void setAutoCompleteListener(AutoCompleteListener listener) {
        mListener = listener;
    }

    private void setCancelDrawableVisible(boolean hasText) {
        if (mCompoundDrawables == null) {
            mCompoundDrawables = getCompoundDrawables();
        }

        if (hasText) {
            setCompoundDrawablesWithIntrinsicBounds(
                    blockDrawable,
                    mCompoundDrawables[1],
                    clearDrawable,
                    mCompoundDrawables[3]);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(
                    blockDrawable,
                    mCompoundDrawables[1],
                    null,
                    mCompoundDrawables[3]);
        }
    }

    public void closeKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder binder = getWindowToken();
        if (binder != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }
}