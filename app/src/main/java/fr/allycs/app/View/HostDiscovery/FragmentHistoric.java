package fr.allycs.app.View.HostDiscovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Database.DBAccessPoint;
import fr.allycs.app.Controller.Core.Database.DBSession;
import fr.allycs.app.Controller.Misc.MyFragment;
import fr.allycs.app.Controller.Network.BonjourService.ServicesController;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.Model.Target.SniffSession;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Adapter.AccessPointAdapter;
import fr.allycs.app.View.Widget.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Widget.Adapter.SessionAdapter;
import fr.allycs.app.View.Widget.Dialog.HostDialogDetail;

public class                        FragmentHistoric extends MyFragment {
    private String                  TAG = "FragmentHistoric";
    private Singleton               mSingleton = Singleton.getInstance();
    private Host                    mFocusedHost = null;
    private List<AccessPoint>       HistoricAps;
    private Session                 focusedSession = null;
    private HostDiscoveryActivity   mActivity = null;
    private RecyclerView            mRV;
    private TextView                mEmptyList;
    private RecyclerView.Adapter    RV_AdapterAp = null, RV_AdapterSessions = null, RV_AdapterHostSession = null;

    private AccessPointAdapter.typeFragment actualMode = null;
    public enum HistoricDetailMode  { ApHistoric, SessionsOfAp, devicesOfSession, detailSession, noHistoric}
    public static final String      HOST_HISTORIC = "HostDetail", DB_HISTORIC = "HistoricDB";
    public HistoricDetailMode       mActualMode = HistoricDetailMode.noHistoric;

    private RelativeLayout          mDetailSessionLayout;
    private RelativeLayout          gatewayLine, DevicesLine, WiresharkLine, ServicesLine;

    private TextView                date, name, nbrServiceDiscovered, typeScan;
    private TextView                titleGateway, titleWireshark, titleDevices, titleService;
    private TextView                subtitleGateway, subtitleWireshark, subtitleDevices, subtitleService;
    private ImageView               forwardGateway, forwardWireshark, forwardListDevices, forwardServices;
    private String                  mTitle = "Audit historic", mSubtitle = "";

