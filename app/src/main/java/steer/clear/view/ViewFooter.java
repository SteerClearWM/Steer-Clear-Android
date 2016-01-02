package steer.clear.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import steer.clear.R;
import steer.clear.util.TextUtils;

public class ViewFooter extends AppCompatButton {

    public ViewFooter(Context context) {
        super(context);
        init();
    }

    public ViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(TextUtils.getStaticTypeFace(getContext(), TextUtils.FONT_NAME));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.footer_text_size));
        setBackgroundResource(R.drawable.footer_selector);
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
    }

}
