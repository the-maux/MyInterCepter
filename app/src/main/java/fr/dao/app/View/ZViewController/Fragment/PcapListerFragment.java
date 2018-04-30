package fr.dao.app.View.ZViewController.Fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Database.DBManager;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.PcapFileAdapter;

public class                    PcapListerFragment  extends MyFragment {
    private Singleton           mSingleton = Singleton.getInstance();
    private List<File>          files = new ArrayList<>();
    private RecyclerView        mRV_files;
    private PcapFileAdapter     mAdapter;
    private MyActivity          mActivity;
    private Host                mFocusedHost;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_recyclerview, container, false);
        initXml(rootView);
        mActivity = (MyActivity) getActivity();
        init();
        return rootView;
    }

    private void                initXml(View rootView) {
        mRV_files = rootView.findViewById(R.id.RL_items);
    }

    public void                 init() {
        Bundle args = getArguments();
        if (args == null || args.getString("macAddress") == null) {
            initWithNoFocusHost();
        } else {
            initWithFocusHost();
        }
        mRV_files.setAdapter(mAdapter);
        mRV_files.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void                initWithFocusHost() {
        mActivity.showSnackbar("Showing Pcap host host");
        mFocusedHost = DBHost.getDevicesFromMAC(getArguments().getString("macAddress"));
        List<Pcap> pcapList = DBManager.getListPcapFormHost(mFocusedHost);
        for (Pcap pcap : pcapList) {
            File file = pcap.getFile();
            if (file != null && file.exists())
                files.add(pcap.getFile());
        }
        mAdapter = new PcapFileAdapter(getActivity(), files, this);
    }

    private void                initWithNoFocusHost() {
        mActivity.showSnackbar("No host selected => Showing ALL pcaps recorded");
        for (File file : Pcap.getListFiles(new File(mSingleton.Settings.PcapPath))) {
            if (file.getPath().endsWith(".pcap"))
                files.add(file);
        }
        mAdapter = new PcapFileAdapter(getActivity(), files, this);
    }

    public static PcapListerFragment newInstance() {
        return new PcapListerFragment();
    }
}


