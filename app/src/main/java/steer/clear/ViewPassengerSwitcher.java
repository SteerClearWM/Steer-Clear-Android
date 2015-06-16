package steer.clear;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;

/**
 * Created by milespeele on 6/16/15.
 */
public class ViewPassengerSwitcher extends TextSwitcher {

    public ViewPassengerSwitcher(Context context) {
        super(context);
        setFactory(mFactory);
    }

    public ViewPassengerSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFactory(mFactory);
    }

    private final ViewFactory mFactory = new ViewFactory() {

        @Override
        public View makeView() {
            TextView t = new TextView(ViewPassengerSwitcher.this.getContext());
            t.setGravity(Gravity.CENTER_HORIZONTAL);
            t.setTextAppearance(ViewPassengerSwitcher.this.getContext(), android.R.style.TextAppearance_Large);
            return t;
        }
    };


}
