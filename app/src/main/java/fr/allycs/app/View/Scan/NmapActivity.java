package fr.allycs.app.View.Scan;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Tools.Nmap.NmapControler;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Misc.Utils;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Adapter.HostSelectionAdapter;
import fr.allycs.app.View.Widget.Dialog.RV_dialog;

public class                    NmapActivity extends MyActivity {
    private String              TAG = "NmapActivity";
    private NmapActivity        mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private MaterialSpinner     mNmapParamMenu;
    private NmapOutputFragment  nmapOutputFragment;
    private TextView            monitorHostTargeted, monitorNmapParam;
    private TextView            Output, MonitorInoptionTheTarget;
    private RelativeLayout      mNmapConfEditorLayout, nmapConfLayout;
    private Toolbar             mToolbar;
    private List<Host>          mListHostSelected = new ArrayList<>();
    private TabLayout           mTabs;
    private ImageView           mSettingsMenu;
    private ImageButton         mSettings;
    private NmapControler       nmapControler;

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        nmapControler = new NmapControler();
        initXml();
        initSpinner();
        initRecyHost();
        if (mSingleton.hostsList == null) {
            MonitorInoptionTheTarget.setText("No target selected");
        } else {
            initTabswithTargets(mSingleton.hostsList);
            initFragment();
            monitorNmapParam.setText(nmapControler.getNmapParamFromMenuItem(nmapControler.getMenuCommmands().get(0)));
        }
    }

    private void                initFragment() {
        //TODO: FAIRE LE LIEN
        // Passez le (RV + TextView Output) dans le fragment
        // Faire le mode sans Target
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        mNmapParamMenu = findViewById(R.id.spinnerTypeScan);
        monitorHostTargeted = (EditText) findViewById(R.id.hostEditext);
        monitorNmapParam = (EditText) findViewById(R.id.nmapMonitorParameter);
        mTabs = findViewById(R.id.tabs);
        mToolbar = findViewById(R.id.toolbar);
        Output = findViewById(R.id.Output);
        Output.setMovementMethod(new ScrollingMovementMethod());
        mSettingsMenu = findViewById(R.id.settingsMenu);
        MonitorInoptionTheTarget = findViewById(R.id.targetMonitor);
        mSettings = findViewById(R.id.settings);
        mNmapConfEditorLayout = findViewById(R.id.nmapConfEditorLayout);
        nmapConfLayout = findViewById(R.id.nmapConfLayout);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrateDevice(mInstance);
                nmapControler.start(Output, mInstance);
            }
        });
        mSettings.setOnClickListener(onSwitchHeader());
        monitorNmapParam.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "onEditorAction : " + actionId + " event:" + event);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    nmapControler.start(Output, mInstance);
                    return true;
                }
                return false;
            }
        });
    }

    private View.OnClickListener onSwitchHeader() {
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

    private void                initSpinner() {
        mNmapParamMenu.setItems(nmapControler.getMenuCommmands());
        mNmapParamMenu.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
                monitorNmapParam.setText(nmapControler.getNmapParamFromMenuItem(typeScan));
            }
        });
    }

    private void                initRecyHost() {
        if (mSingleton.hostsList == null || mSingleton.hostsList.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, "Vous n'avez pas de targets selectionne", Snackbar.LENGTH_SHORT).show();
        } else  {
            MonitorInoptionTheTarget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RV_dialog(mInstance)
                                .setAdapter(new HostSelectionAdapter(mInstance, mSingleton.hostsList, mListHostSelected), false)
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
                    }
                });
            }
        }

    public void                 initTabswithTargets(final List<Host> hosts) {
        mTabs.removeAllTabs();
        for (Host host : hosts) {
            TabLayout.Tab tabItem = mTabs.newTab();
            tabItem.setText(host.ip);
        }
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                String ipHostSelected = tab.getText().toString().replace("\n", " ");
                Log.d(TAG, "onTabSelected:[" + ipHostSelected + "]");
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
        mListHostSelected.add(host);
        mToolbar.setSubtitle(host.getName());
        //TODO un nmapControler par fragment ?
        nmapControler.setHosts(mListHostSelected);
        MonitorInoptionTheTarget.setText(host.ip);
        monitorHostTargeted.setText(host.ip);
    }

    public void                 flushOutput(final String stdout) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Output.setText(Output.getText() + "\nroot$> " + stdout);
            }
        });
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

}
