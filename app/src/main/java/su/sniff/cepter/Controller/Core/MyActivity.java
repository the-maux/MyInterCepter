package su.sniff.cepter.Controller.Core;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import su.sniff.cepter.Controller.Core.BinaryWrapper.ArpSpoof;
import su.sniff.cepter.R;


public class                    MyActivity extends AppCompatActivity {
    protected String            TAG = this.getClass().getName();
    protected MyActivity        mInstance = this;
    protected Bundle            bundle = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void                 setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupWindowAnimations();
    }

    @Override
    public void                 setContentView(View view) {
        super.setContentView(view);
        setupWindowAnimations();
    }

    @Override
    public void                 setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupWindowAnimations();
    }

    protected void              setupWindowAnimations() {
        Log.d(TAG, "setupWindowAnimations");
        Fade out = new Fade(2);
        out.setDuration(1000);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.myfade);
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(out);

        Slide ret_slide = new Slide();
        ret_slide.setDuration(1000);
    }

    @Override
    public void                 onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void              onDestroy() {
        ArpSpoof.stopArpSpoof();
        super.onDestroy();
    }

    @Override
    public void                 overridePendingTransition(int enterAnim, int exitAnim) {

        super.overridePendingTransition(enterAnim, exitAnim);
    }

    @Override
    public void                     startActivity(Intent intent) {
        Log.d(TAG, "bundle Injected");
        super.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
