package steerclear.wm.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;

import steerclear.wm.R;
import steerclear.wm.data.AdapterAutoComplete;
import steerclear.wm.util.TextUtils;

public class ViewAutoComplete extends AutoCompleteTextView {

    public ViewAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ViewAutoComplete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewAutoComplete(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ViewAutoComplete);
        int color = typedArray.getColor(R.styleable.ViewAutoComplete_highlightColor, -1);
        typedArray.recycle();

        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        setTextColor(Color.BLACK);
        setHintTextColor(Color.GRAY);
        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        setThreshold(2);
        setSingleLine(true);
        setGravity(Gravity.START | Gravity.CENTER);
        setHorizontallyScrolling(true);
        setEllipsize(android.text.TextUtils.TruncateAt.END);
        setTypeface(TextUtils.getStaticTypeFace(getContext(), TextUtils.FONT_NAME));
        setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.fragment_map_autocomplete_view_padding));
    }

    @Override
    protected void replaceText(CharSequence text) {
        setText(text);
    }
}