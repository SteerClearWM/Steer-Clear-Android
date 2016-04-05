package steerclear.wm.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import steerclear.wm.R;
import steerclear.wm.util.Logg;

public class ViewPassengerSelect extends ScrollView implements View.OnClickListener {

    @Bind(R.id.fragment_hail_ride_passenger_select_linear_layout) LinearLayout linearLayout;

    public int count = 0;

    public ViewPassengerSelect(Context context) {
        super(context);
        init();
    }

    public ViewPassengerSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewPassengerSelect(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewPassengerSelect(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(Math.round(
                getResources().getDimension(R.dimen.view_passenger_select_edge_length)));
        setScrollbarFadingEnabled(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        linearLayout.setOnClickListener(this);
    }

    @Override
    @OnClick({R.id.fragment_passenger_select_1, R.id.fragment_passenger_select_2,
            R.id.fragment_passenger_select_3, R.id.fragment_passenger_select_4,
            R.id.fragment_passenger_select_5, R.id.fragment_passenger_select_6,
            R.id.fragment_passenger_select_7, R.id.fragment_passenger_select_8})
    public void onClick(View v) {
        if (v instanceof ViewTypefaceTextView) {
            ViewTypefaceTextView textView = (ViewTypefaceTextView) v;
            textView.animateBackgroundToColor(ContextCompat.getColor(getContext(), R.color.accent));

            count = Integer.valueOf(textView.getText().toString());

            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                View child = linearLayout.getChildAt(i);
                if (child.getId() != textView.getId()) {
                    child.setBackgroundColor(Color.WHITE);
                }
            }
        }
    }

    public int getPassengers() {
        return count;
    }
}
