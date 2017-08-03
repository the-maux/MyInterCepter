package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

/**
 * Created by root on 03/08/17.
 */

public class                    MenuActivity extends Activity {
    private String              TAG = "MenuActivity";
    private MenuActivity        mInstance = this;
    private CoordinatorLayout   coordinatorLayout;

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String str = getIntent().getExtras().getString("Key_String");// I need to understand this
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
                Class choice = TabActivitys.class;
                switch (clickChoice) {
                    case Nmap:
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Nmap non implémenté", Snackbar.LENGTH_LONG).show();
                        choice = null;
                        break;
                    case CepterMitm:
                        choice = TabActivitys.class;
                        break;
                    case ARPCage:
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Cage ARP non implémenté", Snackbar.LENGTH_LONG).show();
                        choice = null;
                        break;
                    case DnsSpoofing:
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Spoof DNS non implémenté", Snackbar.LENGTH_LONG).show();
                        choice = null;
                        break;
                    case Wireshark:
                        choice = TabActivitys.class;
                        break;
                    case DoraDiagnostic:
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Dora non implémenté", Snackbar.LENGTH_LONG).show();
                        choice = null;
                        break;
                    case Metasploit:
                        Snackbar.make(coordinatorLayout, "Fonctionnalité Nmap non implémenté", Snackbar.LENGTH_LONG).show();
                        choice = null;
                        break;
                    case Settings:
                        choice = TabActivitys.class;
                        break;
                }
                if (choice != null) {
                    String cmd = "-gw " + globalVariable.gw_ip;
                    Intent intent = new Intent(mInstance, choice);
                    intent.putExtra("Key_String", cmd);
                    intent.putExtra("Key_String_origin", "Oui tu venais de la");
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private enum                choice {
        Nmap, CepterMitm, ARPCage, DnsSpoofing, Wireshark, DoraDiagnostic, Metasploit, Settings
    }

}
