package steer.clear.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import steer.clear.R;
import steer.clear.activity.ActivityAuthenticate;
import steer.clear.view.ViewTypefaceTextView;

/**
 * Created by Miles Peele on 8/19/2015.
 */
public class ErrorDialog extends Dialog implements View.OnClickListener, DialogInterface.OnDismissListener {

    @Bind(R.id.error_dialog_title) ViewTypefaceTextView title;
    @Bind(R.id.error_dialog_body) ViewTypefaceTextView body;
    @Bind(R.id.error_dialog_pos_button) Button posButton;

    private String titleText;
    private String bodyText;

    protected ErrorDialog(Context context, String titleText, String bodyText, int theme) {
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
                if (titleText.equals(getContext().getResources().getString(R.string.error_dialog_unauth_title))) {
                    getContext().startActivity(
                            ActivityAuthenticate.newIntent(getContext(), true),
                            ActivityOptionsCompat.makeCustomAnimation(getContext(),
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out).toBundle());
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                } else {
                    dismiss();
                }
                break;
        }
    }

    public static ErrorDialog createFromHttpErrorCode(Context context, int code) {
        switch (code) {
            case 400:
                return new ErrorDialog(context,
                        context.getResources().getString(R.string.error_dialog_bad_creds_title),
                        context.getResources().getString(R.string.error_dialog_bad_creds_body),
                        R.style.DialogTheme);
            case 409:
                return new ErrorDialog(context, context.getResources().getString(R.string.error_dialog_user_exists_title),
                        context.getResources().getString(R.string.error_dialog_user_exists_body),
                        R.style.DialogTheme);
            case 404:
                return new ErrorDialog(context, context.getResources().getString(R.string.error_dialog_no_internet_title),
                        context.getResources().getString(R.string.error_dialog_no_internet_body),
                        R.style.DialogTheme);
            case 401:
                return new ErrorDialog(context, context.getResources().getString(R.string.error_dialog_unauth_title),
                        context.getResources().getString(R.string.error_dialog_unauth_body),
                        R.style.DialogTheme);
            default:
                return new ErrorDialog(context, context.getResources().getString(R.string.error_dialog_general_title),
                        context.getResources().getString(R.string.error_dialog_general_body),
                        R.style.DialogTheme);
        }
    }

    public static void createFromGoogleCode(Context context, int code) {
        switch (code) {
            case 7:
                break;
            default:
                new ErrorDialog(context,
                        context.getResources().getString(R.string.error_dialog_general_title),
                        context.getResources().getString(R.string.error_dialog_general_body),
                        R.style.DialogTheme).show();
        }
    }

}

