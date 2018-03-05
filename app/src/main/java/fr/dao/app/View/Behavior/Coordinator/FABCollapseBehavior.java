package fr.dao.app.View.Behavior.Coordinator;


import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

public class                        FABCollapseBehavior extends CoordinatorLayout.Behavior {
    private String                  TAG = "FABCollapseBehavior";

    public                          FABCollapseBehavior(){
        super();
    }
    public                          FABCollapseBehavior(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public boolean                  layoutDependsOn(final CoordinatorLayout parent, final View child, View dependency){
        if (dependency instanceof AppBarLayout) {
            ((AppBarLayout) dependency).addOnOffsetChangedListener(new FabOffsetter(parent, (FloatingActionMenu)child));
         /*   ((AppBarLayout) dependency).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    Log.d(TAG, "offsetChange:[" + verticalOffset + "]");
                    float displacementFraction = -verticalOffset / (float) appBarLayout.getHeight();
                    float fabTranslationY = (parent.getBottom() - fabTopNormal) * displacementFraction;

                }
            });*/
        }
        return dependency instanceof AppBarLayout;
    }

    public boolean                  onDependentViewChanged(CoordinatorLayout parent, View child, View dependency){
        if (child instanceof FloatingActionMenu && dependency instanceof AppBarLayout) {
        //    Log.d(TAG, "dependency::dependency.getHeight()::[" + dependency.getHeight() +"]");
         //   Log.d(TAG, "dependency::dependency.getTop()::[" + dependency.getTop()+"]");
         //   Log.d(TAG, "dependency::dependency.getBottom()::[" + dependency.getBottom()+"]");
       //     float tY = dependency.getTranslationY() - dependency.getHeight();
         //   Log.d(TAG, "dependency::translation::[" + tY +"]");
         //   child.setTranslationY(tY);
            return true;
        } else
            return super.onDependentViewChanged(parent, child, dependency);
    }
}
