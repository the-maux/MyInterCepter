package fr.allycs.app.View.Scan;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.AndroidUtils.Utils;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Nmap.NmapControler;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Dialog.DialogQuestionWithInput;

public class                    NmapActivity extends MyActivity {
    private String              TAG = "NmapActivity";
    private NmapActivity        mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private MaterialSpinner     mNmapParamMenu;
    private NmapOutputFragment  nmapOutputFragment;
    private TextView            monitorHostTargeted, monitorNmapParam;
    private TextView            MonitorInoptionTheTarget;
    private RelativeLayout      mNmapConfEditorLayout, nmapConfLayout;
    private Toolbar             mToolbar;
    private List<Host>          mListHostSelected = new ArrayList<>();
    private TabLayout           mTabs;
    private ImageView           mSettingsMenu;
    private ImageButton         mSettings;
    private NmapControler       nmapControler;
    private ProgressBar         mProgressBar;
    private boolean             isExternalTarget;

    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        initXml();
        nmapControler = new NmapControler(false);
        initFragment();
        initSpinner();
        initRecyHost();
        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        mNmapParamMenu = findViewById(R.id.spinnerTypeScan);
        monitorHostTargeted = (EditText) findViewById(R.id.hostEditext);
        monitorNmapParam = (EditText) findViewById(R.id.nmapMonitorParameter);
        mTabs = findViewById(R.id.tabs);
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mSettingsMenu = findViewById(R.id.settingsMenu);
        MonitorInoptionTheTarget = findViewById(R.id.targetMonitor);
        mSettings = findViewById(R.id.settings);
        mNmapConfEditorLayout = findViewById(R.id.nmapConfEditorLayout);
        nmapConfLayout = findViewById(R.id.nmapConfLayout);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNmap();
            }
        });
        mSettings.setOnClickListener(onClickSettingsBtn());
        monitorNmapParam.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startNmap();
                    return true;
                }
                return false;
            }
        });
    }

    private void                init() {
        if (mSingleton.selectedHostsList == null || mSingleton.selectedHostsList.isEmpty()) {
            MonitorInoptionTheTarget.setText("No target selected");
            isExternalTarget = true;
            askForExternalTarget();
        } else {
            isExternalTarget = false;
            mListHostSelected = mSingleton.selectedHostsList;
            initTabswithTargets(mListHostSelected);
            monitorNmapParam.setText(nmapControler.getNmapParamFromMenuItem(nmapControler.getMenuCommmands().get(0)));
            initUIWithTarget(mListHostSelected.get(0));
        }
    }


    private void                initFragment() {
        try {
            nmapOutputFragment = new NmapOutputFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, nmapOutputFragment)
                    .commit();
        } catch (IllegalStateException e) {
            showSnackbar("Error in fragment: " + e.getCause().getMessage());
            e.getStackTrace();
            super.onBackPressed();
        }
    }

    private void                initSpinner() {
        mNmapParamMenu.setItems(nmapControler.getMenuCommmands());
        mNmapParamMenu.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
                String param = nmapControler.getNmapParamFromMenuItem(typeScan);
                monitorNmapParam.setText(param);
                nmapControler.setmActualItemMenu(typeScan);
                if (param != null)
                    setToolbarTitle(null, typeScan);
            }
        });
    }
/*
            MonitorInoptionTheTarget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RV_dialog(mInstance)
                                .setAdapter(new HostSelectionAdapter(mInstance, mSingleton.selectedHostsList, mListHostSelected), false)
                                .setTitle("Choix des cibles")
                                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (mListHostSelected.isEmpty())
                                            Snackbar.make(mCoordinatorLayout, "No target selected", Snackbar.LENGTH_LONG).show();
                                        else {
                                            initTabswithTargets(mListHostSelected);
                                            Snackbar.make(mCoordinatorLayout, mListHostSelected.size() + " target", Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                }).show();
 */
    private void                initRecyHost() {
        if (mSingleton.selectedHostsList == null || mSingleton.selectedHostsList.isEmpty()) {
            if (isExternalTarget) {

            } else {
                Snackbar.make(mCoordinatorLayout, "Vous n'avez aucun device selectionne", Snackbar.LENGTH_SHORT).show();
            }
        } else {

        }
    }

    public void                 initTabswithTargets(final List<Host> hosts) {
        mTabs.removeAllTabs();
        for (Host host : hosts) {
            TabLayout.Tab tabItem = mTabs.newTab();
            tabItem.setText(host.ip);
            mTabs.addTab(tabItem);
        }
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                String ipHostSelected = tab.getText().toString().replace("\n", " ");
                Log.d(TAG, "onSelectedTab host :[" + ipHostSelected + "]");
                for (Host host : hosts) {
                    if (host.ip.contentEquals(ipHostSelected)) {
                        initUIWithTarget(host);
                        nmapOutputFragment.refresh(host);
                        break;
                    }
                }
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void                 initUIWithTarget(Host host) {
        String subtitle = ((nmapControler.ismOneByOnExecuted()) ?
                host.getName() :
                mListHostSelected.size() + " device"  + ((mListHostSelected.size() >= 2) ? "s" : ""));
        setToolbarTitle("Nmap v6.40", subtitle);
        List<Host> TmpHost = new ArrayList<>();
        TmpHost.add(host);
        nmapControler.setHosts(TmpHost);
        MonitorInoptionTheTarget.setText(host.ip);
        monitorHostTargeted.setText(host.ip);
        nmapOutputFragment.refresh(host);
    }

    private View.OnClickListener onClickSettingsBtn() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNmapConfEditorLayout.getVisibility() == View.GONE &&
                        nmapConfLayout.getVisibility() == View.GONE) {
                    nmapConfLayout.setVisibility(View.VISIBLE);
                } else if (mNmapConfEditorLayout.getVisibility() == View.GONE) {
                    nmapConfLayout.setVisibility(View.GONE);
                    mNmapConfEditorLayout.setVisibility(View.VISIBLE);
                } else {
                    nmapConfLayout.setVisibility(View.GONE);
                    mNmapConfEditorLayout.setVisibility(View.GONE);
                }
            }
        };
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    public void                 startNmap() {
        Utils.vibrateDevice(mInstance);
        nmapControler.startAsLive(nmapOutputFragment, mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void                askForExternalTarget() {
        final DialogQuestionWithInput dialog = new DialogQuestionWithInput(this)
                .hideSecondInput()
                .setIcon(R.drawable.dns) //IMAGE HOST
                .setTitle("Choose your target")
                .setHintToEDFirstQuestion("")
                .setHintToTILFirstQuestion("");
        dialog.onPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                onAddExternal(dialog.getFirstInputQuestion());
            }
        }).show();
    }

    private void                onAddExternal(String externalHost) {
        int nbrPoint = StringUtils.countMatches(externalHost, ".");
        InetAddress address = null;
        if (nbrPoint == 3) {
            try {
                address = InetAddress.getByName(externalHost);
                String ip = address.getHostAddress();
                List<String> listExternalIp = new ArrayList<>();
                //new NmapControler(listExternalIp);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                addExternalHostFailed(externalHost + ": Name or service unknow");
                return ;
            }
            addExternalHostFailed(externalHost + ": Name or service unknow");
        }
    }
    private void                addExternalHostFailed(String msg) {
        showSnackbar(msg);
        askForExternalTarget();
    }
}
