package fr.dao.app.View.ZViewController.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import java.util.ArrayList;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Target.Host;


public class                    MyActivity extends AppCompatActivity {
    protected String            TAG = "MyActivity";
    protected MyActivity        mInstance = this;
    protected Bundle            bundle = null;

    public void                 onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {}

    public void                 setToolbarBackgroundColor(final int color) {}

    public void                 setStatusBarColor(final int color) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(color));

    }

    public void                 hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mInstance.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = mInstance.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(mInstance);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void                 showKeyboard() {
        InputMethodManager imm = (InputMethodManager) mInstance.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void                 initSettingsButton() {}

    public void                 showSnackbar(String txt) {

    }

    public void                 onHostActualized(ArrayList<Host> hosts) {
        if (hosts != null && hosts.size() > 1) {
            Singleton.getInstance().hostList = hosts;
            Log.d(TAG, "onHostActualize -> Saved to Singleton");
        } else {
            Log.e(TAG, "onHostActualize -> Not Saved to Singleton cause is null or empty");
        }
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
