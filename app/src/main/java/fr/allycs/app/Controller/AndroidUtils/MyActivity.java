package fr.allycs.app.Controller.AndroidUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import fr.allycs.app.Controller.Core.ArpSpoof;


public class                    MyActivity extends AppCompatActivity {
    protected String            TAG = this.getClass().getName();
    protected MyActivity        mInstance = this;
    protected Bundle            bundle = null;

    public void                 onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState, persistentState);
    }

    public void                 setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupWindowAnimations();
    }

    public void                 setContentView(View view) {
        super.setContentView(view);
        setupWindowAnimations();
    }

    public void                 setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupWindowAnimations();
    }

    protected void              setupWindowAnimations() {
/*        Log.d(TAG, "setupWindowAnimations");
        Fade out = new Fade(2);
        out.setDuration(1000);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.myfade);
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        if (splashscreen)
            getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(out);
        Slide ret_slide = new Slide();
        ret_slide.setDuration(1000);*/
    }

    public void                 onBackPressed() {
        super.onBackPressed();
    }

    public void                 overridePendingTransition(int enterAnim, int exitAnim) {

        super.overridePendingTransition(enterAnim, exitAnim);
    }

    public void                 startActivity(Intent intent) {
//        Log.d(TAG, "bundle Injected");
//        ActivityOptions.
        super.startActivity(intent/*, ActivityOptions.makeSceneTransitionAnimation(this).toBundle()*/);
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {}

    public void                 initToolbarButton() {}

    protected void              onDestroy() {
        ArpSpoof.stopArpSpoof();
        super.onDestroy();
    }
}
