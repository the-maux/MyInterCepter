package fr.allycs.app.View.TargetMenu;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Controller.AndroidUtils.Utils;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Dora;
import fr.allycs.app.Controller.Core.Tcpdump.Tcpdump;
import fr.allycs.app.R;
import fr.allycs.app.View.DnsSpoofing.DnsActivity;
import fr.allycs.app.View.Dora.DoraActivity;
import fr.allycs.app.View.Scan.NmapActivity;
import fr.allycs.app.View.Settings.SettingsActivity;
import fr.allycs.app.View.Tcpdump.WiresharkActivity;
import fr.allycs.app.View.WebServer.WebServerActivity;

/**
 * Menu
 */
public class                    TargetMenuActivity extends MyActivity {
    private String              TAG = "TargetMenuActivity";
    private TargetMenuActivity  mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private Singleton           mSingleton = Singleton.getInstance();
    private enum                choice {
        Nmap, CepterMitm, ARPCage, DnsSpoofing, Wireshark, DoraDiagnostic, WebServer, Settings
    }

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
    }

    protected void              onResume() {
        super.onResume();
        findViewById(R.id.NmapButton).setOnClickListener(onClickButton(choice.Nmap));
        findViewById(R.id.MitmButton).setOnClickListener(onClickButton(choice.CepterMitm));
        findViewById(R.id.ArpButton).setOnClickListener(onClickButton(choice.ARPCage));
        findViewById(R.id.DnsButton).setOnClickListener(onClickButton(choice.DnsSpoofing));
        findViewById(R.id.WiresharkButton).setOnClickListener(onClickButton(choice.Wireshark));
        findViewById(R.id.DoraDiagnostic).setOnClickListener(onClickButton(choice.DoraDiagnostic));
        findViewById(R.id.Metasploit).setOnClickListener(onClickButton(choice.WebServer));
        findViewById(R.id.SettingsButton).setOnClickListener(onClickButton(choice.Settings));
        initflags();
    }

    private void                initflags() {
        ColorDrawable red = new ColorDrawable(ContextCompat.getColor(this, R.color.material_red_800));
        ColorDrawable green = new ColorDrawable(ContextCompat.getColor(this, R.color.material_green_700));
        ((ImageView) findViewById(R.id.monitorDNS))
                .setImageDrawable((mSingleton.isDnsControlstarted()) ? green : red);
        Tcpdump tcpdump = Tcpdump.getTcpdump(this, false);
        if (tcpdump != null)
            ((ImageView) findViewById(R.id.monitorWireshark))
                    .setImageDrawable((tcpdump.isRunning) ? green : red);
        else
            ((ImageView) findViewById(R.id.monitorWireshark)).setImageDrawable(red);
        if (Dora.getDora(this) != null)
            ((ImageView) findViewById(R.id.monitorDora))
                    .setImageDrawable((Dora.getDora(this).isRunning()) ? green : red);
        else
            ((ImageView) findViewById(R.id.monitorDora)).setImageDrawable(red);
    }

    private View.OnClickListener onClickButton(final choice clickChoice) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrateDevice(mInstance);
                Class choice = TargetMenuActivity.class;
                Intent intent;
                Pair<View, String> p1;
                ActivityOptionsCompat options = null;
                switch (clickChoice) {
                    case Nmap:
                        choice = NmapActivity.class;
                        p1 = Pair.create(findViewById(R.id.nmapImage), "NmapIconTransition");
                        options = ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1);
                        break;
                    case CepterMitm:
                        choice = null;
                        Snackbar.make(mCoordinatorLayout, "Fonctionnalité Cepter non implémenté", Snackbar.LENGTH_LONG).show();
                        break;
                    case ARPCage:
                        choice = null;
                        Snackbar.make(mCoordinatorLayout, "Fonctionnalité Icmp non implémenté", Snackbar.LENGTH_LONG).show();
                        break;
                    case DnsSpoofing:
                        choice = DnsActivity.class;
                        p1 = Pair.create(findViewById(R.id.dnsImage), "iconDNS");
                        options = ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1);
                        break;
                    case Wireshark:
                        if (mSingleton.selectedHostsList == null) {
                            Snackbar.make(mCoordinatorLayout, "Wireshark needs target(s) to work", Snackbar.LENGTH_LONG).show();
                            choice = null;
                        } else {
                            choice = WiresharkActivity.class;
                            p1 = Pair.create(findViewById(R.id.wiresharkImage), "wiresharkIcon");
                            options = ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1);
                        }
                        break;
                    case DoraDiagnostic:
                        if (mSingleton.selectedHostsList == null) {
                            Snackbar.make(mCoordinatorLayout, "Dora needs target(s) to work", Snackbar.LENGTH_LONG).show();
                            choice = null;
                        } else {
                            choice = DoraActivity.class;
                        }
                        break;
                    case WebServer:
                        choice = WebServerActivity.class;
                        break;
                    case Settings:
                        choice = SettingsActivity.class;
                        break;
                }
                if (choice != null) {
                    intent = new Intent(mInstance, choice);
                    if (options == null)
                        startActivity(intent);
                    else
                        startActivity(intent, options.toBundle());
                }
            }
        };
    }

}
