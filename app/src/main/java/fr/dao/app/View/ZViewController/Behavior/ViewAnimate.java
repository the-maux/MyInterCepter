package fr.dao.app.View.ZViewController.Behavior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;

public class                    ViewAnimate {
    private static int          SHORT_DURATION = 250, LONG_DURATION = 350;

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
        try {
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
        } catch (AndroidRuntimeException e) {
            e.printStackTrace();

        }
    }
    public static void          FadeAnimateReveal(final MyActivity context, final View fab, final Runnable runnable) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                Animation scaleUp = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                scaleUp.setDuration(LONG_DURATION);
                scaleUp.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        fab.setVisibility(View.VISIBLE);
                    }
                    public void onAnimationEnd(Animation animation) {
                        if (runnable != null) {
                            context.runOnUiThread(new Runnable() {
                                public void run() {
                                    runnable.run();
                                }
                            });
                        }
                    }

                    public void onAnimationRepeat(Animation animation) {}
                });
                scaleUp.setFillEnabled(true);
                scaleUp.setFillBefore(true);
                fab.startAnimation(scaleUp);
            }
        });
    }
    public static void          FabAnimateReveal(final MyActivity context, final View fab, final Runnable runnable) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.fab_scale_up);
                scaleUp.setDuration(SHORT_DURATION);
                scaleUp.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        fab.setVisibility(View.VISIBLE);
                    }
                    public void onAnimationEnd(Animation animation) {
                        if (runnable != null) {
                            context.runOnUiThread(new Runnable() {
                                public void run() {

                                    new Thread(runnable).start();
                                }
                            });
                        }
                    }
                    public void onAnimationRepeat(Animation animation) {}
                });
                fab.startAnimation(scaleUp);
            }
        });
    }
    public static void          FabAnimateHide(Context context, final View fab, final Runnable runnable) {
        Animation scaleDown = AnimationUtils.loadAnimation(context, R.anim.fab_scale_down);
        scaleDown.setDuration(LONG_DURATION);
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
        fab.startAnimation(scaleDown);
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
        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
        });
        fab.startAnimation(scaleDown);
    }

    public static void          scaleUp(Context context, final View mMenuFAB) {
        Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.fab_scale_up);
        scaleUp.setDuration(1250);
        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mMenuFAB.setVisibility(View.VISIBLE);
            }
            public void onAnimationEnd(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
        });
        mMenuFAB.startAnimation(scaleUp);
    }
}
