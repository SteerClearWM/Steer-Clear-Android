package steer.clear.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import steer.clear.R;

/**
 * Created by milespeele on 6/16/15.
 */
public class ViewPassengerSwitcher extends RelativeLayout {

    LayoutInflater mInflater;
    TextSwitcher switcher;
    ImageButton animateNext;
    ImageButton animatePrevious;

    public ViewPassengerSwitcher(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public ViewPassengerSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public ViewPassengerSwitcher(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {
        mInflater.inflate(R.layout.view_passenger_switcher, this, true);
        switcher = (TextSwitcher) findViewById(R.id.fragment_hail_ride_passenger_select);
        switcher.setText("0");
    }

    private final ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {

        @Override
        public View makeView() {
            TextView t = new TextView(ViewPassengerSwitcher.this.getContext());
            t.setText("0");
            t.setGravity(Gravity.CENTER_HORIZONTAL);
            t.setTextAppearance(ViewPassengerSwitcher.this.getContext(), android.R.style.TextAppearance_Large);
            return t;
        }
    };


}
