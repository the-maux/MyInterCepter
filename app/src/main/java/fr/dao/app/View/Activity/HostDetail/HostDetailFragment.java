package fr.dao.app.View.Activity.HostDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Activity.HostDiscovery.HostDiscoveryActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Widget.Adapter.ConsoleLogAdapter;


public class                    HostDetailFragment extends MyFragment {
    private String              TAG = "HostNotesFragment";
 //   private CoordinatorLayout   mCoordinatorLayout;
    private Host                mFocusedHost;//TODO need to be init
    private Context             mCtx;
    private HostDiscoveryActivity mActivity;
    private RecyclerView        mRV;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initXml(rootView);
        init();
        mCtx = getActivity();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mRV = rootView.findViewById(R.id.list);
    }

    public void                 init() {
        if (mSingleton.hostList == null) {
         //   Snackbar.make(mCoordinatorLayout, "No target saved, You need to scan the Network", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), HostDiscoveryActivity.class));
            getActivity().onBackPressed();
            return;
        }
        mFocusedHost = mSingleton.hostList.get(0);
        ArrayList<String[]> arrayList = buildInfoArray();
        ConsoleLogAdapter adapter = new ConsoleLogAdapter(arrayList);
        mRV.setAdapter(adapter);
        mRV.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        mRV.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRV.getContext(),
                manager.getOrientation());
        mRV.addItemDecoration(dividerItemDecoration);
    }

    private ArrayList<String[]> buildInfoArray() {
        ArrayList<String[]> arrayList = new ArrayList<>();
        String[] title1 = {"Name", mFocusedHost.name};
        String[] title2 = {"IP Address", mFocusedHost.ip};
        String[] title3 = {"MAC Address", mFocusedHost.mac};
        String[] title4 = {"Hostname", mFocusedHost.name};
        String[] title5 = {"MAC Vendor", mFocusedHost.vendor};
        String[] title6 = {"NetBIOS Domain", ""};
        String[] title7 = {"NetBIOS Name", ""};
        String[] title8 = {"NetBIOS Role", ""};
        String[] title9 = {"First seen", ""};
        String[] title10 = {"Brand and Model", ""};
        String[] title11 = {"Bonjour Name", ""};
        String[] title12 = {"Bonjour Services", ""};
        String[] title13 = {"UPnP Name", ""};
        String[] title14 = {"UPnP Device", ""};
        String[] title15 = {"UPnP Services", ""};
        arrayList.add(title1);
        arrayList.add(title2);
        arrayList.add(title3);
        arrayList.add(title4);
        arrayList.add(title5);
        arrayList.add(title6);
        arrayList.add(title7);
        arrayList.add(title8);
        arrayList.add(title9);
        arrayList.add(title10);
        arrayList.add(title11);
        arrayList.add(title12);
        arrayList.add(title13);
        arrayList.add(title14);
        arrayList.add(title15);
        return arrayList;
    }

}