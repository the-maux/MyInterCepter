package fr.dao.app.View.Sniff;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.SniffPacketsAdapter;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    SniffReaderFrgmnt extends MyFragment {
    private String              TAG = "SniffReaderFrgmnt";
    private CoordinatorLayout   mCoordinatorLayout;
    private Context             mCtx;
    private SniffActivity mActivity;
    private RecyclerView        mRV_Wireshark;
    private SniffPacketsAdapter mAdapterWireshark;
    private Tcpdump             mTcpdump;
    private File                mPcapFile;
    ProgressDialog              dialog;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wireshark, container, false);
        mCtx = getActivity();
        initXml(rootView);
        mActivity = (SniffActivity) getActivity();
        mTcpdump = Tcpdump.getTcpdump(mActivity, true);
        init();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mRV_Wireshark = rootView.findViewById(R.id.RV_Wireshark);
    }

    public void                 init() {
        if (getArguments() != null && getArguments().getString("Pcap") != null) {
            mPcapFile = new File(getArguments().getString("Pcap"));
            Log.d(TAG, "reading:" + mPcapFile.getPath());
            initRV();
            dialog = ProgressDialog.show(mActivity, mPcapFile.getName(), "Loading. Please wait...", true);
            dialog.show();
            mTcpdump.readPcap(mPcapFile, this);
        } else {
            Log.e(TAG, "no Pcap returned");
            mActivity.showSnackbar("No Pcap to read",  ContextCompat.getColor(mActivity, R.color.stop_color));
        }
    }

    private void                initRV() {
        mAdapterWireshark = new SniffPacketsAdapter(mActivity, mRV_Wireshark);
        mRV_Wireshark.setAdapter(mAdapterWireshark);
        mRV_Wireshark.setItemAnimator(null);
        mRV_Wireshark.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    public void                 onPcapAnalysed(final ArrayList<Trame> mBufferOfTrame) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mAdapterWireshark.loadListOfTrame(mBufferOfTrame, dialog);
                mActivity.setToolbarTitle(null, mBufferOfTrame.size() + " packets");
            }
        });
    }
    private int rax = 0;
    public synchronized void    loadingMonitor() {
        final String monitor = "Loading. Reading " + rax++ + " packets";
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage(monitor);
            }
        });
    }
}