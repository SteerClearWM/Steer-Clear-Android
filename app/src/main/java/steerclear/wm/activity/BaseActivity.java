package steerclear.wm.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import steerclear.wm.MainApp;
import steerclear.wm.R;
import steerclear.wm.retrofit.Client;
import steerclear.wm.util.Datastore;
import steerclear.wm.util.ErrorUtils;
import steerclear.wm.util.Logg;

/**
 * Created by mbpeele on 1/1/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject Client helper;
    @Inject Datastore store;
    @Inject EventBus bus;

    private CompositeSubscription compositeSubscription;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp mainApp = (MainApp) getApplication();
        mainApp.getApplicationComponent().inject(this);
        tracker = mainApp.getDefaultTracker();
        compositeSubscription = new CompositeSubscription();
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);

        tracker.setScreenName("BUTT");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    public void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    public void removeSubscription(Subscription subscribtion) {
        compositeSubscription.remove(subscribtion);
    }

    public boolean hasInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void handleError(Throwable throwable) {
        Logg.log(getClass().getName(), throwable);

        int code = ErrorUtils.getErrorCode(throwable);
        Snackbar.make(findViewById(android.R.id.content),
                ErrorUtils.getMessage(this, code),
                Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (code == ErrorUtils.UNAUTHORIZED) {
                            startActivity(AuthenticateActivity.newIntent(BaseActivity.this, true));
                            finish();
                        }
                    }
                })
                .show();
    }

    public void handleError(Throwable throwable, @StringRes int resId) {
        Logg.log(getClass().getName(), throwable);

        Snackbar.make(findViewById(android.R.id.content),
                resId,
                Snackbar.LENGTH_LONG)
                .show();
    }

    public void handleError(String message) {
        Logg.log(getClass().getName(), message);

        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
