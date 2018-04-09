package fr.dao.app.View.Behavior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import fr.dao.app.R;

public class                    ViewAnimate {
    private static int          SHORT_DURATION = 250, LONG_DURATION = 800;

    public static void          setVisibilityToGoneQuick(final View view) {
        setVisibilty(view, SHORT_DURATION, View.GONE);
    }
    public static void          setVisibilityToInvisibleQuick(final View view) {
        setVisibilty(view, SHORT_DURATION, View.INVISIBLE);
    }
    public static void          setVisibilityToGoneLong(final View view) {
        setVisibilty(view, LONG_DURATION, View.GONE);
    }
    public static void          setVisibilityToVisibleQuick(final View view) {
        setVisibilty(view, SHORT_DURATION, View.VISIBLE);
    }
    public static void          setVisibilityToVisibleQuick(final View view, int millisecond) {
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

    public static void          FabAnimateReveal(Context context, final FloatingActionButton fab,  final Runnable runnable) {
        Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.fab_scale_up);
        scaleUp.setDuration(LONG_DURATION);
        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }
            public void onAnimationEnd(Animation animation) {
                if (runnable != null) {
                    new Thread(runnable).start();
                }
            }
            public void onAnimationRepeat(Animation animation) {}
        });
        fab.startAnimation(scaleUp);
    }
    public static void          FabAnimateHide(Context context, final FloatingActionButton fab, final Runnable runnable) {
        Animation scaleDown = AnimationUtils.loadAnimation(context, R.anim.fab_scale_down);
        scaleDown.setDuration(LONG_DURATION);
        fab.startAnimation(scaleDown);
        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
                if (runnable != null) {
                    new Thread(runnable).start();
                }
            }
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    public static void          FabAnimateReveal(Context context, final FloatingActionButton fab) {
        Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.fab_scale_up);
        scaleUp.setDuration(LONG_DURATION);
        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }
            public void onAnimationEnd(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
        });
        fab.startAnimation(scaleUp);
    }
    public static void          FabAnimateHide(Context context, final FloatingActionButton fab) {
        Animation scaleDown = AnimationUtils.loadAnimation(context, R.anim.fab_scale_down);
        scaleDown.setDuration(LONG_DURATION);
        fab.startAnimation(scaleDown);
        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
        });
    }

}
