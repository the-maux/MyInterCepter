package su.sniff.cepter.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.R;

/**
 * Menu
 */
public class                    MenuActivity extends MyActivity {
    private String              TAG = "MenuActivity";
    private MenuActivity        mInstance = this;
    private CoordinatorLayout   coordinatorLayout;

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initXml();
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
                        choice = DNSSpoofingActivity.class;
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Dns Spoofing non implémenté", Snackbar.LENGTH_LONG).show();
                        break;
                    case Wireshark:
                        choice = WiresharkActivity.class;
                        break;
                    case DoraDiagnostic:
                        if (Singleton.getInstance().hostsList == null) {
                            Snackbar.make(coordinatorLayout, "Dora need targets to work", Snackbar.LENGTH_LONG).show();
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
