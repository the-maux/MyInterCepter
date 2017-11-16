package su.sniff.cepter.Controller.Core.BinaryWrapper;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Model.Unix.DoraProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.View.Dora.DoraActivity;

/**
 * Created by the-maux on 06/10/17.
 */

public class Dora {
    private String                  TAG = "Dora";
    private static Dora mInstance = null;
    private DoraActivity            activity;
    private List<DoraProcess>       mListOfHostDored = new ArrayList<>();
    private Singleton               mSingleton = Singleton.getInstance();
    private boolean                 running = false;

    private Dora(DoraActivity activity) {
        this.activity = activity;
        for (Host host : mSingleton.hostsList) {
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

    public void                     reset() {
        if (mInstance != null) {
            mListOfHostDored.clear();
            for (Host host : mSingleton.hostsList) {
                mListOfHostDored.add(new DoraProcess(host));
            }
        }
    }

    public boolean                  onAction() {
        if (!running) {
            running  = true;
            for (DoraProcess doraProcess : mListOfHostDored) {
                doraProcess.exec();
            }
            activity.adapterRefreshDeamon();
            Log.d(TAG, "diagnose dora started");
        } else {
            running  = false;
            for (DoraProcess doraProcess : mListOfHostDored) {
                RootProcess.kill(doraProcess.mProcess.getPid());
            }
            Log.d(TAG, "diagnose dora stopped");
        }
        return running;
    }

    public boolean                  isRunning() {
        return running;
    }

    public List<DoraProcess>        getmListOfHostDored() {
        return mListOfHostDored;
    }
}
