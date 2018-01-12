package fr.allycs.app.View.HostDiscovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.R;

public class                        FragmentHistoric extends Fragment {
    private String                  TAG = "FragmentHistoric";
    private HostDiscoveryActivity   mActivity;
    private Singleton               mSingleton = Singleton.getInstance();
    private RecyclerView            mHost_RV;
    private TextView                mEmptyList;

    @Override public View           onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hostdiscovery_scan, container, false);
        initXml(rootView);
        this.mActivity = (HostDiscoveryActivity) getActivity();
        return rootView;
    }

    private void                    initXml(View rootView) {
        mHost_RV = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mEmptyList = (TextView) rootView.findViewById(R.id.emptyList);
    }

    private void                    initHistoricRecyclerView() {
        //TODO: Create Historic
//        mHost_RV.setAdapter(mHostAdapter);
        mHost_RV.setHasFixedSize(true);
        mHost_RV.setLayoutManager(new LinearLayoutManager(mActivity));
    }

}
