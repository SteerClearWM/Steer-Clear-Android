package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

import steer.clear.R;
import steer.clear.util.FontUtils;

/**
 * Created by Miles Peele on 8/21/2015.
 */
public class ViewFooter extends Button {

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewFooter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(FontUtils.getStaticTypeFace(getContext(), "Avenir.otf"));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.footer_text_size));
        setBackgroundResource(R.drawable.footer_selector);
        setGravity(Gravity.CENTER);
    }
}
