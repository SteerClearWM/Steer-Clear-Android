package steerclear.wm.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import steerclear.wm.MainApp;
import steerclear.wm.R;
import steerclear.wm.data.event.EventLogout;
import steerclear.wm.util.TextUtils;

public class ViewHeader extends AppCompatTextView {

    public ViewHeader(Context context) {
        super(context);
        init();
    }

    public ViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ((MainApp) getContext().getApplicationContext()).getApplicationComponent().inject(this);
        setTypeface(TextUtils.getStaticTypeFace(getContext(), TextUtils.FONT_NAME));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.header_text_size));
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_dark));
    }
}