    @Override public View           onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_historic, container, false);

        initXml(rootView);
        init();
        pushToolbar();
        return rootView;
    }

    @Override public void           init() {
        if (getArguments() != null && getArguments().getString("mode") != null) {
            Log.d(TAG, "init in mode:[" + getArguments().getString("mode") + "]");
            switch (getArguments().getString("mode")) {
                case HOST_HISTORIC:
                    mFocusedHost = mSingleton.hostsList.get(0);
                    initHistoricFromDB(mFocusedHost);
                    break;
                case DB_HISTORIC:
                    this.mActivity = (HostDiscoveryActivity) getActivity();
                    initHistoricFromDB();
                    break;
            }
            mRV.setAdapter(RV_AdapterAp);
            mRV.setHasFixedSize(true);
            mRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            Log.d(TAG, "Historic Mode is not set (referer from User or Discovery)");
        }
    }

    private void                    initXml(View rootView) {
        mRV = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mEmptyList = (TextView) rootView.findViewById(R.id.emptyList);
        mDetailSessionLayout = rootView.findViewById(R.id.detailSessionLayout);
        mDetailSessionLayout.setVisibility(View.GONE);

        /* Detail Session */
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

    }

    private void                    initHistoricFromDB() /* All Session, no filter*/{
        HistoricAps = DBAccessPoint.getAllSessionsRecorded();
        if (HistoricAps.isEmpty()) {
            setTitleToolbar("Historic", "No historic");
            mActualMode = HistoricDetailMode.noHistoric;
            mEmptyList.setVisibility(View.VISIBLE);
            mRV.setVisibility(View.GONE);
        } else {
            if (RV_AdapterAp == null) {
                RV_AdapterAp = new AccessPointAdapter(this, HistoricAps);
            }
            setTitleToolbar("Historic", HistoricAps.size() + " wifi scanned");
            mRV.setAdapter(RV_AdapterAp);
            mActualMode = HistoricDetailMode.ApHistoric;
            mEmptyList.setVisibility(View.GONE);
        }
    }
    private void                    initHistoricFromDB(Host mFocusedHost) {/* Search With Device In*/
        Log.d(TAG, "initHistoricFromDB for host :" + mFocusedHost.getName());
        HistoricAps = DBSession.getAllAPWithDeviceIn(mFocusedHost);
        if (HistoricAps.isEmpty()) {
            mActualMode = HistoricDetailMode.noHistoric;
            mEmptyList.setVisibility(View.VISIBLE);
            mRV.setVisibility(View.GONE);
        } else {
            if (RV_AdapterAp == null) {
                RV_AdapterAp = new AccessPointAdapter(this, HistoricAps);
            }
            mRV.setAdapter(RV_AdapterAp);
            mActualMode = HistoricDetailMode.ApHistoric;
            mEmptyList.setVisibility(View.GONE);
        }
    }

    public void                     onAccessPointFocus(AccessPoint ap) {
        mActualMode = HistoricDetailMode.SessionsOfAp;
        if (ap != null) {
            List<Session> allSessionWithDeviceIn  = DBSession.getAllSessionFromApWithDeviceIn(ap.sessions(), mFocusedHost);
            RV_AdapterSessions = new SessionAdapter(this, allSessionWithDeviceIn, ap);
        } else {
            ap = ((SessionAdapter)RV_AdapterSessions).getAccessPoint();
        }
        setTitleToolbar(ap.Ssid, ap.nbrSession + " sessions found");
        mDetailSessionLayout.setVisibility(View.GONE);
        mRV.setVisibility(View.VISIBLE);
        mRV.setAdapter(RV_AdapterSessions);
    }

    public void                     onSessionFocused(final Session session) {
        Log.d(TAG, "onSessionFocused");
        mActualMode = HistoricDetailMode.detailSession;
        mRV.setVisibility(View.GONE);
        mDetailSessionLayout.setVisibility(View.VISIBLE);
        if (session != null) {
            focusedSession = session;
        }
        if (focusedSession == null) {
            onBackPressed();
        }
        date.setText(focusedSession.getDateString());
        if (focusedSession.name == null || focusedSession.name.isEmpty())
            name.setVisibility(View.GONE);
        initViewSessionFocus_Gateway(focusedSession);
        initViewSessionFocus_Devices(focusedSession);
        initViewSessionFocus_Wireshark(focusedSession);
        initViewSessionFocus_Services(focusedSession);
        setTitleToolbar(null, focusedSession.getDateString());
        typeScan.setText("Realised with an " + focusedSession.typeScan + "scan");
        nbrServiceDiscovered.setText(((focusedSession.services == null) ? "0" : focusedSession.services.size()) + " services discovered on network");
    }

    private void                    initViewSessionFocus_Gateway(final Session session) {
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
    private void                    initViewSessionFocus_Devices(final Session session) {
        if (session.listDevices() != null) {
            titleDevices.setText(session.listDevices().size() + " devices decouvert");
            subtitleDevices.setText(session.nbrOs + " Os découvert");
            forwardListDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hostOfSessionsFocused(session);
                }
            });
        } else {
            titleDevices.setText("Aucun device découvert sur ce reseau");
            subtitleDevices.setText("No scan performed correctly");
        }
    }
    private void                    initViewSessionFocus_Wireshark(final Session session) {
        if (session.SniffSessions() != null && !session.SniffSessions().isEmpty()) {
            titleWireshark.setText(session.SniffSessions().size() + " sessions sniff realise");
            int nbrSession = 0;
            for (SniffSession sniffSession : session.SniffSessions()) {
                for (Host device : sniffSession.listDevices()) {
                    if (mFocusedHost.equals(device)) {
                        nbrSession++;
                        break;
                    }
                }
            }
            subtitleWireshark.setText(nbrSession + " sniff avec ce device in");
            forwardWireshark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Faire un listing des Pcap
                }
            });
        } else {
            titleWireshark.setText("Aucune sessions realised");
            subtitleWireshark.setText("0 pcap recorded");
        }
    }
    private void                    initViewSessionFocus_Services(final Session session) {
        if (session.services != null && !session.services.isEmpty()) {
            titleService.setText(session.services.size() + " découvert sur ce réseau");
            subtitleService.setText("Sur " + ServicesController.howManyHostTheServices(session.services) + " devices différents");
            forwardGateway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            ServicesLine.setVisibility(View.GONE);
        }
    }

    public void                     hostOfSessionsFocused(Session session) {
        mDetailSessionLayout.setVisibility(View.GONE);
        mRV.setVisibility(View.VISIBLE);
        mActualMode = HistoricDetailMode.devicesOfSession;
        if (RV_AdapterHostSession == null) {
            HostDiscoveryAdapter hostAdapter = new HostDiscoveryAdapter(getActivity(), mRV, true);
            hostAdapter.updateHostList(session.listDevices());
            RV_AdapterHostSession = hostAdapter;
        }
        setTitleToolbar(focusedSession.getDateString(),  session.listDevices().size() + " devices");
        mRV.setAdapter(RV_AdapterHostSession);
    }

    private void                    setTitleToolbar(String title, String subtitle) {
        if (title != null)
            mTitle = title;
        if (subtitle != null)
            mSubtitle = subtitle;
        if (isVisible()) {
            mActivity.setToolbarTitle(title, subtitle);
        }
    }

    private void                    pushToolbar() {
        mActivity.setToolbarTitle(mTitle, mSubtitle);
    }

    @Override
    public BottomSheetMenuDialog    onSettingsClick(final AppBarLayout mAppbar, Activity activity) {
        return new BottomSheetBuilder(activity)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(ContextCompat.getColor(activity, R.color.material_light_white))
                .setAppBarLayout(mAppbar)
                .addTitleItem("Settings")
                .addItem(0, "Purge BDD", R.mipmap.ic_os_filter)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem menuItem) {
                        Log.d(TAG, "STRING:"+menuItem.getTitle().toString());
                    }
                })
                .expandOnStart(true)
                .createDialog();
    }

    public boolean                  onBackPressed() {
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
