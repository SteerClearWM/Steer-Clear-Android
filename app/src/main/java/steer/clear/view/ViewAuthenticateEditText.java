package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

import steer.clear.util.Utils;

/**
 * Created by Miles Peele on 7/25/2015.
 */
public class ViewAuthenticateEditText extends EditText {

    private Paint curPaint;
    private RectF rect;

    public ViewAuthenticateEditText(Context context) {
        super(context);
        init();
    }

    public ViewAuthenticateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewAuthenticateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewAuthenticateEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTypeface(Utils.getStaticTypeFace(getContext(), "Antipasto.otf"));
    }

    public String getEnteredText() {
        return getText().toString();
    }
}
