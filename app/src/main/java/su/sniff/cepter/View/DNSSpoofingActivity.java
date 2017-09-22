package su.sniff.cepter.View;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import su.sniff.cepter.Controller.System.FilesUtil;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Pcap.DnsIntercept;
import su.sniff.cepter.Model.Pcap.MyObject;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.DnsSpoofAdapter;
import su.sniff.cepter.View.Dialog.TIL_dialog;

/**
 * TODO:
 *      + Read ./files/dnsSpoof.conf
 *      + init in List
 *      + displayt in RV
 *      + add to file
 * Good luck bra
 */
public class                            DNSSpoofingActivity extends MyActivity {
    private String                      TAG = "DNSSpoofingActivity";
    private DNSSpoofingActivity         mInstance = this;
    private CoordinatorLayout           mCoordinatorLayout;
    private Toolbar                     toolbar;
    private SearchView                  filterText;
    private ImageButton                 action_add_host, mSettingsBtn;
    private TabItem                     listSpoof, historique;
    private FloatingActionButton        mFab;
    private RecyclerView                mDnsSpoof_RV;
    private RelativeLayout              clipper;
    private TextView                    action_deleteall, action_import, action_export;
    private Singleton                   singleton = Singleton.getInstance();
    private DnsSpoofAdapter             mDnsSpoofAdapter;

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnsspoofing);
        initXml();
        initMenu();
        init();
    }

    private void                        initXml() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        filterText = (SearchView) findViewById(R.id.filterText);
        action_add_host = (ImageButton) findViewById(R.id.action_add_host);
        mSettingsBtn = (ImageButton) findViewById(R.id.action_settingsBtn);
        listSpoof = (TabItem) findViewById(R.id.listSpoof);
        historique = (TabItem) findViewById(R.id.historique);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDnsSpoof_RV = (RecyclerView) findViewById(R.id.dnsSpoof_RV);
        clipper = (RelativeLayout) findViewById(R.id.clipper);
        action_deleteall = (TextView) findViewById(R.id.action_deleteall);
        action_import = (TextView) findViewById(R.id.action_import);
        action_export = (TextView) findViewById(R.id.action_export);
        mFab.setOnClickListener(onFabClick());
    }

    private View.OnClickListener        onFabClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleton.setDnsSpoofActived(!singleton.isDnsSpoofActived());
                if (singleton.isDnsSpoofActived()) {
                    mFab.setImageResource(R.mipmap.ic_stop);
                } else {
                    mFab.setImageResource(R.mipmap.ic_play);
                }
            }
        };
    }

    private void                        initMenu() {
        action_add_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowDialogAddHost();
            }
        });
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.findViewById(R.id.clipper).setVisibility(View.VISIBLE);
                mFab.setVisibility(View.GONE);
            }
        });
        action_deleteall.setOnClickListener(onClickTopMenu());
        action_import.setOnClickListener(onClickTopMenu());
        action_export.setOnClickListener(onClickTopMenu());
        clipper.setOnClickListener(onClickTopMenu());
        toolbar.setTitle(singleton.dnsSpoofed.size() + " domain spoofed");
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
                                .setHint("Name of file");
                        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                String nameOfFile = dialog.getText();
                                if (nameOfFile.contains(".") || nameOfFile.length() < 4) {
                                    Snackbar.make(mCoordinatorLayout, "Syntax incorrect", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        FilesUtil.dumpDnsOnFile(Singleton.getInstance().dnsSpoofed, nameOfFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                                .show();
                        break;
                    case R.id.action_deleteall:
                        Singleton.getInstance().dnsSpoofed.clear();
                        mDnsSpoofAdapter.notifyDataSetChanged();
                        break;
                    case R.id.action_import:
                        //TODO: Choose wich file
                        //Singleton.getInstance().dnsSpoofed = FilesUtil.readDnsFromFile("NameOfFile");
                        Snackbar.make(mCoordinatorLayout, "Non implémenté", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void                        onShowDialogAddHost() {
        final TIL_dialog dialog = new TIL_dialog(mInstance)
                .setTitle("Add host")
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
        DnsIntercept dnsIntercept = new DnsIntercept(ip, domain);
        singleton.dnsSpoofed.add(0, dnsIntercept);
        mDnsSpoofAdapter.notifyItemInserted(0);
        toolbar.setTitle(singleton.dnsSpoofed.size() + " domain spoofed");
        Snackbar.make(mCoordinatorLayout,  dnsIntercept.domainSpoofed + " -> " + dnsIntercept.domainAsked, Snackbar.LENGTH_LONG);
    }

    private void                        init() {
        mDnsSpoofAdapter = new DnsSpoofAdapter(this, singleton.dnsSpoofed);
        mDnsSpoof_RV.setAdapter(mDnsSpoofAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));
        if (singleton.dnsSpoofed.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, "Aucun dns enregistré", Snackbar.LENGTH_LONG);
        }
        if (singleton.isDnsSpoofActived()) {
            mFab.setImageResource(R.mipmap.ic_stop);
        } else {
            mFab.setImageResource(R.mipmap.ic_play);
        }
    }
}