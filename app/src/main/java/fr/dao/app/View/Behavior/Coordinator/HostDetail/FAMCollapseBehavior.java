package fr.dao.app.View.Behavior.Coordinator.HostDetail;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

public class FAMCollapseBehavior extends CoordinatorLayout.Behavior {
    private String                  TAG = "FAMCollapseBehavior";

    public FAMCollapseBehavior(){
        super();
    }
    public FAMCollapseBehavior(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public boolean                  layoutDependsOn(final CoordinatorLayout parent, final View child, View dependency){
        if (dependency instanceof AppBarLayout) {
            ((AppBarLayout) dependency).addOnOffsetChangedListener(new FAMOffseter(parent, (FloatingActionMenu)child));
        }
        return dependency instanceof AppBarLayout;
    }

    public boolean                  onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        return child instanceof FloatingActionMenu &&
                dependency instanceof AppBarLayout || super.onDependentViewChanged(parent, child, dependency);
    }

    private class                    FAMOffseter implements AppBarLayout.OnOffsetChangedListener {
        private final CoordinatorLayout parent;
        private final FloatingActionMenu fam;

        public                      FAMOffseter(@NonNull CoordinatorLayout parent, @NonNull FloatingActionMenu child) {
            this.parent = parent;
            this.fam = child;
        }

        public void                 onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (fam.isOpened())
                fam.close(true);
            float displacementFraction = -verticalOffset / (float) appBarLayout.getTotalScrollRange();
            float oppposite = displacementFraction - (float)1.0;
            /*if (oppposite < 0.21f && fam.getVisibility() == View.VISIBLE) {
                fam.setVisibility(View.GONE);/*
                fam.animate()
                        .alpha(0.0f)
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                fam.setVisibility(View.GONE);
                            }
                        });*/
           /* } else if (oppposite > 0.21f && fam.getVisibility() == View.GONE) {
               /* fam.animate()
                        .alpha(1.0f)
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                fam.setVisibility(View.VISIBLE);
                            }
                        });*/
             //   fam.setVisibility(View.VISIBLE);
           /* }*/
            ViewCompat.setElevation(appBarLayout, (float)4.0);
            ViewCompat.setElevation(fam, (float)12.0);

            fam.setTranslationY(verticalOffset);
        }

        public boolean              equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FAMOffseter that = (FAMOffseter) o;
            return parent.equals(that.parent) && fam.equals(that.fam);
        }
    }
}
