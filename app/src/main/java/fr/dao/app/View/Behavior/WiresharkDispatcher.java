package fr.dao.app.View.Behavior;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.View.Activity.Wireshark.WiresharkActivity;
import fr.dao.app.View.Widget.Adapter.WiresharkAdapter;

public class                        WiresharkDispatcher  {
    private String                  TAG = "WiresharkDispatcher";
    private java.util.Queue         queue = new java.util.LinkedList();
    private WiresharkActivity       mActivity;
    private RecyclerView.Adapter    mAdapterWireshark;
    private RecyclerView            mRV_Wireshark;
    private ArrayList<Trame>        TrameBuffer = new ArrayList();
    private boolean                 mIsRunning = false, mAutoscroll = true;
    private int                     REFRESH_TIME = 1000;

    private DashboardSniff          mDashboard;
    private boolean                 isDashboardMode = false;

    public                          WiresharkDispatcher(RecyclerView.Adapter adapter,
                                                        RecyclerView recyclerView, WiresharkActivity activity) {
        mAdapterWireshark = adapter;
        mIsRunning = true;
        mActivity = activity;
        mRV_Wireshark = recyclerView;
        adapterRefreshDeamon();
    }

    public ArrayList<Trame>         flush() {
        return TrameBuffer;
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
                        ((WiresharkAdapter) mAdapterWireshark).addTrameOnAdapter(poppedTrame);
                        TrameBuffer.add(poppedTrame);
                        mDashboard.addTrame(poppedTrame);
                    }
                    if (!isDashboardMode) {
//                    Log.d(TAG, "notifyItemRangeInserted(" + size + ");");
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
}
