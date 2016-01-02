package steer.clear.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.IBinder;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import steer.clear.util.Logg;
import steer.clear.util.TextUtils;

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

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (hasFocus() && (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN)) {
//            clearFocus();
//
//            if (requestFocus()) {
//                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                IBinder token = getWindowToken();
//                if (token != null) {
//                    imm.hideSoftInputFromWindow(token, InputMethodManager.SHOW_FORCED);
//                }
//            }
//
////            Logg.log(hasFocus(), isFocused());
////
////            if (hasFocus()) {
////                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
////                IBinder token = getWindowToken();
////                if (token != null) {
////                    imm.hideSoftInputFromWindow(token, InputMethodManager.SHOW_FORCED);
////                }
////            }
//        }
//        return super.onTouchEvent(event);
//    }

    public String getEnteredText() {
        return getText().toString();
    }
}
