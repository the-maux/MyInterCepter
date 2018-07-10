package fr.dao.app.View.ZViewController.Coordinator.HostDetail;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import fr.dao.app.R;

public class                        ImageCollapseBehavior extends CoordinatorLayout.Behavior {
    private String                  TAG = "ImageCollapseBehavior";
    private int                     hehightImg = -1, widthImg = -1;
    private int                     mini_H = -42, mini_W = -42;
    public float                    X_toGO = -1f, Y_toGO=-1f, X_from=-1f, Y_from=-1f;
    int                             location_toGO[] = new int[2], location_FROM[] = new int[2];

    public ImageCollapseBehavior(){
        super();
    }
    public ImageCollapseBehavior(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public boolean                  layoutDependsOn(final CoordinatorLayout parent, final View child, View dependency){
        //Log.d(TAG, "parent(" + dependency.getClass().getName() + ") child(" + child.getClass().getName() + "):: dependency("+ dependency.getClass().getName()+")");
        if (dependency.getId() == R.id.myView && Y_toGO == -1f && dependency.getLeft() != 0 && dependency.getTop() != 0) {
            dependency.getLocationInWindow(location_toGO);
            X_toGO = location_toGO[0];
            Y_toGO = location_toGO[1];
            mini_H = dependency.getHeight();
            mini_W = dependency.getWidth();
            Log.e(TAG, "TAG1");
        }

        if (child.getId() == R.id.OsImg && X_from == -1 && child.getLeft() != 0 && child.getTop() != 0) {
            child.getLocationInWindow(location_FROM);
            hehightImg = child.getLayoutParams().height;
            widthImg = child.getLayoutParams().width;
            X_from = location_FROM[0];
            Y_from = location_FROM[1];
            Log.e(TAG, "TAG2");
        }

        if (dependency instanceof AppBarLayout && child instanceof ImageView) {
            ((AppBarLayout) dependency).addOnOffsetChangedListener(new ImageOffseter(parent, (ImageView) child));
        }
        return dependency instanceof AppBarLayout;
    }

    public boolean                  onDependentViewChanged(CoordinatorLayout parent, View child, View dependency){
        if (child instanceof ImageView && dependency instanceof AppBarLayout) {
            return true;
        } else
            return super.onDependentViewChanged(parent, child, dependency);
    }

    private class                    ImageOffseter implements AppBarLayout.OnOffsetChangedListener {
        private final CoordinatorLayout parent;
        private final ImageView imageView;

        public                      ImageOffseter(@NonNull CoordinatorLayout parent, @NonNull  ImageView child) {
            this.parent = parent;
            this.imageView = child;
        }
        private int actualOffset = 0;
        public void                 onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (X_from != -1) {
                float displacementFraction = -verticalOffset / (float) appBarLayout.getTotalScrollRange();
                float X_transition = (X_from - X_toGO) * displacementFraction;
                float Y_transition = (Y_from - Y_toGO) * displacementFraction;
                imageView.setTranslationY(-Y_transition);
                imageView.setTranslationX(-X_transition);
                Log.d(TAG, "onOffsetChanged:" + displacementFraction + " X:" + X_transition + "  ->Y:" + Y_transition);
                boolean isGoDown = actualOffset < verticalOffset;
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                float oppposite = (displacementFraction - 1.0f);
                if ((int) (-oppposite * hehightImg) > mini_H) {
                    params.height = (int) (-oppposite * hehightImg);
                    params.width = (int) (-oppposite * widthImg);
                    imageView.setLayoutParams(params);
                }
                imageView.requestLayout();
                actualOffset = verticalOffset;
            }
        }

        public boolean              equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageOffseter that = (ImageOffseter) o;
            return parent.equals(that.parent) && imageView.equals(that.imageView);
        }
    }
}
