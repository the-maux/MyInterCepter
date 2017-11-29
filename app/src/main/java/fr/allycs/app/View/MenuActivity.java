package fr.allycs.app.View;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.BinaryWrapper.Dora;
import fr.allycs.app.Controller.Core.BinaryWrapper.Tcpdump;
import fr.allycs.app.R;
import fr.allycs.app.View.Dora.DoraActivity;

/**
 * Menu
 */
public class                    MenuActivity extends MyActivity {
    private String              TAG = "MenuActivity";
    private MenuActivity        mInstance = this;
    private CoordinatorLayout   coordinatorLayout;
    private Singleton           singleton = Singleton.getInstance();

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initXml();
    }

    @Override
    protected void              onResume() {
        super.onResume();
        initflags();
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        findViewById(R.id.NmapButton).setOnClickListener(onClickButton(choice.Nmap));
        findViewById(R.id.MitmButton).setOnClickListener(onClickButton(choice.CepterMitm));
        findViewById(R.id.ArpButton).setOnClickListener(onClickButton(choice.ARPCage));
        findViewById(R.id.DnsButton).setOnClickListener(onClickButton(choice.DnsSpoofing));
        findViewById(R.id.WiresharkButton).setOnClickListener(onClickButton(choice.Wireshark));
        findViewById(R.id.DoraDiagnostic).setOnClickListener(onClickButton(choice.DoraDiagnostic));
        findViewById(R.id.Metasploit).setOnClickListener(onClickButton(choice.Metasploit));
        findViewById(R.id.SettingsButton).setOnClickListener(onClickButton(choice.Settings));
    }

    private void                initflags() {
        ColorDrawable red = new ColorDrawable(ContextCompat.getColor(this, R.color.material_red_700));
        ColorDrawable green = new ColorDrawable(ContextCompat.getColor(this, R.color.material_green_700));
        ((CircleImageView) findViewById(R.id.monitorDNS)).setImageDrawable((singleton.isDnsSpoofActived()) ? green : red);

        if (Tcpdump.getTcpdump(this) != null)
            ((CircleImageView) findViewById(R.id.monitorWireshark)).setImageDrawable((Tcpdump.getTcpdump(this).isRunning) ? green : red);
        else
            ((CircleImageView) findViewById(R.id.monitorWireshark)).setImageDrawable(red);
        if (Dora.getDora(this) != null)
            ((CircleImageView) findViewById(R.id.monitorDora)).setImageDrawable((Dora.getDora(this).isRunning()) ? green : red);
        else
            ((CircleImageView) findViewById(R.id.monitorDora)).setImageDrawable(red);
    }

    private View.OnClickListener onClickButton(final choice clickChoice) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class choice = MenuActivity.class;
                switch (clickChoice) {
                    case Nmap:
                        choice = NmapActivity.class;
                        break;
                    case CepterMitm:
                        choice = null;
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Cepter non implémenté", Snackbar.LENGTH_LONG).show();
                        break;
                    case ARPCage:
                        choice = null;
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Icmp non implémenté", Snackbar.LENGTH_LONG).show();
                        break;
                    case DnsSpoofing:
                        choice = DnsActivity.class;
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Dns Spoofing non implémenté", Snackbar.LENGTH_LONG).show();
                        break;
                    case Wireshark:
                        if (Singleton.getInstance().hostsList == null) {
                            Snackbar.make(coordinatorLayout, "Wireshark needs target(s) to work", Snackbar.LENGTH_LONG).show();
                            choice = null;
                        } else {
                            choice = WiresharkActivity.class;
                        }
                        break;
                    case DoraDiagnostic:
                        if (Singleton.getInstance().hostsList == null) {
                            Snackbar.make(coordinatorLayout, "Dora needs target(s) to work", Snackbar.LENGTH_LONG).show();
                            choice = null;
                        } else {
                            choice = DoraActivity.class;
                        }
                        break;
                    case Metasploit:
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Metasploit non implémenté", Snackbar.LENGTH_LONG).show();
                        choice = null;
                        break;
                    case Settings:
                        choice = SettingsActivity.class;
                        break;
                }
                if (choice != null) {
                    Intent intent = new Intent(mInstance, choice);
                    startActivity(intent);
                    }
            }
        };
    }

    private enum                choice {
        Nmap, CepterMitm, ARPCage, DnsSpoofing, Wireshark, DoraDiagnostic, Metasploit, Settings
    }

}
