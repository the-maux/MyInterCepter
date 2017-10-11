package su.sniff.cepter.View;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
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

import com.github.clans.fab.FloatingActionButton;

import java.io.File;

import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.BinaryWrapper.DnsSpoof;
import su.sniff.cepter.Model.Target.DNSSpoofItem;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.ConsoleLogAdapter;
import su.sniff.cepter.View.Adapter.DnsLogsAdapter;
import su.sniff.cepter.View.Adapter.DnsSpoofConfAdapter;
import su.sniff.cepter.View.Dialog.TIL_dialog;

/**
 * TODO:
 *      + Read ./files/dnsSpoof.conf
 *      + initViewConf in List
 *      + displayt in RV
 *      + add to file
 * Good luck bra
 */
public class                            DNSSpoofingActivity extends MyActivity {
    private String                      TAG = "DNSSpoofingActivity";
    private DNSSpoofingActivity         mInstance = this;
    private CoordinatorLayout           mCoordinatorLayout;
    private Toolbar                     mToolbar;
    private SearchView                  mFilterText;
    private ImageButton                 mAction_add_host, mSettingsBtn;
    private TabLayout                   tabs;
    private FloatingActionButton        mFab;
    private RecyclerView                mDnsSpoof_RV;
    private RelativeLayout              mClipper;
    private TextView                    mAction_deleteall, mAction_import, mAction_export, textEmpty, title;
    private Singleton                   mSingleton = Singleton.getInstance();
    private DnsSpoof                    mDnsSpoof = mSingleton.dnsSpoofed;
    private DnsSpoofConfAdapter         mDnsSpoofAdapter;
    private DnsLogsAdapter              mDnsConsoleAdapter;

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnsspoofing);
        initXml();
        initFabs();
        initMenu();
        initTabs();
        initViewConf();
    }

    private void                        initXml() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        mFilterText = (SearchView) findViewById(R.id.searchView);
        mAction_add_host = (ImageButton) findViewById(R.id.action_add_host);
        mSettingsBtn = (ImageButton) findViewById(R.id.settings);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDnsSpoof_RV = (RecyclerView) findViewById(R.id.dnsSpoof_RV);
        mClipper = (RelativeLayout) findViewById(R.id.clipper);
        title = (TextView) findViewById(R.id.title);
        mAction_deleteall = (TextView) findViewById(R.id.action_deleteall);
        mAction_import = (TextView) findViewById(R.id.action_import);
        mAction_export = (TextView) findViewById(R.id.action_export);
        textEmpty = (TextView) findViewById(R.id.textEmpty);
        tabs = (TabLayout) findViewById(R.id.tabs);
    }

    private void                        initFabs() {
        if (mSingleton.isDnsSpoofActived()) {
            mFab.setImageResource(R.mipmap.ic_stop);
        } else {
            mFab.setImageResource(android.R.drawable.ic_media_play);
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSingleton.setDnsSpoofActived(!mSingleton.isDnsSpoofActived());
                if (mSingleton.isDnsSpoofActived()) {
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
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "Domains":
                        Log.d(TAG, "Loading View 1");
                        title.setText("List of spoofed domain(s):");
                        initViewConf();
                        break;
                    case "Console":
                        Log.d(TAG, "Loading View 2");
                        title.setText("Dns ouput:");
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
        mToolbar.setTitle(mDnsSpoof.listDomainSpoofed.size() + " domain spoofed");
    }

    private View.OnClickListener        onClickTopMenu() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.findViewById(R.id.clipper).setVisibility(View.GONE);
                mFab.setVisibility(View.VISIBLE);
                switch (v.getId()) {
                    case R.id.action_export:
                        final TIL_dialog dialog = new TIL_dialog(mInstance)
                                .setTitle("Exporter la liste des dns")
                                .setHintText("/etc/dnsmasq.hosts")
                                .setHint("Name of file");
                        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                String nameOfFile = dialog.getText();
                                if (nameOfFile.contains(".") || nameOfFile.length() < 4) {
                                    Snackbar.make(mCoordinatorLayout, "Syntax incorrect", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDnsSpoof.readDnsFromFile(new File(nameOfFile));
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
                        final TIL_dialog dialog2 = new TIL_dialog(mInstance)
                                .setTitle("Exporter la liste des dns")
                                .setHintText("/etc/dnsmasq.hosts")
                                .setHint("Name of file");
                        dialog2.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                String nameOfFile = dialog2.getText();
                                if (nameOfFile.contains(".") || nameOfFile.length() < 4) {
                                    Snackbar.make(mCoordinatorLayout, "Syntax incorrect", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDnsSpoof.dumpDomainList(nameOfFile);
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
        final TIL_dialog dialog = new TIL_dialog(mInstance)
                .setTitle("Add mhost")
                .setHint("IP:www.domain.fr");
        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                String domain = dialog.getText();
                if (domain.contains(":")) {
                    String[] tmp = domain.split(":");
                    onCheckAddedHost(tmp[0], tmp[1]);
                } else {
                    Snackbar.make(mCoordinatorLayout, "Format non respecté", Snackbar.LENGTH_LONG);
                }
            }
        })
        .show();
    }

    private void                        onCheckAddedHost(String ip, String domain) {//ip:domain
        DNSSpoofItem dnsIntercept = new DNSSpoofItem(ip, domain);
        mDnsSpoof.listDomainSpoofed.add(0, dnsIntercept);
        mDnsSpoofAdapter.notifyItemInserted(0);
        mToolbar.setTitle(mDnsSpoof.listDomainSpoofed.size() + " domain spoofed");
        if (textEmpty.getVisibility() == View.GONE)
            textEmpty.setVisibility(View.GONE);
        Snackbar.make(mCoordinatorLayout,  dnsIntercept.domainSpoofed + " -> " + dnsIntercept.domainAsked, Snackbar.LENGTH_LONG);
    }

    private void                        initViewConf() {
        mDnsSpoofAdapter = new DnsSpoofConfAdapter(this, mDnsSpoof.listDomainSpoofed);
        mDnsSpoof_RV.setAdapter(mDnsSpoofAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));
        if (mDnsSpoof.listDomainSpoofed.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, "Aucun dns enregistré", Snackbar.LENGTH_LONG);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
        mDnsConsoleAdapter = new DnsLogsAdapter(this, mDnsSpoof.mDomainLogs);
        mDnsSpoof.setRV_Adapter(mDnsConsoleAdapter);
    }

    private void                        initViewConsoleLogs() {
        mDnsSpoof_RV.setAdapter(mDnsConsoleAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    public void                         actualiseDomainspoofed() {
        mToolbar.setTitle(mDnsSpoof.listDomainSpoofed.size() + " domain spoofed");
    }
}