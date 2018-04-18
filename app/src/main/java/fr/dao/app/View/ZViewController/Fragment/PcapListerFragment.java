package fr.dao.app.View.ZViewController.Fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.PcapFileAdapter;

    public class                PcapListerFragment  extends DialogFragment {
    private Singleton       mSingleton = Singleton.getInstance();
    private List<File>      files = new ArrayList<>();
    private RecyclerView    mRV_files;
    private PcapFileAdapter mAdapter;

    public View             onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_recyclerview, container, false);
        initXml(rootView);
        init();
        return rootView;
    }

    private void            initXml(View rootView) {
        mRV_files = rootView.findViewById(R.id.RL_items);
    }

    private void            init() {
        for (File file : Pcap.getListFiles(new File(mSingleton.Settings.PcapPath))) {
            if (file.getPath().endsWith(".pcap"))
                files.add(file);
        }
        mAdapter = new PcapFileAdapter(getActivity(), files, this);
        mRV_files.setAdapter(mAdapter);
        mRV_files.setLayoutManager(new LinearLayoutManager(getActivity()));
    }



    public static PcapListerFragment newInstance() {
        return new PcapListerFragment();
    }

}
