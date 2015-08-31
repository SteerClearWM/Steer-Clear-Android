package steer.clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import steer.clear.R;

/**
 * Created by Miles Peele on 8/30/2015.
 */
public class ViewMarkerSelectLayout extends LinearLayout implements View.OnClickListener {

    @Bind(R.id.fragment_map_show_pickup_location) ViewTypefaceButton pickup;
    @Bind(R.id.fragment_map_show_dropoff_location) ViewTypefaceButton dropoff;

    public ViewMarkerSelectLayout(Context context) {
        super(context);
    }

    public ViewMarkerSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewMarkerSelectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewMarkerSelectLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }


    @Override
    @OnClick({R.id.fragment_map_show_pickup_location, R.id.fragment_map_show_dropoff_location})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_map_show_pickup_location:
                pickup.startRectAnimation();
                break;
            case R.id.fragment_map_show_dropoff_location:
                dropoff.startRectAnimation();
                break;
        }
    }
}
