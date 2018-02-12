package fr.allycs.app.View.Behavior.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class                    MyActivity extends AppCompatActivity {
    protected String            TAG = this.getClass().getName();
    protected MyActivity        mInstance = this;
    protected Bundle            bundle = null;

    public void                 onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState, persistentState);
    }

    public void                 onBackPressed() {
        super.onBackPressed();
    }

    public void                 overridePendingTransition(int enterAnim, int exitAnim) {
        Log.d(TAG, "overridePendingTransition::(EnterAnim:" + enterAnim + ") & exitAnim(" + exitAnim +")");
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    public void                 startActivity(Intent intent) {
//        Log.d(TAG, "bundle Injected");
//        ActivityOptions.
        super.startActivity(intent/*, ActivityOptions.makeSceneTransitionAnimation(this).toBundle()*/);
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {}

    public void                 setToolbarBackgroundColor(final int color) {}

    public void                 initSettingsButton() {}

    protected void              onDestroy() {
        super.onDestroy();
    }
}
