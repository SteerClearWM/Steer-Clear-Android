package Steer.Clear.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import Steer.Clear.MainApp;
import Steer.Clear.R;
import Steer.Clear.event.EventAnimateToMarker;

public class ViewMarkerSelectLayout extends LinearLayout implements View.OnClickListener {

    @Bind(R.id.fragment_map_show_dropoff_location) ViewTypefaceButton pickup;
    @Bind(R.id.fragment_map_show_pickup_location) ViewTypefaceButton dropoff;

    @Inject EventBus bus;

    public ViewMarkerSelectLayout(Context context) {
        super(context);
        init();
    }

    public ViewMarkerSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewMarkerSelectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewMarkerSelectLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        ((MainApp) getContext().getApplicationContext()).getApplicationComponent().inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    @OnClick({R.id.fragment_map_show_dropoff_location, R.id.fragment_map_show_pickup_location})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_map_show_dropoff_location:
                pickup.setSelected();
                dropoff.setNotSelected();
                bus.post(new EventAnimateToMarker(pickup.getId()));
                break;
            case R.id.fragment_map_show_pickup_location:
                dropoff.setSelected();
                pickup.setNotSelected();
                bus.post(new EventAnimateToMarker(dropoff.getId()));
                break;
        }
    }

    public int getIdOfSelectedButton() {
        if (pickup.isChosen()) {
            return pickup.getId();
        } else if (dropoff.isChosen()) {
            return dropoff.getId();
        } else {
            return -1;
        }
    }
}
