package steerclear.wm.util;

import android.content.Context;
import android.content.res.Resources;

import retrofit.RetrofitError;
import retrofit.client.Response;
import steerclear.wm.R;

public class ErrorUtils {

    public final static int UNAUTHORIZED = 401;
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
                return resources.getString(R.string.snackbar_unknown_error);
            case 404:
                return resources.getString(R.string.snackbar_no_internet);
            case 401:
                return resources.getString(R.string.snackbar_unauthorized);
            case 500:
                return resources.getString(R.string.snackbar_internal_server_error);
            case 503:
                return resources.getString(R.string.error_dialog_not_running_body);
            default:
                return resources.getString(R.string.snackbar_general_error);
        }
    }

}

