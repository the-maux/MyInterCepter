package fr.allycs.app.View.TargetMenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.R;

/**
 * Menu
 */
public class                    TargetMenuActivity extends MyActivity {
    private String              TAG = "TargetMenuActivity";
    private TargetMenuActivity  mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private Singleton           mSingleton = Singleton.getInstance();

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
    }

    protected void              onResume() {
        super.onResume();
        RecyclerView RV_menu = findViewById(R.id.RV_menu);
        RV_menu.setAdapter(new MenuAdapter(this));
        RV_menu.setLayoutManager(new GridLayoutManager(this, 2));
    }

    /*private void                initflags() {
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
    }*/
    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

}
