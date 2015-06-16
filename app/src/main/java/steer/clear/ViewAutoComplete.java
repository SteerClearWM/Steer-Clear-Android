package steer.clear;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class ViewAutoComplete extends AutoCompleteTextView implements View.OnTouchListener {

    private ProgressBar mLoadingIndicator;

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;
    private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;

    final int DRAWABLE_LEFT = 0;
    //final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    //final int DRAWABLE_BOTTOM = 3;

    private AutoCompleteListener mListener;
    public interface AutoCompleteListener {
        void arrowClicked();
        void clearClicked();
    }

    private MyHandler mHandler = new MyHandler(this);

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getRawX() >= (getRight() - getPaddingRight() - getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                mListener.arrowClicked();
            }

            if (event.getRawX() <= (getLeft() + getPaddingLeft() + getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                mListener.clearClicked();
            }
        }
        return false;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ViewAutoComplete> ref;

        public MyHandler(ViewAutoComplete view) {
            ref = new WeakReference<ViewAutoComplete>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ref.get().handlerFilter((CharSequence) msg.obj, msg.arg1);
        }
    }

    public ViewAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnTouchListener(this);
    }

    public void setLoadingIndicator(ProgressBar progressBar) {
        mLoadingIndicator = progressBar;
    }

    public void setAutoCompletListener(AutoCompleteListener listener) {
        mListener = listener;
    }

    protected void handlerFilter(CharSequence msg, int delay) {
        super.performFiltering(msg, delay);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFilterComplete(int count) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (isFocused()) {

        }
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
}