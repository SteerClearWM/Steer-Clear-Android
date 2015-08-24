package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

import steer.clear.R;
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
        setTypeface(Utils.getStaticTypeFace(getContext(), "Avenir.otf"), Typeface.BOLD);
        setGravity(GravityCompat.START);
        setLines(1);
        setMaxLines(1);
        getBackground().setColorFilter(getResources().getColor(R.color.spirit_gold), PorterDuff.Mode.SRC_ATOP);
    }

    public String getEnteredText() {
        return getText().toString();
    }
}
