package fr.dao.app.Core;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Unix.DoraProcess;
import fr.dao.app.View.Dora.DoraActivity;

public class                        Dora {
    private String                  TAG = "Dora";
    private static Dora             mInstance = null;
    private DoraActivity            activity;
    private List<DoraProcess>       mListOfHostDored = new ArrayList<>();
    private Singleton               mSingleton = Singleton.getInstance();
    private boolean                 isRunning = false;

    private Dora(DoraActivity activity) {
        this.activity = activity;
    }

    public static synchronized Dora getDora(DoraActivity activity) {
        if (mInstance == null) {
            mInstance = new Dora(activity);
        }
        return mInstance;
    }

    public static synchronized boolean  isRunning() {
        return mInstance != null && mInstance.isRunning;
    }

    public void                     reset() {
        if (mInstance != null) {
            mListOfHostDored.clear();
            for (Host host : mSingleton.hostList) {
                if (!host.name.contains("My Device") && !host.ip.contentEquals(mSingleton.network.gateway))
                    mListOfHostDored.add(new DoraProcess(host));
            }
        }
    }

    public int                      onAction() {
        mSingleton.actualNetwork.defensifAction = mSingleton.actualNetwork.defensifAction + 1;
        mSingleton.actualNetwork.save();
        if (mListOfHostDored.isEmpty())
           reset();
        if (!isRunning) {
            isRunning = true;
            Log.d(TAG, "dora started " + mListOfHostDored.size() + " process");
            for (DoraProcess doraProcess : mListOfHostDored) {
                doraProcess.exec();
            }
            activity.adapterRefreshDeamon();

        }
        return mListOfHostDored.size();
    }

    public int                     onStop() {
        isRunning = false;
        for (DoraProcess doraProcess : mListOfHostDored) {
            RootProcess.kill(doraProcess.mProcess.getmPid());
        }
        Log.d(TAG, "dora stopped " + mListOfHostDored.size() + " process");
        return mListOfHostDored.size();
    }

    public List<DoraProcess>        getmListOfHostDored() {
        if (mListOfHostDored.isEmpty() && mSingleton.hostList != null && !mSingleton.hostList.isEmpty())
            reset();
        return mListOfHostDored;
    }
}
