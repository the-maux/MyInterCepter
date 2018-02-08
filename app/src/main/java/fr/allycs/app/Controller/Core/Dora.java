package fr.allycs.app.Controller.Core;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.DoraProcess;
import fr.allycs.app.View.Activity.Dora.DoraActivity;

public class Dora {
    private String                  TAG = "Dora";
    private static Dora             mInstance = null;
    private DoraActivity            activity;
    private List<DoraProcess>       mListOfHostDored = new ArrayList<>();
    private Singleton               mSingleton = Singleton.getInstance();
    private boolean isRunning = false;

    private Dora(DoraActivity activity) {
        this.activity = activity;
        for (Host host : mSingleton.selectedHostsList) {
            mListOfHostDored.add(new DoraProcess(host));
        }
    }

    public static synchronized Dora getDora(DoraActivity activity) {
        if (mInstance == null) {
            mInstance = new Dora(activity);
        }
        return mInstance;
    }

    public static synchronized Dora getDora(Activity activity) {
        return mInstance;
    }

    public static synchronized boolean  isRunning() {
        if (mInstance == null)
            return false;
        return mInstance.isRunning;
    }

    public void                     reset() {
        if (mInstance != null) {
            mListOfHostDored.clear();
            for (Host host : mSingleton.selectedHostsList) {
                mListOfHostDored.add(new DoraProcess(host));
            }
        }
    }

    public boolean                  onAction() {
        if (!isRunning) {
            isRunning = true;
            for (DoraProcess doraProcess : mListOfHostDored) {
                doraProcess.exec();
            }
            activity.adapterRefreshDeamon();
            Log.d(TAG, "diagnose dora started");
        } else {
            isRunning = false;
            for (DoraProcess doraProcess : mListOfHostDored) {
                RootProcess.kill(doraProcess.mProcess.getmPid());
            }
            Log.d(TAG, "diagnose dora stopped");
        }
        return isRunning;
    }

    public List<DoraProcess>        getmListOfHostDored() {
        return mListOfHostDored;
    }
}
