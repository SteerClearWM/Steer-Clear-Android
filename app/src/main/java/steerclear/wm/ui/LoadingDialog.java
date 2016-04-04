package steerclear.wm.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import steerclear.wm.R;
import steerclear.wm.ui.view.ViewTypefaceTextView;

/**
 * Created by mbpeele on 4/4/16.
 */
public class LoadingDialog extends ProgressDialog {

    @Bind(R.id.dialog_progress_image)
    ImageView imageView;
    @Bind(R.id.dialog_progress_text)
    ViewTypefaceTextView textView;

    private ObjectAnimator rotateRight;

    public LoadingDialog(Context context) {
        super(context);
        init();
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        getWindow().getAttributes().windowAnimations = R.style.ProgressDialogAnimation;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        ButterKnife.bind(this);
        rotateRight = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f)
                .setDuration(3000);
        rotateRight.setRepeatCount(ValueAnimator.INFINITE);
        rotateRight.setInterpolator(new LinearInterpolator());
        rotateRight.start();
    }
}

