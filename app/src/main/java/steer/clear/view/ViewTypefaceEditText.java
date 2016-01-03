package Steer.Clear.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import Steer.Clear.util.TextUtils;

public class ViewTypefaceEditText extends AppCompatEditText {

    public ViewTypefaceEditText(Context context) {
        super(context);
        init();
    }

    public ViewTypefaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewTypefaceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(TextUtils.getStaticTypeFace(getContext(), TextUtils.FONT_NAME), Typeface.BOLD);
    }

    public String getEnteredText() {
        return getText().toString();
    }
}
