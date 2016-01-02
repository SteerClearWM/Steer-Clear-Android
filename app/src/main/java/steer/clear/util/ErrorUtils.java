package steer.clear.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;
import steer.clear.R;
import steer.clear.activity.ActivityAuthenticate;
import steer.clear.view.ViewTypefaceButton;
import steer.clear.view.ViewTypefaceTextView;

public class ErrorUtils {

    public final static int NO_INTERNET = 404;

    public static int getErrorCode(Throwable throwable) {
        if (throwable instanceof RetrofitError) {
            Response error = ((RetrofitError) throwable).getResponse();
            if (error != null) {
                return error.getStatus();
            } else {
                return NO_INTERNET;
            }
        } else {
            return NO_INTERNET;
        }
    }

    public static String getMessage(Context context, Throwable throwable) {
        return getMessage(context, getErrorCode(throwable));
    }

    public static String getMessage(Context context, int code) {
        Resources resources = context.getResources();
        switch (code) {
            case 400:
                return resources.getString(R.string.error_dialog_bad_creds_body);
            case 409:
                return resources.getString(R.string.error_dialog_user_exists_body);
            case 404:
                return resources.getString(R.string.error_dialog_no_internet_body);
            case 401:
                return resources.getString(R.string.error_dialog_unauth_body);
            case 503:
                return resources.getString(R.string.error_dialog_not_running_body);
            default:
                return resources.getString(R.string.error_dialog_general_body);
        }
    }

}

