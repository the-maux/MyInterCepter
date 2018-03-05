package fr.dao.app.View.Behavior.Coordinator;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;

import com.github.clans.fab.FloatingActionMenu;

public class                    FabOffsetter implements AppBarLayout.OnOffsetChangedListener {
    private float fabTranslationYByThis = 0.0f;

    private final CoordinatorLayout parent;
    private final FloatingActionMenu fab;

    public                      FabOffsetter(@NonNull CoordinatorLayout parent, @NonNull FloatingActionMenu child) {
        this.parent = parent;
        this.fab = child;
    }

    public void                 onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        // fab should scroll out down in sync with the appBarLayout scrolling out up.
        // fab should scroll out down in sync with the appBarLayout scrolling out up.
        // let's see how far along the way the appBarLayout is
        // (if displacementFraction == 0.0f then no displacement, appBar is fully expanded;
        //  if displacementFraction == 1.0f then full displacement, appBar is totally collapsed)
        float displacementFraction = -verticalOffset / (float) appBarLayout.getTotalScrollRange();

        // top position, accounting for translation not coming from this behavior
        float topUntranslatedFromThis = fab.getTop() + fab.getTranslationY() - fabTranslationYByThis;

        // total length to displace by (from position uninfluenced by this behavior) for a full appBar collapse
        float fullDisplacement = parent.getBottom() - topUntranslatedFromThis;

        // calculate new value for displacement coming from this behavior
        float newTranslationYFromThis = fullDisplacement * displacementFraction;

        // update translation value by difference found in this step
        fab.setTranslationY(newTranslationYFromThis - fabTranslationYByThis + fab.getTranslationY());

        // store new value
        fabTranslationYByThis = newTranslationYFromThis;
    }

    public boolean              equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FabOffsetter that = (FabOffsetter) o;

        return parent.equals(that.parent) && fab.equals(that.fab);

    }

    public int                  hashCode() {
        int result = parent.hashCode();
        result = 31 * result + fab.hashCode();
        return result;
    }
}
