package fr.allycs.app.View.Behavior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class                    ViewAnimate {
    private static int          SHORT_DURATION = 250, LONG_DURATION = 800;

    public static void          setVisibilityToGoneQuick(final View view) {
        setVisibilty(view, SHORT_DURATION, View.GONE);
    }
    public static void          setVisibilityToGoneLong(final View view) {
        setVisibilty(view, LONG_DURATION, View.GONE);
    }
    public static void          setVisibilityToVisibleQuick(final View view) {
        setVisibilty(view, SHORT_DURATION, View.VISIBLE);
    }
    public static void          setVisibilityToVisibleLong(final View view) {
        setVisibilty(view, LONG_DURATION, View.VISIBLE);
    }
    private static void         setVisibilty(final View view, int duration, final int visibility) {
        view.animate()
                .alpha((visibility == View.VISIBLE) ? 1.0f : 0.0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(visibility);
                    }
                });
    }
}
