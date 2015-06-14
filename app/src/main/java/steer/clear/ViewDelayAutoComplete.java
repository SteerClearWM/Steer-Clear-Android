package steer.clear;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

public class ViewDelayAutoComplete extends AutoCompleteTextView {

    private ProgressBar mLoadingIndicator;

    public ViewDelayAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLoadingIndicator(ProgressBar progressBar) {
        mLoadingIndicator = progressBar;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFilterComplete(int count) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.GONE);
        }
        super.onFilterComplete(count);
    }

    @Override
    protected void replaceText(CharSequence text) {
        float viewWidth = getMeasuredWidth();
        float textWidth = getPaint().measureText((String) text);
        if (textWidth >= viewWidth) {
            setText(TextUtils.ellipsize(text, new TextPaint(), textWidth - viewWidth, TextUtils.TruncateAt.END));
        } else {
            setText(text);
        }
    }
}