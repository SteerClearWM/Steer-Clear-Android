package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

import steer.clear.Logger;
import steer.clear.R;
import steer.clear.util.Utils;

/**
 * Created by Miles Peele on 7/25/2015.
 */
public class ViewEditText extends EditText {

    private Paint curPaint;
    private RectF rect;

    public ViewEditText(Context context) {
        super(context);
        init();
    }

    public ViewEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
