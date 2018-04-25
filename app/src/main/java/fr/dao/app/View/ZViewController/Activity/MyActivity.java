package fr.dao.app.View.ZViewController.Activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Target.Host;


public class                    MyActivity extends AppCompatActivity {
    protected String            TAG = this.getClass().getName();
    protected MyActivity        mInstance = this;
    protected Bundle            bundle = null;

    public void                 onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {}

    public void                 setToolbarBackgroundColor(final int color) {}

    public void                 initSettingsButton() {}

    public void                 showSnackbar(String txt) {

    }

    public void                 onHostActualized(ArrayList<Host> hosts) {
        Log.d(TAG, "MyActivity::onHostActualize -> Saved ton Singleton");
        Singleton.getInstance().hostList = hosts;
    }

    protected ProgressBar       mProgressBar;/* Generic ProgressBar mecanics, but can be everywhere*/
    protected int               mProgress = 0;
    public int                  MAXIMUM_PROGRESS = 100;
    public void                 setProgressState(final int progress){
        if (mProgressBar != null) {
            mInstance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.VISIBLE);
                    if (progress != -1) {
                        if (progress >= MAXIMUM_PROGRESS)
                            mProgressBar.setVisibility(View.GONE);
                        mProgress = progress;
                    }
                }
            });
        }
    }


//    public void                 overridePendingTransition(int enterAnim, int exitAnim) {
//        if (Singleton.getInstance().Settings.UltraDebugMode)
//            Log.d(TAG, "overridePendingTransition::(EnterAnim:" + enterAnim + ") & exitAnim(" + exitAnim +")");
//        super.overridePendingTransition(enterAnim, exitAnim);
//    }

}
