package su.sniff.cepter.View.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.List;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.TcpdumpHostCheckerADapter;
import su.sniff.cepter.View.WiresharkActivity;

/**
 * Created by maxim on 14/08/2017.
 */

public class                    HostChoiceDialog {
    private RecyclerView        RV_host;

    public void               ShowDialog(Activity activity, AlertDialog.Builder dialog, final List<Host> listHostSelected, final TextView monitorHost){
        dialog.setCancelable(false);
        dialog.setTitle("Choix des cibles");
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_hostchoice, null);
        dialog.setView(dialogView);
        RV_host = (RecyclerView) dialogView.findViewById(R.id.RV_host);
        TcpdumpHostCheckerADapter adapter = new TcpdumpHostCheckerADapter(activity, Singleton.getInstance().hostsList, listHostSelected);
        RV_host.setAdapter(adapter);
        RV_host.setHasFixedSize(true);
        RV_host.setLayoutManager(new LinearLayoutManager(activity));
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                monitorHost.setText(listHostSelected.size() + " target");
            }
        });
        dialog.show();
    }


}
