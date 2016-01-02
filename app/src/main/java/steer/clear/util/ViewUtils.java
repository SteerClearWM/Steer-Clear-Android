package steer.clear.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by mbpeele on 1/1/16.
 */
public class ViewUtils {

    public final static String BACKGROUND_COLOR = "backgroundColor";
    public final static String SCALE_X = "scaleX";
    public final static String SCALE_Y = "scaleY";
    public final static String ALPHA = "alpha";
    public final static int DEFAULT_VISBILITY_DURATION = 350;

    public static ObjectAnimator invisible(View view, int duration) {
        ObjectAnimator gone = goneAnimator(view);
        gone.removeAllListeners();
        gone.setDuration(duration);
        gone.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        return gone;
    }

    public static void gone(View view, int duration) {
        goneAnimator(view).setDuration(duration).start();
    }

    public static void gone(View view) {
        goneAnimator(view).start();
    }

    public static ObjectAnimator goneAnimator(View view) {
        ObjectAnimator gone = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
        gone.setDuration(DEFAULT_VISBILITY_DURATION);
        gone.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        return gone;
    }

    public static void visible(View view, int duration) {
        visibleAnimator(view).setDuration(duration).start();
    }

    public static void visible(View view) {
        visibleAnimator(view).start();
    }

    public static ObjectAnimator visibleAnimator(View view) {
        ObjectAnimator visibility = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        visibility.setDuration(DEFAULT_VISBILITY_DURATION);
        visibility.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        return visibility;
    }
}
