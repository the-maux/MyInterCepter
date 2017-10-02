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

import su.sniff.cepter.Controller.System.FilesUtil;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Pcap.DNSSpoofItem;
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
    private Toolbar                     mToolbar;
    private SearchView                  mFilterText;
    private ImageButton                 mAction_add_host, mSettingsBtn;
    private TabItem                     listSpoof, historique;
    private FloatingActionButton        mFab;
    private RecyclerView                mDnsSpoof_RV;
    private RelativeLayout              mClipper;
    private TextView                    mAction_deleteall, mAction_import, mAction_export, textEmpty;
    private Singleton                   mSingleton = Singleton.getInstance();
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        mFilterText = (SearchView) findViewById(R.id.searchView);
        mAction_add_host = (ImageButton) findViewById(R.id.action_add_host);
        mSettingsBtn = (ImageButton) findViewById(R.id.settings);
        listSpoof = (TabItem) findViewById(R.id.listSpoof);
        historique = (TabItem) findViewById(R.id.historique);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDnsSpoof_RV = (RecyclerView) findViewById(R.id.dnsSpoof_RV);
        mClipper = (RelativeLayout) findViewById(R.id.clipper);
        mAction_deleteall = (TextView) findViewById(R.id.action_deleteall);
        mAction_import = (TextView) findViewById(R.id.action_import);
        mAction_export = (TextView) findViewById(R.id.action_export);
        textEmpty = (TextView) findViewById(R.id.textEmpty);
        mFab.setOnClickListener(onFabClick());
    }

    private View.OnClickListener        onFabClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSingleton.setDnsSpoofActived(!mSingleton.isDnsSpoofActived());
                if (mSingleton.isDnsSpoofActived()) {
                    mFab.setImageResource(R.mipmap.ic_stop);
                } else {
                    mFab.setImageResource(R.mipmap.ic_play);
                }
            }
        };
    }

    private void                        initMenu() {
        mAction_add_host.setOnClickListener(new View.OnClickListener() {
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
        mAction_deleteall.setOnClickListener(onClickTopMenu());
        mAction_import.setOnClickListener(onClickTopMenu());
        mAction_export.setOnClickListener(onClickTopMenu());
        mClipper.setOnClickListener(onClickTopMenu());
        mToolbar.setTitle(mSingleton.dnsSpoofed.listDomainSpoofed.size() + " domain spoofed");
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
                                    mSingleton.dnsSpoofed.dumpDomainList();
                                }
                            }
                        })
                                .show();
                        break;
                    case R.id.action_deleteall:
                        mSingleton.dnsSpoofed.clear();
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
        mSingleton.dnsSpoofed.listDomainSpoofed.add(0, dnsIntercept);
        mDnsSpoofAdapter.notifyItemInserted(0);
        mToolbar.setTitle(mSingleton.dnsSpoofed.listDomainSpoofed.size() + " domain spoofed");
        if (textEmpty.getVisibility() == View.GONE)
            textEmpty.setVisibility(View.GONE);
        Snackbar.make(mCoordinatorLayout,  dnsIntercept.domainSpoofed + " -> " + dnsIntercept.domainAsked, Snackbar.LENGTH_LONG);
    }

    private void                        init() {
        mDnsSpoofAdapter = new DnsSpoofAdapter(this, mSingleton.dnsSpoofed.listDomainSpoofed);
        mDnsSpoof_RV.setAdapter(mDnsSpoofAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));
        if (mSingleton.dnsSpoofed.listDomainSpoofed.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, "Aucun dns enregistré", Snackbar.LENGTH_LONG);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
        if (mSingleton.isDnsSpoofActived()) {
            mFab.setImageResource(R.mipmap.ic_stop);
        } else {
            mFab.setImageResource(android.R.drawable.ic_media_play);
        }
    }
}