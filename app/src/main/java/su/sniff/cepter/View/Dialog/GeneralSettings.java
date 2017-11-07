package su.sniff.cepter.View.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.CheckedTextView;

import su.sniff.cepter.Controller.Core.Singleton;
import su.sniff.cepter.Controller.Core.BinaryWrapper.Tcpdump;
import su.sniff.cepter.R;

/**
 * Created by the-maux on 19/09/17.
 */

public class                    GeneralSettings implements View.OnClickListener {
    private Activity            mActivity;
    private Tcpdump tcpdump;
    private Singleton           singleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;

    public                      GeneralSettings(Activity activity, CoordinatorLayout coordinatorLayout, Tcpdump tcpdump) {
        this.tcpdump = tcpdump;
        this.mActivity = activity;
        this.mCoordinatorLayout = coordinatorLayout;
    }
    
    
    @Override
    public void                 onClick(View v) {
        AlertDialog.Builder dialog  = new AlertDialog.Builder(mActivity, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.setCancelable(false);
        View dialogView = mActivity.getLayoutInflater().inflate(R.layout.menu_iptable_setting, null);
        dialog.setView(dialogView);
        final CheckedTextView dumpInFileChkd, sslStripChkd, lockScreenChkd, DeepAnalChkd;
        final CheckedTextView Port_redirect, Portfiltering, DnsSpoofing;
        Port_redirect = (CheckedTextView) dialogView.findViewById(R.id.Portredirect);/**TODO**/
        Portfiltering = (CheckedTextView) dialogView.findViewById(R.id.Portfiltering);/**TODO**/
        DnsSpoofing = (CheckedTextView) dialogView.findViewById(R.id.DnsSpoofing);/**TODO**/
        dumpInFileChkd = (CheckedTextView) dialogView.findViewById(R.id.dumpInFileChkd);
        sslStripChkd = (CheckedTextView) dialogView.findViewById(R.id.sslStripChkd);/**TODO**/
        lockScreenChkd = (CheckedTextView) dialogView.findViewById(R.id.lockScreenChkd);/**TODO**/
        DeepAnalChkd = (CheckedTextView) dialogView.findViewById(R.id.DeepAnalChkd);

        dumpInFileChkd.setChecked(tcpdump.isDumpingInFile);
        dumpInFileChkd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpdump.isDumpingInFile = !tcpdump.isDumpingInFile;
                dumpInFileChkd.setChecked(tcpdump.isDumpingInFile);
            }
        });
        sslStripChkd.setChecked(singleton.isSslStripModeActived());
        sslStripChkd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleton.setSslStripModeActived(!singleton.isSslStripModeActived());
                sslStripChkd.setChecked(singleton.isSslStripModeActived());
            }
        });
        lockScreenChkd.setChecked(singleton.isLockScreen());
        lockScreenChkd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleton.setLockScreen(!singleton.isLockScreen());
                lockScreenChkd.setChecked(singleton.isLockScreen());
                Snackbar.make(mCoordinatorLayout, "Non implémenté", Snackbar.LENGTH_SHORT).show();
            }
        });
        DeepAnalChkd.setChecked(tcpdump.isDeepAnalyseTrame());
        DeepAnalChkd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpdump.setDeepAnalyseTrame(!tcpdump.isDeepAnalyseTrame());
                DeepAnalChkd.setChecked(tcpdump.isDeepAnalyseTrame());
                Snackbar.make(mCoordinatorLayout, "Non implémenté", Snackbar.LENGTH_SHORT).show();
            }
        });
        Port_redirect.setOnClickListener(onPortMitm(true));
        Portfiltering.setOnClickListener(onPortMitm(false));
        DnsSpoofing.setOnClickListener(onDnsSpoof(DnsSpoofing));
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(mCoordinatorLayout, "Settings set", Snackbar.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private View.OnClickListener onPortMitm(final boolean flag) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) { /** TODO: PortRedirect */

                } else {    /** TODO: PortFiltering */

                }
                Snackbar.make(mCoordinatorLayout, "Non implémenté", Snackbar.LENGTH_SHORT).show();
            }
        };
    }

    private View.OnClickListener onDnsSpoof(final CheckedTextView dnsSpoofing) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dnsSpoofing.setChecked(singleton.isDnsSpoofActived());
                dnsSpoofing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        singleton.setDnsSpoofActived(!singleton.isDnsSpoofActived());
                        dnsSpoofing.setChecked(singleton.isDnsSpoofActived());
                    }
                });
            }
        };
    }
}
