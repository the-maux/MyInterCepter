package fr.dao.app.View.Activity.HostDiscovery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.io.File;
import java.util.List;

import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Database.DBManager;
import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Core.Database.DBSniffSession;
import fr.dao.app.Core.Network.BonjourService.ServicesController;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Target.SniffSession;
import fr.dao.app.R;
import fr.dao.app.View.Activity.HostDetail.HostDetailActivity;
import fr.dao.app.View.Activity.TargetMenu.TargetMenuActivity;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Behavior.ViewAnimate;
import fr.dao.app.View.Widget.Adapter.HostDiscoveryAdapter;
import fr.dao.app.View.Widget.Adapter.NetworksAdapter;
import fr.dao.app.View.Widget.Adapter.PcapFileAdapter;
import fr.dao.app.View.Widget.Adapter.SniffSessionAdapter;
import fr.dao.app.View.Widget.Dialog.RV_dialog;

public class                        FragmentHistoric extends MyFragment {
    private String                  TAG = "FragmentHistoric";
    private FragmentHistoric        mInstance = this;
    private Host                    mFocusedHost = null;
    private List<Network>           networksScanned;
    private Network                 focusedNetwork = null;
    private MyActivity              mActivity = null;
    private RecyclerView            mRV;
    private TextView                mEmptyList;
    private RecyclerView.Adapter    RV_AdapterAp = null;
    private HostDiscoveryAdapter    RV_AdapterHostSession = null;
    public enum HistoricDetailMode  {NETWORK_LISTING, DEVICE_OF_NETWORK, DETAIL_NETWORK, WIRESHARK_LISTING, NO_RECORDS}
    public static final String      HOST_HISTORIC = "HostDetail", DB_HISTORIC = "HistoricDB";
    public HistoricDetailMode       mActualMode = HistoricDetailMode.NO_RECORDS;

