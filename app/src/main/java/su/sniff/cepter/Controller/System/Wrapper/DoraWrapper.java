package su.sniff.cepter.Controller.System.Wrapper;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Pcap.DoraProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.View.DoraActivity;
import su.sniff.cepter.View.WiresharkActivity;

/**
 * Created by the-maux on 06/10/17.
 */

public class                        DoraWrapper {
    private String                  TAG = "DoraWrapper";
    private static DoraWrapper      mInstance = null;
    private DoraActivity            activity;
    private List<DoraProcess>       mListOfHostDored = new ArrayList<>();
    private Singleton               mSingleton = Singleton.getInstance();
    private boolean                 running = false;

    private                         DoraWrapper(DoraActivity activity) {
        this.activity = activity;
        for (Host host : mSingleton.hostsList) {
            mListOfHostDored.add(new DoraProcess(host));
        }
    }

    public static synchronized DoraWrapper getDora(DoraActivity activity) {
        if (mInstance == null) {
            mInstance = new DoraWrapper(activity);
        }
        return mInstance;
    }

    public static synchronized DoraWrapper getDora(Activity activity) {
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
