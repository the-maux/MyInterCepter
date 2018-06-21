package fr.dao.app.View.Sniff;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.View.ZViewController.Adapter.SniffPacketsAdapter;

public class                        SniffDispatcher {
    private String                  TAG = "SniffDispatcher";
    private java.util.Queue         queue = new java.util.LinkedList();
    private RecyclerView            mRV_Wireshark;
    private boolean                 mIsRunning = false, mAutoscroll = true, isDashboardMode = false;
    private int                     REFRESH_TIME = 800;
    private ArrayList<Trame>        TrameBuffer = new ArrayList();
    private DashboardSniff          mDashboard;
    private RecyclerView.Adapter    mAdapterWireshark;

    public SniffDispatcher(RecyclerView recyclerView, RecyclerView.Adapter adapter, boolean isDashboardMode) {
        mAdapterWireshark = adapter;
        mIsRunning = true;
        mRV_Wireshark = recyclerView;
        this.isDashboardMode = isDashboardMode;
        adapterRefreshDeamon();
    }

    public void                     setDashboard(DashboardSniff dashboard) {
        this.mDashboard = dashboard;
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
            Log.i(TAG, "Dispatcher closing");
        }
    }

    public synchronized void        addToQueue(Trame o) {
        queue.add(o);
        notifyAll();
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

    public int                      flush() {
        //Log.d(TAG, "flushing");
        return ((SniffPacketsAdapter) mAdapterWireshark).flush(TrameBuffer);
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
                        ((SniffPacketsAdapter) mAdapterWireshark).addTrameOnAdapter(poppedTrame);
                        mDashboard.addTrame(poppedTrame);
                    }
                    if (!isDashboardMode) {
                        if (size == 1)
                            mAdapterWireshark.notifyItemInserted(0);
                        else
                            mAdapterWireshark.notifyItemRangeInserted(0, size);
                        if (mAutoscroll)
                            mRV_Wireshark.smoothScrollToPosition(0);
                    } else {
                        mDashboard.notifyAdapterPackets();
                    }
                }
            }
        });
    }
    public void                     switchOutputType(boolean isDashboard) {
        this.isDashboardMode = isDashboard;
    }

    public synchronized void        stop() {
        mIsRunning = false;
    }

    public void                     reset() {
        TrameBuffer.clear();
        mIsRunning = true;
        adapterRefreshDeamon();
    }
}
