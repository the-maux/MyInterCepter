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
        //Log.d(TAG, "layoutDependsOn::child(" + child.getClass().getName() + ")::parent(" + dependency.getClass().getName() + ")");
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
        private int actualOffset = 0;
        public void                 onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            float displacementFraction = -verticalOffset / (float) appBarLayout.getTotalScrollRange();
            Log.d(TAG, "onOffsetChanged:" + appBarLayout.getScrollY() + "----" + verticalOffset);
//            Log.d(TAG, "onOffsetChanged:displacementFraction(" + displacementFraction + ")");
//            Log.d(TAG, "onOffsetChanged:opposite(" + -oppposite + ")");
            boolean isGoDown = actualOffset < verticalOffset;
            if (verticalOffset < 100) {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                if (params.height > 58 || isGoDown) {

                }
                Log.d(TAG, "params.height[" + params.height+ "] && imgTop+v[" +(imageView.getY() + verticalOffset) +"]");
                if (appBarLayout.getY() < (imageView.getY() + (verticalOffset * 0.6f)) || isGoDown)
                    imageView.setTranslationY(verticalOffset * 0.6f);
                if (appBarLayout.getX() < (imageView.getX() + (verticalOffset)) || isGoDown) {
                    float oppposite = displacementFraction - (float)1.0;
                    params.height = (int) (-oppposite * hehightImg);
                    params.width = (int) (-oppposite * widthImg)    ;
                    imageView.setLayoutParams(params);
                    imageView.setTranslationX(verticalOffset * 1.8f);
                }
                //
//  Log.d(TAG, "onOffsetChanged:alpha(" + (-oppposite) + ")");
                //imageView.setAlpha(-oppposite);
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
