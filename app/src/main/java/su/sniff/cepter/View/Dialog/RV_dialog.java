package su.sniff.cepter.View.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.TcpdumpHostCheckerADapter;

/**
 * Générique AlertDialog with RecyclerView
 */
public class                    RV_dialog {
    private RecyclerView        RV_host;
    protected AlertDialog.Builder dialog;

    public                      RV_dialog(Activity activity) {
        dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(false);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_hostchoice, null);
        dialog.setView(dialogView);
        RV_host = (RecyclerView) dialogView.findViewById(R.id.RV_host);
        RV_host.setHasFixedSize(true);
        RV_host.setLayoutManager(new LinearLayoutManager(activity));
    }

    public RV_dialog            setAdapter(RecyclerView.Adapter adapter) {
        RV_host.setAdapter(adapter);
        return this;
    }

    public RV_dialog            setTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public RV_dialog            onPositiveButton(String msg, DialogInterface.OnClickListener listener) {
        dialog.setPositiveButton(msg, listener);
        return this;
    }

    public void                 show() {
        dialog.show();
    }

    public RV_dialog         setLayoutManager(RecyclerView.LayoutManager manager) {
        RV_host.setLayoutManager(manager);
        return this;
    }

}
