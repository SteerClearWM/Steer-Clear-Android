package steer.clear.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class ErrorDialog extends Dialog implements View.OnClickListener, DialogInterface.OnDismissListener {

    @Bind(R.id.error_dialog_title) ViewTypefaceTextView title;
    @Bind(R.id.error_dialog_body) ViewTypefaceTextView body;
    @Bind(R.id.error_dialog_pos_button) ViewTypefaceButton posButton;

    private String titleText;
    private String bodyText;

    public final static int NO_INTERNET = 404;

    public ErrorDialog(Context context, String titleText, String bodyText, int theme) {
        super(context, theme);
        this.titleText = titleText;
        this.bodyText = bodyText;
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_error);
        ButterKnife.bind(this);
        title.setText(titleText);
        body.setText(bodyText);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ButterKnife.unbind(this);
    }

    @Override
    @OnClick(R.id.error_dialog_pos_button)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.error_dialog_pos_button:
                dismiss();
                break;
        }
    }

    public static void createFromNetworkError(Context context, Throwable throwable) {
        if (throwable instanceof RetrofitError) {
            Response error = ((RetrofitError) throwable).getResponse();
            if (error != null) {
                createFromHttpErrorCode(context, error.getStatus()).show();
            } else {
                createFromHttpErrorCode(context, NO_INTERNET).show();
            }
        } else {
            createFromHttpErrorCode(context, NO_INTERNET).show();
        }
    }

    public static ErrorDialog createFromHttpErrorCode(Context context, int code) {
        switch (code) {
            case 400:
                return new ErrorDialog(context,
                        context.getResources().getString(R.string.error_dialog_bad_creds_title),
                        context.getResources().getString(R.string.error_dialog_bad_creds_body),
                        R.style.AlertDialogTheme);
            case 409:
                return new ErrorDialog(context, context.getResources().getString(R.string.error_dialog_user_exists_title),
                        context.getResources().getString(R.string.error_dialog_user_exists_body),
                        R.style.AlertDialogTheme);
            case 404:
                return new ErrorDialog(context, context.getResources().getString(R.string.error_dialog_no_internet_title),
                        context.getResources().getString(R.string.error_dialog_no_internet_body),
                        R.style.AlertDialogTheme);
            case 401:
                return new ErrorDialog(context, context.getResources().getString(R.string.error_dialog_unauth_title),
                        context.getResources().getString(R.string.error_dialog_unauth_body),
                        R.style.AlertDialogTheme);
            case 503:
                return new ErrorDialog(context,
                        context.getResources().getString(R.string.error_dialog_not_running_title),
                        context.getResources().getString(R.string.error_dialog_not_running_body),
                        R.style.AlertDialogTheme);
            default:
                return new ErrorDialog(context, context.getResources().getString(R.string.error_dialog_general_title),
                        context.getResources().getString(R.string.error_dialog_general_body),
                        R.style.AlertDialogTheme);
        }
    }

    public static void createFromGoogleCode(Context context, int code) {
        switch (code) {
            case 7:
            case 13:
            case 16:
                break;
            default:
                new ErrorDialog(context,
                        context.getResources().getString(R.string.error_dialog_general_title),
                        context.getResources().getString(R.string.error_dialog_general_body),
                        R.style.AlertDialogTheme).show();
        }
    }

}

