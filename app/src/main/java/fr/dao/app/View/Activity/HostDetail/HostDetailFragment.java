package fr.dao.app.View.Activity.HostDetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Widget.Adapter.HostDetailAdapter;


public class                    HostDetailFragment extends MyFragment {
    private String              TAG = "HostNotesFragment";
 //   private CoordinatorLayout   mCoordinatorLayout;
    private Host                mFocusedHost;//TODO need to be init
    private Context             mCtx;
    private HostDetailActivity mActivity;
    private RecyclerView        mRV;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initXml(rootView);
        init();
        mActivity = (HostDetailActivity) getActivity();
        mCtx = getActivity();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mRV = rootView.findViewById(R.id.list);
    }

    HostDetailAdapter adapter;
    public void                 init() {
        Bundle args = getArguments();
        if (args == null || args.getString("macAddress") == null) {
            mActivity.showSnackbar("Error in focus device (no MAC) in bundle");
        } else {
            adapter = new HostDetailAdapter((MyActivity)getActivity());
            mFocusedHost = DBHost.getDevicesFromMAC(args.getString("macAddress"));
            mFocusedHost.dumpMe();
            mRV.setAdapter(adapter);
            LinearLayoutManager manager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(manager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRV.getContext(), manager.getOrientation());
            mRV.addItemDecoration(dividerItemDecoration);
            adapter.updateList(buildInfoArray(), mFocusedHost);
        }
    }

    public void                 onResume() {
        super.onResume();
        init();
    }

    private ArrayList<String[]> buildInfoArray() {
        ArrayList<String[]> arrayList = new ArrayList<>();
        buildBasicInfos(arrayList);
        buildUpnP(arrayList);
        buildNetBIOS(arrayList);
        buildBonjour(arrayList);
        return arrayList;
    }

    private void                buildBasicInfos(ArrayList<String[]> arrayList) {
        String[] title1 = {"Name", mFocusedHost.name};
        arrayList.add(title1);
        String[] titleOs = {"Operating System", mFocusedHost.osType.name()};
        arrayList.add(titleOs);
        if (!mFocusedHost.osDetail.contains("Unknown")) {
            String[] titleOsDetail = {"Os Detail", mFocusedHost.osDetail};
            arrayList.add(titleOsDetail);
        }
        String[] title2 = {"IP Address", mFocusedHost.ip};
        arrayList.add(title2);
        String[] title3 = {"MAC Address", mFocusedHost.mac};
        arrayList.add(title3);
        if (!mFocusedHost.deviceType.contains("Unknown")) {
            String[] title4 = {"Device Type", mFocusedHost.osType.name().toUpperCase()};
            arrayList.add(title4);
        }
        String[] title5 = {"MAC Vendor", mFocusedHost.vendor};
        arrayList.add(title5);
        String[] title9 = {"First seen", mFocusedHost.getDateString()};
        arrayList.add(title9);
        if (!mFocusedHost.Brand_and_Model.contains("Unknown")) {
            String[] title10 = {"Brand and Model", mFocusedHost.Brand_and_Model};
            arrayList.add(title10);
        }
        try {
            if (mFocusedHost.Ports(null) != null) {
                String[] title11 = {"First seen", mFocusedHost.Ports().portArrayList().size() + " ports scanned"};
                arrayList.add(title11);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void                buildBonjour(ArrayList<String[]> arrayList) {
        if (mFocusedHost.Bonjour_Name.contains("Unknown") && mFocusedHost.Bonjour_Services.contains("Unknown")) {
            String[] title11 = {"Bonjour Service", "No configuration detected"};
            arrayList.add(title11);
        } else {
            String[] title11 = {"Bonjour Name", mFocusedHost.Bonjour_Name};
            String[] title12 = {"Bonjour Services", mFocusedHost.Bonjour_Services};
            arrayList.add(title11);
            arrayList.add(title12);
        }
    }

    private void                buildNetBIOS(ArrayList<String[]> arrayList) {
        if (mFocusedHost.NetBIOS_Domain.contains("Unknown") &&
                mFocusedHost.NetBIOS_Domain.contains("Unknown") &&
                mFocusedHost.NetBIOS_Domain.contains("Unknown")) {
            String[] title6 = {"NetBIOS", "No configuration detected"};
            arrayList.add(title6);
        } else {
            String[] title6 = {"NetBIOS Domain", mFocusedHost.NetBIOS_Domain};
            String[] title7 = {"NetBIOS Name", mFocusedHost.NetBIOS_Name};
            String[] title8 = {"NetBIOS Role", mFocusedHost.NetBIOS_Role};
            arrayList.add(title6);
            arrayList.add(title7);
            arrayList.add(title8);
        }
    }

    private void                buildUpnP(ArrayList<String[]> arrayList) {
        if (mFocusedHost.UPnP_Name.contains("Unknown") &&
                mFocusedHost.UPnP_Device.contains("Unknown") &&
                mFocusedHost.UPnP_Services.contains("Unknown")) {
            String[] title6 = {"UPnP Services", "No configuration detected"};
            arrayList.add(title6);
        } else {
            String[] title13 = {"UPnP Name", mFocusedHost.UPnP_Name};
            String[] title14 = {"UPnP Device", mFocusedHost.UPnP_Device};
            String[] title15 = {"UPnP Services", mFocusedHost.UPnP_Services};
            arrayList.add(title13);
            arrayList.add(title14);
            arrayList.add(title15);
        }

    }

}