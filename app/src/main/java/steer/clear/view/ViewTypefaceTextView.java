package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import steer.clear.util.Utils;

/**
 * Created by Miles Peele on 8/21/2015.
 */
public class ViewTypefaceTextView extends TextView {

    public ViewTypefaceTextView(Context context) {
        super(context);
        init();
    }

    public ViewTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewTypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewTypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(Utils.getStaticTypeFace(getContext(), "Avenir.otf"));
    }
}
