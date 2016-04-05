package steerclear.wm.util;

import android.content.Context;
import android.content.res.Resources;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import steerclear.wm.R;

public class ErrorUtils {

    private static int getErrorCode(HttpException httpException) {
        return httpException.code();
    }

    public static String getMessage(Context context, HttpException httpException) {
        return getMessage(context, getErrorCode(httpException));
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