    private ConstraintLayout        mDetailSessionLayout;
    private ImageView               gateway_logo;
    private TextView                date, name, nbrScanned/*, nbrServiceDiscovered, typeScan*/;
    private CardView                cardGateway, cardWireshark, cardDevices, cardService;
    private TextView                titleGateway, titleWireshark, titleDevices, titleService;
    private TextView                subtitleGateway, subtitleWireshark, subtitleDevices, subtitleService;
    private String                  mTitle = "Audit historic", mSubtitle = "", mHistoricMODE;

    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_historic, container, false);
        initXml(rootView);
        init();
        return rootView;
    }

    public void                     init() {
        if (getArguments() != null && getArguments().getString("mode") != null) {
            Log.d(TAG, "init in mode:[" + getArguments().getString("mode") + "]");
            this.mActivity = (MyActivity) getActivity();
            switch (getArguments().getString("mode")) {
                case HOST_HISTORIC:
                    mHistoricMODE = HOST_HISTORIC;
                    Bundle args = getArguments();
                    mFocusedHost = DBHost.getDevicesFromMAC(args.getString("macAddress"));
                    initHistoricFromDB(mFocusedHost);
                    break;
                case DB_HISTORIC:
                    mHistoricMODE = DB_HISTORIC;
                    initHistoricFromDB();
                    break;
            }
            mRV.setAdapter(RV_AdapterAp);
            mRV.setHasFixedSize(true);
            LinearLayoutManager manager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(manager);

        } else {
            Log.d(TAG, "Historic Mode is not set (referer from User or Discovery)");
            onBackPressed();
        }
    }

    public void                     onResume() {
        super.onResume();
        if (mHistoricMODE.contains(DB_HISTORIC))
            pushToolbar();
    }

    private void                    initXml(View rootView) {
        mRV = rootView.findViewById(R.id.recycler_view);
        mEmptyList = rootView.findViewById(R.id.emptyList);
        mDetailSessionLayout = rootView.findViewById(R.id.detailSessionLayout);
        mDetailSessionLayout.setVisibility(View.GONE);

        
        /* Detail Network */
        name = rootView.findViewById(R.id.title);
        date = rootView.findViewById(R.id.dateSession);
        nbrScanned = rootView.findViewById(R.id.nbrScanned);


        cardGateway = rootView.findViewById(R.id.gateway_card);
        cardWireshark = rootView.findViewById(R.id.wireshark_card);
        cardDevices = rootView.findViewById(R.id.devices_card);
        cardService = rootView.findViewById(R.id.services_card);

        gateway_logo = rootView.findViewById(R.id.gateway_logo);

        titleGateway = rootView.findViewById(R.id.titleGateway_menu);
        titleWireshark  = rootView.findViewById(R.id.title_wireshark);
        titleDevices = rootView.findViewById(R.id.SubtitleDevices);
        titleService = rootView.findViewById(R.id.titleServices);

        MyGlideLoader.loadDrawableInCircularImageView(mInstance.getContext(), R.drawable.radar,
                (ImageView) rootView.findViewById(R.id.radar_logo));
        subtitleGateway = rootView.findViewById(R.id.subtitleGateway);
        subtitleWireshark  = rootView.findViewById(R.id.SubtitleWireshark);
        subtitleDevices = rootView.findViewById(R.id.footer_devices);
        subtitleService = rootView.findViewById(R.id.SubtitleServices);
        /*typeScan = rootView.findViewById(R.id.TypeOfScan);
        nbrServiceDiscovered = rootView.findViewById(R.id.nbrServiceDiscovered)*/;
    }

    private void                    initHistoricFromDB() /* omg, plz refacto that*/{
        networksScanned = DBNetwork.getAllAccessPoint();
        mDetailSessionLayout.setVisibility(View.GONE);
        if (networksScanned.isEmpty()) {
            setTitleToolbar("Historic", "No historic");
            mActualMode = HistoricDetailMode.NO_RECORDS;
            ViewAnimate.setVisibilityToVisibleQuick(mEmptyList);
            ViewAnimate.setVisibilityToGoneQuick(mRV);
        } else {
            ViewAnimate.setVisibilityToVisibleQuick(mRV);
            if (RV_AdapterAp == null) {
                RV_AdapterAp = new NetworksAdapter(this, networksScanned);
            }
            setTitleToolbar("Historic", networksScanned.size() + " network scanned");
            mRV.setAdapter(RV_AdapterAp);
            mActualMode = HistoricDetailMode.NETWORK_LISTING;
            mEmptyList.setVisibility(View.GONE);
            ViewAnimate.setVisibilityToVisibleQuick(mRV);
        }
    }
    private void                    initHistoricFromDB(Host mFocusedHost) {/* Search With Device In*/
        Log.d(TAG, "initHistoricFromDB for host :" + mFocusedHost.getName());
        mDetailSessionLayout.setVisibility(View.GONE);
        networksScanned = DBNetwork.getAllAPWithDeviceIn(mFocusedHost);
        if (networksScanned.isEmpty()) {
            mActualMode = HistoricDetailMode.NO_RECORDS;
            mEmptyList.setVisibility(View.VISIBLE);
            mRV.setVisibility(View.GONE);
        } else {

            if (RV_AdapterAp == null) {
                RV_AdapterAp = new NetworksAdapter(this, networksScanned);
            }
            mRV.setAdapter(RV_AdapterAp);
            mActualMode = HistoricDetailMode.NETWORK_LISTING;
            mEmptyList.setVisibility(View.GONE);
            ViewAnimate.setVisibilityToVisibleQuick(mRV);
        }
    }

    public void                     onNetworkFocused(final Network network) {
        mActualMode = HistoricDetailMode.DETAIL_NETWORK;
        ViewAnimate.setVisibilityToGoneQuick(mRV);
        ViewAnimate.setVisibilityToVisibleQuick(mDetailSessionLayout);
        if (network != null) {
            focusedNetwork = network;
        }
        if (focusedNetwork == null) {
            onBackPressed();
        }
        date.setText("Last scan realised the " + focusedNetwork.getDateString());
        name.setText(focusedNetwork.Ssid);
        nbrScanned.setText("Scanned " + focusedNetwork.nbrScanned + " times");
        initViewSessionFocus_Gateway(focusedNetwork);
        initViewSessionFocus_Devices(focusedNetwork);
        initViewSessionFocus_Wireshark(focusedNetwork);
        initViewSessionFocus_Services(focusedNetwork);
        ViewAnimate.setVisibilityToVisibleQuick(cardGateway, 250);
        ViewAnimate.setVisibilityToVisibleQuick(cardWireshark, 300);
        ViewAnimate.setVisibilityToVisibleQuick(cardDevices, 350);
        ViewAnimate.setVisibilityToVisibleQuick(cardService, 400);
        setTitleToolbar(null, focusedNetwork.getDateString());
        String nbrService = ((focusedNetwork.Services() == null) ? "0" :
                focusedNetwork.Services().size()) + " Services discovered on Network";
        titleService.setText(nbrService);
    }

    private void                    initViewSessionFocus_Gateway(final Network session) {
        if (session.Gateway != null) {
            titleGateway.setText(session.Gateway.getName());
            subtitleGateway.setText(session.Gateway.mac);
            cardGateway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityOptionsCompat options;
                    Intent intent = new Intent(mActivity, HostDetailActivity.class);
                    Pair<View, String> p1 = Pair.create((View)gateway_logo, "hostPicture");
                    Pair<View, String> p2 = Pair.create((View)titleGateway, "hostTitle");
                    options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(mActivity, p1, p2);
                    intent.putExtra("mode", "Recorded");
                    intent.putExtra("macAddress", session.Gateway.mac);
                    mActivity.startActivity(intent, options.toBundle());
                }
            });
        } else {
            titleGateway.setText("Gateway not in database");
            subtitleGateway.setText("Did you deleted it ?");
        }
    }
    private void                    initViewSessionFocus_Devices(final Network session) {
        if (session.listDevices() != null) {
            titleDevices.setText(session.listDevices().size() + " devices decouvert");
            subtitleDevices.setText(session.nbrOs + " os découvert");
            cardDevices.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mActualMode = HistoricDetailMode.DEVICE_OF_NETWORK;
                    if (RV_AdapterHostSession == null)
                        RV_AdapterHostSession = new HostDiscoveryAdapter(getActivity(), mRV, true, null);
                    RV_AdapterHostSession.updateHostList(session.listDevices());
                    ViewAnimate.setVisibilityToGoneQuick(mDetailSessionLayout);
                    ViewAnimate.setVisibilityToVisibleQuick(mRV);
                    setTitleToolbar(session.Ssid,  session.listDevices().size() + " devices discovered");
                    mRV.setAdapter(RV_AdapterHostSession);
                }
            });
        } else {
            titleDevices.setText("Aucun device découvert sur ce reseau");
            subtitleDevices.setText("No scan performed correctly");
        }
    }
    private void                    initViewSessionFocus_Wireshark(final Network session) {
        if (session.SniffSessions() != null && !session.SniffSessions().isEmpty()) {
            titleWireshark.setText(session.SniffSessions().size() + " sniffing recorded");
            int nbrSession = 0;
            for (SniffSession sniffSession : session.SniffSessions()) {
                if (mFocusedHost == null)
                    nbrSession++;
                else
                    for (Host device : sniffSession.listDevices()) {
                        if (mFocusedHost.equals(device)) {
                            nbrSession++;
                            break;
                        }
                    }
            }
            subtitleWireshark.setText(nbrSession + " sniffing recorded");
            cardWireshark.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mActualMode = HistoricDetailMode.WIRESHARK_LISTING;
                    ViewAnimate.setVisibilityToGoneQuick(mDetailSessionLayout);
                    ViewAnimate.setVisibilityToVisibleQuick(mRV);
                    List<File> pcaps = DBManager.getListPcapFormSSIDFile(session.Ssid);
                    mRV.setAdapter(new PcapFileAdapter(mActivity, pcaps, null));
                    mRV.setHasFixedSize(true);
                    mRV.setLayoutManager(new LinearLayoutManager(getActivity()));
                    setTitleToolbar(session.Ssid,  pcaps.size() + "records");
                }
            });
        } else {
            titleWireshark.setText("Aucune sessions recorded");
            subtitleWireshark.setText("0 pcap recorded");
            cardWireshark.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                }
            });
        }
    }
    private void                    initViewSessionFocus_Services(final Network session) {
        if (session.Services() != null && !session.Services().isEmpty()) {
            titleService.setText("No stats recorded");
            String subtitle = ServicesController.howManyHostTheServices(session.Services()) + " devices différents";
            subtitleService.setText(subtitle);
            /*forwardGateway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
        } else {
           // ServicesLine.setVisibility(View.GONE);
        }
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
        mActivity.initSettingsButton();
    }

    private void                    showSniffSessionList() {
        SniffSessionAdapter adapter = new SniffSessionAdapter(mActivity, DBSniffSession.getAllSniffSession());
        new RV_dialog(mActivity)
                .setAdapter(adapter, true)
                .setTitle("Sniffing sessions recorded")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public BottomSheetMenuDialog    onSettingsClick(final AppBarLayout mAppbar, Activity activity) {
        return new BottomSheetBuilder(activity)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(ContextCompat.getColor(activity, R.color.material_light_white))
                .setAppBarLayout(mAppbar)
                .addTitleItem("Historic settings")
                .addItem(0, "Purge all history", R.mipmap.ic_os_filter)
                .addItem(1, "MITM Network", R.mipmap.ic_os_filter)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem menuItem) {
                        Log.d(TAG, "STRING:"+menuItem.getTitle().toString());
                        switch (menuItem.getTitle().toString()) {
                            case "Purge all history":
                               //osFilterDialog();
                                break;
                            case "MITM Network":
                                showSniffSessionList();
                                break;
                            case "Mode offline":
                                startActivity(new Intent(mActivity, TargetMenuActivity.class));
                                break;
                        }
                    }
                })
                .expandOnStart(true)
                .createDialog();
    }

    private void                    initFullWiresharkFocus() {
        List<SniffSession> allsniffedSessions = DBSniffSession.getAllSniffSession();
        for (SniffSession sniffSession : allsniffedSessions) {
            Log.d(TAG, "SNIFFSESSION:\t:" + sniffSession);
            Log.d(TAG, "SNIFFSESSION:\t:SESSION: " + sniffSession.session);
            Log.d(TAG, "SNIFFSESSION:\t:SERIAL: " + sniffSession.listDevicesSerialized);
            for (Host host : sniffSession.listDevices()) {
                Log.d(TAG, "SNIFFSESSION:\t:CLIENT: " + host);
            }
            for (Pcap pcap : sniffSession.listPcapRecorded()) {
                Log.d(TAG, "SNIFFSESSION:\t:PCAP: " + pcap);
            }
            showSniffSessionList();
        }
    }

    public void                     onAddButtonClick(ImageButton addHostBtn) {
        MyGlideLoader.loadDrawableInImageView(mActivity, R.mipmap.ic_history, addHostBtn, true);
        addHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFullWiresharkFocus();
            }
        });
    }

    public boolean                  onBackPressed() {
        Log.d(TAG, "onBackPressed:" + mActualMode);
        switch (mActualMode) {
            case NETWORK_LISTING:
                Log.i(TAG, "listing SSID so returning to scanning fragment");
                return true;
            case NO_RECORDS:
                Log.i(TAG, "listing SSID but not wifi so returning to scanning fragment");
                return true;
            case DETAIL_NETWORK:
                Log.i(TAG, "DETAIL_NETWORK so reinit fragment with listing ssid");
                if (mFocusedHost == null)
                    initHistoricFromDB();
                else
                    initHistoricFromDB(mFocusedHost);
                return false;
            case DEVICE_OF_NETWORK:
                Log.i(TAG, "returning to network detail");
                onNetworkFocused(null);
                return false;
            case WIRESHARK_LISTING:
                Log.i(TAG, "listing wireshark so, returning to network detail");
                onNetworkFocused(null);
                return false;
            default:
                return true;
        }

    }

}
