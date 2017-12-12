package fr.allycs.app.View.HostDetail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Database.DBSession;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.AccessPointAdapter;
import fr.allycs.app.View.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Adapter.SessionAdapter;
import fr.allycs.app.View.Dialog.HostDialogDetail;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;


public class                    HostDetailFragment extends android.app.Fragment{
    private String              TAG = "HostDetailFragment";
    private TabLayout           mTabs;
    private CoordinatorLayout   mCoordinatorLayout;
    private List<AccessPoint>   HistoricAps;
    private Singleton           mSingleton = Singleton.getInstance();
    private RecyclerView        RV_Historic;
    private RelativeLayout      mDetailSessionLayout;
    public enum HistoricMode { ApHistoric, SessionsOfAp, devicesOfSession, detailSession, noHistoric}
    private Host                mFocusedHost;//TODO need to be init
    public  HistoricMode        mActualMode = HostDetailFragment.HistoricMode.noHistoric;
    private RecyclerView.Adapter RV_AdapterAp = null, RV_AdapterSessions = null, RV_AdapterHostSession = null;
    private TextView            mNoHistoric;
    private Context             mCtx = getActivity();
    private HostDiscoveryActivity mActivity;

    private TextView            date, name, nbrServiceDiscovered, typeScan;
    private RelativeLayout      gatewayLine, DevicesLine, WiresharkLine, ServicesLine;
    private TextView            titleGateway, titleWireshark, titleDevices, titleService;
    private TextView            subtitleGateway, subtitleWireshark, subtitleDevices, subtitleService;
    private ImageView           forwardGateway, forwardWireshark, forwardListDevices, forwardServices;


