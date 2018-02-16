package fr.allycs.app.View.Widget.Fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Adapter.PcapFileAdapter;

public class                PcapListerFragment  extends DialogFragment {
    private Singleton       mSingleton = Singleton.getInstance();
    private List<File>      files = new ArrayList<>();
    private RecyclerView    mRV_files;
    private PcapFileAdapter mAdapter;


    //TODO: TU DOIS FAIRE LE RETOUR DU PCAP
    // juste aprés le onClick sur card_view -> PcapAdapter -> readOnWireshark
    public View             onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_recyclerview, container, false);
        for (File file : getListFiles(new File(mSingleton.PcapPath))) {
            if (file.getPath().endsWith(".pcap"))
                files.add(file);
        }
        mAdapter = new PcapFileAdapter(getActivity(), files);
        mRV_files.setAdapter(mAdapter);
        mRV_files.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    private List<File>      getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().endsWith(".pcap")){
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    public void             showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PcapListerFragment.newInstance().show(ft, "Choose Pcap");
    }

    static PcapListerFragment newInstance() {
        return new PcapListerFragment();
    }

}
