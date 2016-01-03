package steerclear.wm.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
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
public class ActivityBase extends AppCompatActivity {

    @Inject Client helper;
    @Inject Datastore store;
    @Inject EventBus bus;

    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((MainApp) getApplication()).getApplicationComponent().inject(this);
        compositeSubscription = new CompositeSubscription();
        super.onCreate(savedInstanceState);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    public void addSubscription(Subscription subscription) {
        if (hasInternet()) {
            compositeSubscription.add(subscription);
        } else {
            handleError(getResources().getString(R.string.snackbar_no_internet));
        }
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
                            startActivity(ActivityAuthenticate.newIntent(ActivityBase.this, true));
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
