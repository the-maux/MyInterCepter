package fr.dao.app.View.Behavior.Coordinator.HostDetail;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class                        ImageCollapseBehavior extends CoordinatorLayout.Behavior {
    private String                  TAG = "ImageCollapseBehavior";
    private int                     hehightImg = -42, widthImg = -42;
    public ImageCollapseBehavior(){
        super();
    }
    public ImageCollapseBehavior(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public boolean                  layoutDependsOn(final CoordinatorLayout parent, final View child, View dependency){
       // Log.d(TAG, "layoutDependsOn::child(" + child.getClass().getName() + ")::parent(" + dependency.getClass().getName() + ")");
        if (dependency instanceof AppBarLayout && child instanceof ImageView) {
            if (hehightImg == -42 && widthImg == -42) {
                hehightImg = child.getLayoutParams().height;
                widthImg = child.getLayoutParams().width;
            }
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

        public void                 onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            float displacementFraction = -verticalOffset / (float) appBarLayout.getTotalScrollRange();
            float oppposite = displacementFraction - (float)1.0;
            Log.d(TAG, "onOffsetChanged:displacementFraction(" + displacementFraction + ")");
            Log.d(TAG, "onOffsetChanged:opposite(" + -oppposite + ")");
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.height = (int) (-oppposite * hehightImg);
            params.width = (int) (-oppposite * widthImg);
            imageView.setLayoutParams(params);
            imageView.setTranslationY(verticalOffset);
            Log.d(TAG, "onOffsetChanged:alpha(" + (-oppposite) + ")");
            imageView.setAlpha(-oppposite);
            imageView.requestLayout();
        }

        public boolean              equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageOffseter that = (ImageOffseter) o;
            return parent.equals(that.parent) && imageView.equals(that.imageView);
        }
    }
}
