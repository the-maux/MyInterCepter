package fr.dao.app.View.Proxy;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Model.Net.HttpTrame;
import fr.dao.app.View.ZViewController.Adapter.HTTProxyAdapter;

public class                        HTTPDispatcher {
    private String                  TAG = "HTTPDispatcher";
    private java.util.Queue         queue = new java.util.LinkedList();
    private RecyclerView            mRV_Proxy;
    private ArrayList<HttpTrame>    TrameBuffer = new ArrayList();
    private RecyclerView.Adapter    mAdapterProxy;
    private boolean                 mIsRunning, mAutoscroll = true;
    private int                     REFRESH_TIME = 800;

    public                          HTTPDispatcher(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        mAdapterProxy = adapter;
        mIsRunning = true;
        mRV_Proxy = recyclerView;
        adapterRefreshDeamon();
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

    public synchronized void        addToQueue(HttpTrame o) {
        queue.add(o);
        notifyAll();
    }

    private synchronized HttpTrame  pop() {
        HttpTrame msg;
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        msg = (HttpTrame)queue.remove();
        return msg;
    }

    private synchronized void       publishNewTrame() {
        mRV_Proxy.post(new Runnable() {
            public void run() {
                int size = queue.size();
                if (size >= 2) {
                    for (int i = 0; i < size; i++) {
                        HttpTrame poppedTrame = pop();
                        HttpTrame poppedTrame2 = pop();
                        TrameBuffer.add(poppedTrame);
                        ((HTTProxyAdapter) mAdapterProxy).addTrameOnAdapter(poppedTrame, TrameBuffer.size());
                        if (poppedTrame.request.contentEquals(poppedTrame2.request)) {
                            Log.e(TAG, "deleting Doublon ->> [" + poppedTrame.request);
                            Log.e(TAG, "deleting Doublon ->> [" + poppedTrame2.request);
                        } else {
                            TrameBuffer.add(poppedTrame2);
                            ((HTTProxyAdapter) mAdapterProxy).addTrameOnAdapter(poppedTrame2, TrameBuffer.size());
                        }
                    }
                    if (mAutoscroll)
                        mRV_Proxy.smoothScrollToPosition(0);
                }
            }
        });
    }

    public synchronized void        stop() {
        mIsRunning = false;
    }

    public void                     reset() {
        TrameBuffer.clear();
        mIsRunning = true;
        adapterRefreshDeamon();
    }

    public ArrayList<HttpTrame>     getActualTrameStack() {
        return TrameBuffer;
    }
}