    @Override public View       onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hostdetail, container, false);
        initXml(rootView);
        mFocusedHost = mSingleton.hostsList.get(0);
        initHistoricFromDB();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mDetailSessionLayout = rootView.findViewById(R.id.detailSessionLayout);
        mDetailSessionLayout.setVisibility(View.GONE);
        mTabs  = rootView.findViewById(R.id.tabs);
        mNoHistoric = rootView.findViewById(R.id.noHistoric);


        name = rootView.findViewById(R.id.title);
        date = rootView.findViewById(R.id.dateSession);
        gatewayLine = rootView.findViewById(R.id.gatewayLine);
        DevicesLine = rootView.findViewById(R.id.DevicesLine);
        WiresharkLine = rootView.findViewById(R.id.WiresharkLine);
        ServicesLine = rootView.findViewById(R.id.ServicesLine);

        forwardGateway = rootView.findViewById(R.id.forwardGateway);
        forwardWireshark = rootView.findViewById(R.id.forwardWireshark);
        forwardListDevices = rootView.findViewById(R.id.forwardDevice);
        forwardServices = rootView.findViewById(R.id.forwardServices);

        titleGateway = rootView.findViewById(R.id.titleGateway);
        titleWireshark  = rootView.findViewById(R.id.titleWireshark);
        titleDevices = rootView.findViewById(R.id.titleDevices);
        titleService = rootView.findViewById(R.id.titleServices);

        subtitleGateway = rootView.findViewById(R.id.SubtitleGateway);
        subtitleWireshark  = rootView.findViewById(R.id.SubtitleWireshark);
        subtitleDevices = rootView.findViewById(R.id.SubtitleDevices);
        subtitleService = rootView.findViewById(R.id.SubtitleServices);

        typeScan = rootView.findViewById(R.id.TypeOfScan);
        nbrServiceDiscovered = rootView.findViewById(R.id.nbrServiceDiscovered);

        RV_Historic = rootView.findViewById(R.id.RV_Historic);
        RV_Historic.setHasFixedSize(true);
        RV_Historic.setLayoutManager(new LinearLayoutManager(mCtx));
    }

    private void                initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab.getHost().toString():" + tab.getText().toString());
                switch (tab.getText().toString()) {
                    case "historic":
                        displayHistoric();
                        break;
                    case "notes":
                        displayNotes();
                        break;
                    case "services":
                        displayServices();
                        break;

                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                displayNotes() {
        Log.d(TAG, "LOAD FROM BDD THE NOTES OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinatorLayout, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                displayHistoric() {
        Log.d(TAG, "LOAD FROM BDD THE HISTORIC OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinatorLayout, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                displayServices() {
        Log.d(TAG, "SHOW SERVICES OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinatorLayout, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    public void                 initHistoricFromDB() {
        HistoricAps = DBSession.getAllAPWithDeviceIn(mFocusedHost);
        if (HistoricAps.isEmpty()) {
            mActualMode = HostDetailFragment.HistoricMode.noHistoric;
            mNoHistoric.setVisibility(View.VISIBLE);
            RV_Historic.setVisibility(View.GONE);
        } else {
            if (RV_AdapterAp == null) {
                RV_AdapterAp = new AccessPointAdapter(this, HistoricAps);
            }
            RV_Historic.setAdapter(RV_AdapterAp);
            mActualMode = HostDetailFragment.HistoricMode.ApHistoric;
            mNoHistoric.setVisibility(View.GONE);
        }
    }

    public void                 onAccessPointFocus(AccessPoint ap) {
        mActualMode = HostDetailFragment.HistoricMode.SessionsOfAp;
        if (RV_AdapterSessions == null) {
            List<Session> allSessionWithDeviceIn = new ArrayList<>();
            for (Session session : ap.Sessions) {
                for (Host device : session.listDevices) {
                    if (device.mac.equals(mFocusedHost.mac)) {
                        allSessionWithDeviceIn.add(session);
                        break;
                    }
                }
            }
            Log.d(TAG, "onAccessPointFocus:: returning " + allSessionWithDeviceIn.size() + " sessions");
            RV_AdapterSessions = new SessionAdapter(this, allSessionWithDeviceIn);
        }
        RV_Historic.setAdapter(RV_AdapterSessions);
    }

    public void                 onSessionFocused(final Session session) {
        mActualMode = HostDetailFragment.HistoricMode.detailSession;
        RV_Historic.setVisibility(View.GONE);
        mDetailSessionLayout.setVisibility(View.VISIBLE);
        date.setText(session.getDateString());
        if (session.name == null || session.name.isEmpty())
            name.setVisibility(View.GONE);
        initViewSessionFocus_Gateway(session);
        initViewSessionFocus_Devices(session);
        initViewSessionFocus_Wireshark(session);
        initViewSessionFocus_Services(session);
        typeScan.setText("Realised with an " + session.typeScan + "scan");
        nbrServiceDiscovered.setText(session.services.size() + " services discovered on network");
    }

    private void                initViewSessionFocus_Gateway(final Session session) {
        if (session.Gateway != null) {
            titleGateway.setText("Gateway: " + session.Gateway.ip);
            subtitleGateway.setText(session.Gateway.name + " - " + session.Gateway.mac);
            forwardGateway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new HostDialogDetail(session.Gateway).show();
                }
            });
        } else {
            gatewayLine.setVisibility(View.GONE);
        }
    }
    private void                initViewSessionFocus_Devices(final Session session) {
        if (session.listDevices != null) {
            titleDevices.setText(session.listDevices.size() + " devices decouvert");
            subtitleDevices.setText(session.nbrOs + " Os découvert");
            forwardListDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hostOfSessionsFocused(session);
                }
            });
        } else {
            titleGateway.setText("Aucun device découvert sur ce reseau");
            subtitleGateway.setText("No scan performed correctly");
        }
    }
    private void                initViewSessionFocus_Wireshark(final Session session) {
        if (session.sniffedSession != null) {
            titleWireshark.setText(session.sniffedSession.size() + " sessions sniff realise");
            subtitleWireshark.setText(session.sniffedSession.size() + " sniff avec ce device in");
            forwardWireshark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Faire un listing des wireshark
                }
            });
        } else {
            titleWireshark.setText("Acune sessions sniff realise");
            subtitleWireshark.setText("");
        }
    }
    private void                initViewSessionFocus_Services(final Session session) {
        if (session.services != null && session.services.isEmpty()) {
            titleService.setText(session.services + " découvert sur ce réseau");
            subtitleService.setText("Sur 3 devices différents");
            forwardGateway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            titleService.setText("Aucun service découvert");
            subtitleService.setText("Scanned 1 time");
        }
    }

    public void                 hostOfSessionsFocused(Session session) {
        mDetailSessionLayout.setVisibility(View.GONE);
        RV_Historic.setVisibility(View.VISIBLE);
        if (RV_AdapterHostSession == null) {
            mActualMode = HostDetailFragment.HistoricMode.devicesOfSession;
            HostDiscoveryAdapter hostAdapter = new HostDiscoveryAdapter(getActivity(), RV_Historic, true);
            hostAdapter.updateHostList(session.listDevices);
            RV_AdapterHostSession = hostAdapter;
        }
        RV_Historic.setAdapter(RV_AdapterHostSession);
    }

    public boolean              onBackPressed() {
        switch (mActualMode) {
            case ApHistoric:
                return true;
            case noHistoric:
                return true;
            case SessionsOfAp:
                initHistoricFromDB();
                return false;
            case detailSession:
                onAccessPointFocus(null);
                return false;
            case devicesOfSession:
                onSessionFocused(null);
                return false;
            default:
                return true;
        }

    }

}


