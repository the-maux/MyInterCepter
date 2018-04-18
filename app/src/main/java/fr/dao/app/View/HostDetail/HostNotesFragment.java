package fr.dao.app.View.HostDetail;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.HostDiscovery.HostDiscoveryActivity;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    HostNotesFragment extends MyFragment {
    private String              TAG = "HostNotesFragment";
    private CoordinatorLayout   mCoordinatorLayout;
    private Host                mFocusedHost;//TODO need to be init
    private Context             mCtx;
    private HostDiscoveryActivity mActivity;
    private ScrollView          mCentral_layout;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        initXml(rootView);
        init();
        mCtx = getActivity();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mCentral_layout = rootView.findViewById(R.id.central_layout);
    }

    public void                 init() {
        Bundle args = getArguments();
        mFocusedHost = DBHost.getDevicesFromMAC(args.getString("macAddress"));
        TextView valueTV = new TextView(getContext());
        StringBuilder buuilder = new StringBuilder("");
        int rax = 0;
        for (String note : mFocusedHost.Notes.split("OxBABOBAB")) {
            buuilder.append(note).append("\n");
            if (rax++ > 0)
                buuilder.append("-------------------------------------").append("\n");
        }
        valueTV.setText(buuilder.toString());
        mCentral_layout.removeAllViews();
        mCentral_layout.addView(valueTV);
    }

    public void                 onResume() {
        super.onResume();
        init();
    }
}