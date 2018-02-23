package fr.dao.app.View.Behavior.Activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import fr.dao.app.Core.Configuration.Singleton;


public class                    MyActivity extends AppCompatActivity {
    protected String            TAG = this.getClass().getName();
    protected MyActivity        mInstance = this;
    protected Bundle            bundle = null;

    public void                 onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void                 overridePendingTransition(int enterAnim, int exitAnim) {
        if (Singleton.getInstance().UltraDebugMode)
            Log.d(TAG, "overridePendingTransition::(EnterAnim:" + enterAnim + ") & exitAnim(" + exitAnim +")");
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {}

    public void                 setToolbarBackgroundColor(final int color) {}

    public void                 initSettingsButton() {}

}
