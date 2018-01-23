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
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Tools.Dns.DnsControl;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Misc.Utils;
import fr.allycs.app.Model.Target.DNSSpoofItem;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Adapter.DnsLogsAdapter;
import fr.allycs.app.View.Widget.Adapter.DnsSpoofConfAdapter;
import fr.allycs.app.View.Widget.Dialog.AddDnsDialog;


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
    private DnsControl                  mDnsSpoof = mSingleton.getDnsControler();
    private DnsSpoofConfAdapter         mDnsSpoofAdapter;
    private DnsLogsAdapter              mDnsConsoleAdapter;

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnsspoofing);
        initXml();
        initFab();
        initMenu();
        initTabs();
        initViewConf();
        initSearchView();
        mDnsSpoof.setToolbar(this);
    }

    private void                        initXml() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mAction_add_host = (ImageButton) findViewById(R.id.action_add_host);
        mSettingsBtn = (ImageButton) findViewById(R.id.settings);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDnsSpoof_RV = (RecyclerView) findViewById(R.id.dnsSpoof_RV);
        mClipper = (RelativeLayout) findViewById(R.id.clipper);
        title = (TextView) findViewById(R.id.host);
        mAction_deleteall = (TextView) findViewById(R.id.action_deleteall);
        mAction_import = (TextView) findViewById(R.id.action_import);
        mAction_export = (TextView) findViewById(R.id.action_export);
        textEmpty = (TextView) findViewById(R.id.textEmpty);
        mTabs = (TabLayout) findViewById(R.id.tabs);
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
                    mFab.setImageResource(R.mipmap.ic_stop);
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
                    case "Domains":
                        title.setText("List of spoofable domain(s):");
                        initViewConf();
                        break;
                    case "Console":
                        title.setText("Logs:");
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
        mToolbar.setTitle(mDnsSpoof.getDnsConf().listDomainSpoofed.size() + " domain spoofable");
    }

    private View.OnClickListener        onClickTopMenu() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.findViewById(R.id.clipper).setVisibility(View.GONE);
                mFab.setVisibility(View.VISIBLE);
                switch (v.getId()) {
                    case R.id.action_export:
                        final AddDnsDialog dialog = new AddDnsDialog(mInstance)
                                .setIcon(R.drawable.dns)
                                .setTitle("Exporter la liste des dns")
                                .setHintText(mDnsSpoof.getDnsConf().PATH_HOST_FILE)
                                .setHint("Name of conf file");
                        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                String nameOfFile = dialog.getHost();
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
                        final AddDnsDialog dialog2 = new AddDnsDialog(mInstance)
                                .setIcon(R.drawable.dns)
                                .setTitle("Importez la liste des dns")
                                .setHintText(mDnsSpoof.getDnsConf().PATH_HOST_FILE)
                                .setHint("Name of file");
                        dialog2.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                String nameOfFile = dialog2.getHost();
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
        final AddDnsDialog dialog = new AddDnsDialog(mInstance)
                .setIcon(R.drawable.dns)
                .setTitle("Ajouter un domain");
        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                String host = dialog.getHost(), ip = dialog.getIp();
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

    private void                        initViewConf() {
        mDnsSpoofAdapter = new DnsSpoofConfAdapter(this, mDnsSpoof.getDnsConf().listDomainSpoofed);
        mDnsSpoof_RV.setAdapter(mDnsSpoofAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));
        if (mDnsSpoof.getDnsConf().listDomainSpoofed.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, "Aucun dns enregistré", Snackbar.LENGTH_LONG);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
        mDnsConsoleAdapter = new DnsLogsAdapter(this, mDnsSpoof.mDnsLogs);
        mDnsSpoof.setRV_Adapter(mDnsConsoleAdapter);
    }

    private void                        initSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtering(query);
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
                filtering("");
                return false;
            }
        });
    }

    private void                        filtering(String query) {
        int tab = mTabs.getSelectedTabPosition();
        if (tab == 0) {//CONF VIEW
            mDnsSpoofAdapter.filtering(query);
        } else {//Logs VIEW
            mDnsConsoleAdapter.filtering(query);
        }
    }

    private void                        initViewConsoleLogs() {
        mDnsSpoof_RV.setAdapter(mDnsConsoleAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    public void                         updateToolbarTitle(DNSSpoofItem domain) {
        mToolbar.setTitle(mDnsSpoof.getDnsConf().listDomainSpoofed.size() + " domain spoofed");
        Snackbar mySnackbar = Snackbar.make(mCoordinatorLayout, "Domain " + domain + " deleted", Snackbar.LENGTH_LONG);
        mySnackbar.setAction("SAVE", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDnsSpoof.getDnsConf().saveConf();
            }
        });
        mySnackbar.show();
    }

    public void                         titleToolbar(final String subtitle) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToolbar.setSubtitle(subtitle);
            }
        });
    }
}