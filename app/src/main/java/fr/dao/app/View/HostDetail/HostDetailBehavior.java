package fr.dao.app.View.HostDetail;


import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionMenu;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.R;

public class                        HostDetailBehavior extends CoordinatorLayout.Behavior {
    private String                  TAG = "HostDetailBehavior";
    private int                     hehightImg = -1, widthImg = -1;
    private int                     mini_H = -42, mini_W = -42;
    public float                    X_toGO = -1f, Y_toGO=-1f, X_from=-1f, Y_from=-1f;
    private int                     location_toGO[] = new int[2], location_FROM[] = new int[2];
    private HostDetailBehaviorOffsetChanger imageOffseter;

    public HostDetailBehavior(Context context, AttributeSet attrs){
        super(context, attrs);
        imageOffseter = new HostDetailBehaviorOffsetChanger(context.getResources().getBoolean(R.bool.is_tab));
    }

    public boolean                  layoutDependsOn(final CoordinatorLayout parent, final View child, View dependency){
        //Log.d(TAG, "parent(" + dependency.getClass().getName() + ") child(" + child.getClass().getName() + "):: dependency("+ dependency.getClass().getName()+")");
        if (dependency.getId() == R.id.myView && Y_toGO == -1f && dependency.getLeft() != 0 && dependency.getTop() != 0) {
            dependency.getLocationInWindow(location_toGO);
            X_toGO = location_toGO[0];
            Log.d(TAG, "X_toGO:" + X_toGO);
            Y_toGO = location_toGO[1];
            mini_H = dependency.getHeight();
            mini_W = dependency.getWidth();
        } else if (dependency.getId() == R.id.hostDetailIcon1)
            Log.i(TAG, "hostDetailIcon1 DETECTED");
        else if (dependency.getId() == R.id.hostDetailIcon2)
            Log.i(TAG, "hostDetailIcon2 DETECTED");
        else if (dependency.getId() == R.id.hostDetailIcon3)
            Log.i(TAG, "hostDetailIcon3 DETECTED");
        else if (child.getId() == R.id.OsImg && X_from == -1 && child.getLeft() != 0 && child.getTop() != 0) {
            child.getLocationInWindow(location_FROM);
            hehightImg = child.getLayoutParams().height;
            widthImg = child.getLayoutParams().width;
            X_from = location_FROM[0];
            Y_from = location_FROM[1];
        }

        if (dependency instanceof AppBarLayout && child instanceof CircleImageView) {
            imageOffseter.init((AppBarLayout) dependency, child);
            ((AppBarLayout) dependency).addOnOffsetChangedListener(imageOffseter);
        } else if (dependency instanceof AppBarLayout && child instanceof FloatingActionMenu) {
            imageOffseter.init((AppBarLayout) dependency, child);
            ((AppBarLayout) dependency).addOnOffsetChangedListener(imageOffseter);
        }
        return dependency instanceof AppBarLayout;
    }

    public boolean                  onDependentViewChanged(CoordinatorLayout parent, View child, View dependency){
        if (child instanceof ImageView && dependency instanceof AppBarLayout) {
            return true;
        } else
            return super.onDependentViewChanged(parent, child, dependency);
    }

    private class                   HostDetailBehaviorOffsetChanger implements AppBarLayout.OnOffsetChangedListener {
        private AppBarLayout        parent = null;
        private CircleImageView     imageView = null;
        private FloatingActionMenu  fam = null;
        private int                 actualOffset = 0;
        private boolean             isTablette;

        public HostDetailBehaviorOffsetChanger(boolean tablette) {
            this.isTablette = tablette;
        }


        public void                 init(AppBarLayout parent, View child) {
            this.parent = parent;
            if (child.getClass() == FloatingActionMenu.class)
                fam = (FloatingActionMenu) child;
            if (child.getClass() == CircleImageView.class)
                imageView = (CircleImageView) child;
        }

        public void                 onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            updateOsImage(appBarLayout, verticalOffset);
            updateFAM(appBarLayout, verticalOffset);

        }

        private void                updateFAM(AppBarLayout appBarLayout, int verticalOffset) {
            if (fam != null) {
                float pourcentageScrollTotal = -verticalOffset / (float) appBarLayout.getTotalScrollRange();
                float Y_transition = (Y_from - Y_toGO) * pourcentageScrollTotal;
                fam.setTranslationY(verticalOffset);
            }
        }

        private void                updateOsImage(AppBarLayout appBarLayout, int verticalOffset) {
            if (X_from != -1 && imageView != null) {
                float displacementFraction = -verticalOffset / (float) appBarLayout.getTotalScrollRange();
                float X_transition = (X_from - X_toGO) * (displacementFraction * ((isTablette) ? 1.04f : 1.1f));
                float Y_transition = (Y_from - Y_toGO) * displacementFraction;
                imageView.setTranslationY(-Y_transition);
                imageView.setTranslationX(-X_transition);
                //Log.d(TAG, "onOffsetChanged:" + displacementFraction + " X:" + X_transition + "  ->Y:" + Y_transition);
                boolean isGoDown = actualOffset < verticalOffset;
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                float oppposite = (displacementFraction - 1.0f);
                if ((int) (-oppposite * hehightImg) > (mini_H - 20)) {
                    params.height = (int) (-oppposite * hehightImg);
                    params.width = (int) (-oppposite * widthImg);
                    imageView.setLayoutParams(params);
                } else
                    Log.d(TAG, "opposite:"+oppposite);
                imageView.requestLayout();
                actualOffset = verticalOffset;
            } else
                Log.d(TAG, "X_FROM(" + X_from + ")  imageView:" + ((imageView == null) ? "null" : "setted)"));
        }

    }
}
