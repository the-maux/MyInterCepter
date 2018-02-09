package fr.allycs.app.View.Behavior;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import fr.allycs.app.Model.Net.Trame;
import fr.allycs.app.View.Activity.Wireshark.WiresharkActivity;
import fr.allycs.app.View.Widget.Adapter.WiresharkAdapter;

public class                        WiresharkDispatcher  {
    private String                  TAG = "WiresharkDispatcher";
    private WiresharkActivity       mActivity;
    private RecyclerView.Adapter    mAdapterWireshark;
    private RecyclerView            mRV_Wireshark;
    private ArrayList               tmp = new ArrayList();
    private boolean                 mIsRunning = false, mAutoscroll = true;
    private int                     REFRESH_TIME = 1000;// == 1seconde
    private java.util.Queue         queue = new java.util.LinkedList();
    private boolean                 acknowledged = false;

    public                          WiresharkDispatcher(RecyclerView.Adapter adapter,
                                                        RecyclerView recyclerView, WiresharkActivity activity) {
        mAdapterWireshark = adapter;
        mIsRunning = true;
        mActivity = activity;
        mRV_Wireshark = recyclerView;
        new Thread(new Runnable() {
            @Override
            public void run() {
                adapterRefreshDeamon();
            }
        }).start();
    }

    private void                 adapterRefreshDeamon() {
        if (mIsRunning) {
            publishNewTrame();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Log.d(TAG, "adapterRefreshDeamon");
                    adapterRefreshDeamon();
                }
            }, REFRESH_TIME);
        }
    }

    private synchronized void   publishNewTrame() {
        Log.d(TAG, "publishNewTrame");
        if (!acknowledged) {
            Log.d(TAG, "ack send to Activity");
            mActivity.connectionSucceed();
            acknowledged = true;
        }
        mRV_Wireshark.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < queue.size(); i++) {
                    tmp.add(pop());
                }
                Log.d(TAG, "addTrameOnAdapter: " +  tmp.size()  + " trames");
                for (Object o : tmp) {
                    ((WiresharkAdapter)mAdapterWireshark).addTrameOnAdapter((Trame)o);
                }
                tmp.clear();
                mAdapterWireshark.notifyDataSetChanged();
                Log.d(TAG, "notify adapter changing");
                if (mAutoscroll) {
                    mRV_Wireshark.smoothScrollToPosition(0);
                }
            }
        });
    }

    private synchronized Trame   pop() {
        Log.d(TAG, "pop");
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

    public synchronized void    addToQueue(Trame o) {
        Log.d(TAG, "addToqueue");
        queue.add(o);
        notifyAll();
    }
}
