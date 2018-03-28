package fr.dao.app.View.Behavior;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.View.Activity.Wireshark.WiresharkActivity;
import fr.dao.app.View.Widget.Adapter.WiresharkPacketsAdapter;

public class                        WiresharkDispatcher  {
    private String                  TAG = "WiresharkDispatcher";
    private java.util.Queue         queue = new java.util.LinkedList();
    private WiresharkActivity       mActivity;
    private RecyclerView            mRV_Wireshark;
    private boolean                 mIsRunning = false, mAutoscroll = true, isDashboardMode;
    private int                     REFRESH_TIME = 1000;
    private ArrayList<Trame>        TrameBuffer = new ArrayList();
    private DashboardSniff          mDashboard;
    private RecyclerView.Adapter    mAdapterWireshark;


    public                          WiresharkDispatcher(RecyclerView.Adapter adapter, boolean isDashboardMode,
                                                        RecyclerView recyclerView, WiresharkActivity activity) {
        mAdapterWireshark = adapter;
        mIsRunning = true;
        mActivity = activity;
        mRV_Wireshark = recyclerView;
        this.isDashboardMode = isDashboardMode;
        adapterRefreshDeamon();
    }

    public int                      flush() {
        //Log.d(TAG, "flushing");
        return ((WiresharkPacketsAdapter) mAdapterWireshark).flush(TrameBuffer);
    }

    private void                    adapterRefreshDeamon() {
        if (mIsRunning) {
            publishNewTrame();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    adapterRefreshDeamon();
                }
            }, REFRESH_TIME);
        } else {
            Log.d(TAG, "Dispatcher closing");
        }
    }

    private synchronized void       publishNewTrame() {
        mRV_Wireshark.post(new Runnable() {
            @Override
            public void run() {
                int size = queue.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        Trame poppedTrame = pop();
                        TrameBuffer.add(poppedTrame);
                        if (poppedTrame != null) {
                            poppedTrame.offsett = TrameBuffer.indexOf(poppedTrame);
                        }
                        ((WiresharkPacketsAdapter) mAdapterWireshark).addTrameOnAdapter(poppedTrame);
                        mDashboard.addTrame(poppedTrame);
                    }
                    if (!isDashboardMode) {
//                        Log.d(TAG, "notifyItemRangeInserted(" + size + ");");
                        if (size == 1)
                            mAdapterWireshark.notifyItemInserted(0);
                        else
                            mAdapterWireshark.notifyItemRangeInserted(0, size);
                        if (mAutoscroll)
                            mRV_Wireshark.smoothScrollToPosition(0);
                    }
                }
            }
        });
    }

    private synchronized Trame      pop() {
        Trame msg;
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        msg = (Trame)queue.remove();
        return msg;
    }

    public synchronized void        addToQueue(Trame o) {
        queue.add(o);
        notifyAll();
    }

    public synchronized void        stop() {
        mIsRunning = false;
    }

    public void                     setDashboard(DashboardSniff dashboard) {
        this.mDashboard = dashboard;
    }

    public void                     switchOutputType(boolean isDashboard) {
        this.isDashboardMode = isDashboard;
    }
}
