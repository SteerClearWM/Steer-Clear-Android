package steerclear.wm.ui.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import icepick.Icepick;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import steerclear.wm.MainApp;
import steerclear.wm.data.DataStore;
import steerclear.wm.data.retrofit.SteerClearClient;
import steerclear.wm.util.ErrorUtils;
import steerclear.wm.util.Logg;

/**
 * Created by mbpeele on 1/1/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    SteerClearClient helper;
    @Inject
    DataStore store;

    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp mainApp = (MainApp) getApplication();
        mainApp.getApplicationComponent().inject(this);
        compositeSubscription = new CompositeSubscription();
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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

    public boolean hasPermissions(String... permissions) {
        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public void requestPermissions(int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
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
        throwable.printStackTrace();

        Snackbar.make(findViewById(android.R.id.content), resId, Snackbar.LENGTH_LONG).show();
    }

    public void handleError(String message) {
        Logg.log(getClass().getName(), message);

        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
