package fr.allycs.app.View.DnsSpoofing;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Controller.AndroidUtils.Utils;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Dnsmasq.DnsmasqControl;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Adapter.DnsLogsAdapter;
import fr.allycs.app.View.Widget.Adapter.DnsSpoofConfAdapter;
import fr.allycs.app.View.Widget.Dialog.DialogQuestionWithInput;


public class                            DnsActivity extends MyActivity {
    private String                      TAG = "DnsActivity";
    private DnsActivity                 mInstance = this;
    private CoordinatorLayout           mCoordinatorLayout;
    private Toolbar                     mToolbar;
    private SearchView                  mSearchView;
    private ImageButton                 mAction_add_host, mSettingsBtn;
    private TabLayout                   mTabs;
    private FloatingActionButton        mFab;
    private RecyclerView                mDnsSpoof_RV;
    private RelativeLayout              mClipper;
    private TextView                    mAction_deleteall, mAction_import, mAction_export, textEmpty, title;
    private Singleton                   mSingleton = Singleton.getInstance();
    private DnsmasqControl              mDnsSpoof = mSingleton.getDnsControler();
    private DnsSpoofConfAdapter         mDnsSpoofAdapter;
    private DnsLogsAdapter              mDnsConsoleAdapter;
    private String                      NAME_CONF_MENU = "Domains intercepted:", NAME_LOGS_MENU = "Dnsmasq logs:";

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnsspoofing);
        initXml();
        initFab();
        initMenu();
        initTabs();
        initRVConfiguration();
        initSearchView();
        mDnsSpoof.setToolbar(this);
    }

    private void                        initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mToolbar = findViewById(R.id.toolbar2);
        mSearchView = findViewById(R.id.searchView);
        mAction_add_host = findViewById(R.id.action_add_host);
        mSettingsBtn = findViewById(R.id.settings);
        mFab = findViewById(R.id.fab);
        mDnsSpoof_RV = findViewById(R.id.dnsSpoof_RV);
        mClipper = findViewById(R.id.clipper);
        title = findViewById(R.id.host);
        mAction_deleteall = findViewById(R.id.action_deleteall);
        mAction_import =findViewById(R.id.action_import);
        mAction_export = findViewById(R.id.action_export);
        textEmpty = findViewById(R.id.textEmpty);
        mTabs = findViewById(R.id.tabs);
        MyGlideLoader.coordoBackground(this, mDnsSpoof_RV);
    }

    private void                        initFab() {
        if (mSingleton.isDnsControlstarted()) {
            mFab.setImageResource(R.mipmap.ic_stop);
        } else {
            mFab.setImageResource(android.R.drawable.ic_media_play);
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance);
                mSingleton.setDnsControlstarted(!mSingleton.isDnsControlstarted());
                if (mSingleton.isDnsControlstarted()) {
                    mDnsSpoof.start();
                    mFab.setImageResource(R.drawable.ic_stop);
                } else {
                    mDnsSpoof.stop();
                    mFab.setImageResource(R.mipmap.ic_play);
                }
            }
        });
    }

    private void                        initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "Domain":
                        title.setText(NAME_CONF_MENU);
                        initRVConfiguration();
                        break;
                    case "Console":
                        title.setText(NAME_LOGS_MENU);
                        initViewConsoleLogs();
                        break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                        initMenu() {
        mAction_add_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddHostDialog();
            }
        });
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.findViewById(R.id.clipper).setVisibility(View.VISIBLE);
                mFab.setVisibility(View.GONE);
            }
        });
        mAction_deleteall.setOnClickListener(onClickTopMenu());
        mAction_import.setOnClickListener(onClickTopMenu());
        mAction_export.setOnClickListener(onClickTopMenu());
        mClipper.setOnClickListener(onClickTopMenu());
        setToolbarTitle(null, mDnsSpoof.getDnsConf().listDomainSpoofable.size() + " domain spoofable");
    }

    private View.OnClickListener        onClickTopMenu() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.findViewById(R.id.clipper).setVisibility(View.GONE);
                mFab.setVisibility(View.VISIBLE);
                switch (v.getId()) {
                    case R.id.action_export:
                        final DialogQuestionWithInput dialog = new DialogQuestionWithInput(mInstance)
                                .setIcon(R.drawable.dns)
                                .setTitle("Exporter la liste des dns")
                                .setHintToEDFirstQuestion(mDnsSpoof.getDnsConf().PATH_HOST_FILE)
                                .hideSecondInput()
                                .setHintToTILFirstQuestion("Name of conf file");
                        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                String nameOfFile = dialog.getFirstInputQuestion();
                                if (nameOfFile.contains(".") || nameOfFile.length() < 4) {
                                    Snackbar.make(mCoordinatorLayout, "Syntax incorrect", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDnsSpoof.saveDnsConf(nameOfFile);
                                }
                            }
                        })
                                .show();
                        break;
                    case R.id.action_deleteall:
                        mDnsSpoof.clear();
                        mDnsSpoofAdapter.notifyDataSetChanged();
                        break;
                    case R.id.action_import:
                        final DialogQuestionWithInput dialog2 = new DialogQuestionWithInput(mInstance)
                                .setIcon(R.drawable.dns)
                                .setTitle("Importez la liste des dns")
                                .hideSecondInput()
                                .setHintToTILFirstQuestion("Name of file")
                                .setHintToEDFirstQuestion(mDnsSpoof.getDnsConf().PATH_HOST_FILE);
                        dialog2.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                String nameOfFile = dialog2.getFirstInputQuestion();
                                if (nameOfFile.contains(".") || nameOfFile.length() < 4) {
                                    Snackbar.make(mCoordinatorLayout, "Syntax incorrect", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDnsSpoof.saveDnsConf(nameOfFile);
                                }
                            }
                        }).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void                        onAddHostDialog() {
        final DialogQuestionWithInput dialog = new DialogQuestionWithInput(mInstance)
                .setIcon(R.drawable.dns)
                .setTitle("Ajouter un domain")
                .setHintToTILFirstQuestion("Domain")
                .setHintToEDFirstQuestion("Ex: google.com")
                .setHintToTILSecoundQuestion("Ip address")
                .setHintToEDSecoundQuestion("Ex: " + mSingleton.network.myIp);
        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                String host = dialog.getFirstInputQuestion(), ip = dialog.getSecoundInputQuestion();
                Snackbar mySnackbar;
                View.OnClickListener retryListene = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddHostDialog();
                    }
                };
                if (host.isEmpty() || !host.contains(".") || host.length() <= 4) {
                    mySnackbar = Snackbar.make(mCoordinatorLayout, "Host incorrect", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("RETRY", retryListene);
                } else if (ip.isEmpty() || StringUtils.countMatches(ip, ".") != 3) {
                    mySnackbar = Snackbar.make(mCoordinatorLayout, "Ip incorrect", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("RETRY", retryListene);
                } else {
                    mDnsSpoof.getDnsConf().addHost(ip, host);
                    mDnsSpoofAdapter.notifyItemInserted(0);
                    mySnackbar = Snackbar.make(mCoordinatorLayout,  host + " -> " + ip, Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("SAVE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDnsSpoof.getDnsConf().saveConf();
                        }
                    });
                }
                mySnackbar.show();
            }
        })
        .show();
    }

    private void                        initRVConfiguration() {
        mDnsSpoofAdapter = new DnsSpoofConfAdapter(this, mDnsSpoof.getDnsConf().listDomainSpoofable);
        mDnsSpoof_RV.setAdapter(mDnsSpoofAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));
        if (mDnsSpoof.getDnsConf().listDomainSpoofable.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, "No dnsmasq configuration could be loaded", Snackbar.LENGTH_LONG);
            Log.e(TAG, "No dnsmasq configuration saved");
        } else {
            textEmpty.setVisibility(View.GONE);
        }
        mDnsConsoleAdapter = new DnsLogsAdapter(this, mDnsSpoof.mDnsLogs);
        mDnsSpoof.setRV_Adapter(mDnsConsoleAdapter);
    }

    private void                        initViewConsoleLogs() {
        mDnsSpoof_RV.setAdapter(mDnsConsoleAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    private void                        initSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (title.getText().toString().contains(NAME_CONF_MENU)) {//CONF VIEW
                    mDnsSpoofAdapter.filtering(query);
                } else {//Logs VIEW
                    mDnsConsoleAdapter.filtering(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (title.getText().toString().contains(NAME_CONF_MENU)) {//CONF VIEW
                    mDnsSpoofAdapter.filtering("");
                } else {//Logs VIEW
                    mDnsConsoleAdapter.filtering("");
                }
                return false;
            }
        });
    }

    public void                         onDnsmasqConfChanged(String msg) {
        Snackbar mySnackbar = Snackbar.make(mCoordinatorLayout, msg, Snackbar.LENGTH_LONG);
        mySnackbar.setAction("SAVE CONFIG", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDnsSpoof.getDnsConf().saveConf();
            }
        });
        mySnackbar.show();
    }

    public void                         setToolbarTitle(final String title, final String subtitle) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                         onBackPressed() {
        super.onBackPressed();
        if (title.getText().toString().contains(NAME_LOGS_MENU) ) {
            initRVConfiguration();
        }
    }
}