package su.sniff.cepter.View;

import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;

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
    private Toolbar                     toolbar;
    private SearchView                  filterText;
    private ImageButton                 action_add_host, mSettingsBtn;
    private TabItem                     listSpoof, historique;
    private FloatingActionButton        fab;
    private RecyclerView                dnsSpoof_RV;
    private RelativeLayout              clipper;
    private TextView                    action_deleteall, action_import, action_export;

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnsspoofing);
        initXml();
        initMenu();

    }

    private void initMenu() {
        action_add_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mAddHostDialog.show();
                //TODO: Faire le add Host
            }
        });
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.findViewById(R.id.clipper).setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            }
        });
    }

    private void                        initXml() {
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        filterText = (SearchView) findViewById(R.id.filterText);
        action_add_host = (ImageButton) findViewById(R.id.action_add_host);
        mSettingsBtn = (ImageButton) findViewById(R.id.action_settingsBtn);
        listSpoof = (TabItem) findViewById(R.id.listSpoof);
        historique = (TabItem) findViewById(R.id.historique);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        dnsSpoof_RV = (RecyclerView) findViewById(R.id.dnsSpoof_RV);
        clipper = (RelativeLayout) findViewById(R.id.clipper);
    }

}