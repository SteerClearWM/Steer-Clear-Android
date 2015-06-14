package steer.clear;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

public class ViewAutoComplete extends AutoCompleteTextView implements View.OnFocusChangeListener {

    private ProgressBar mLoadingIndicator;
    private Drawable clearIcon;
    private OnFocusChangeListener f;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

//    public interface Listener {
//        void didClearText();
//    }

    public ViewAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        clearIcon = getCompoundDrawables()[DRAWABLE_LEFT];
        clearIcon.setBounds(0, 0, clearIcon.getIntrinsicWidth(), clearIcon.getIntrinsicHeight());
        setClearIconVisible(false);
        super.setOnFocusChangeListener(this);
        //addTextChangedListener(new TextWatcherAdapter(this, this));
    }

    public void setLoadingIndicator(ProgressBar progressBar) {
        mLoadingIndicator = progressBar;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener f) {
        this.f = f;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        super.performFiltering(text, keyCode);
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFilterComplete(int count) {
        super.onFilterComplete(count);
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (isFocused()) {
            setClearIconVisible(isNotEmpty(text));
        }
    }

    @Override
    protected void replaceText(CharSequence text) {
//        float viewWidth = getMeasuredWidth();
//        float textWidth = getPaint().measureText((String) text);
//        if (textWidth > viewWidth) {
//            setText(TextUtils.ellipsize(text, new TextPaint(), (textWidth - viewWidth)/2, TextUtils.TruncateAt.END));
//        } else {
//            setText(text);
//        }
        setText(text.subSequence(0, text.length() / 2) + "..");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(isNotEmpty(getText()));
        } else {
            setClearIconVisible(false);
        }
        if (f != null) {
            f.onFocusChange(v, hasFocus);
        }
    }

    protected void setClearIconVisible(boolean visible) {
        boolean wasVisible = (getCompoundDrawables()[DRAWABLE_LEFT] != null);
        if (visible != wasVisible) {
            Drawable x = visible ? clearIcon : null;
            setCompoundDrawables(x,
                    getCompoundDrawables()[DRAWABLE_TOP],
                    getCompoundDrawables()[DRAWABLE_RIGHT], getCompoundDrawables()[DRAWABLE_BOTTOM]);
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }
}