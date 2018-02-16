package fr.allycs.app.View.Widget.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import fr.allycs.app.R;

public class                    RV_dialog {
    private RecyclerView        mRV_host;
    protected AlertDialog.Builder dialog;

    public                      RV_dialog(Activity activity) {
        dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(true);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_recyclerview, null);
        dialog.setView(dialogView);
        dialog.setIcon(R.drawable.ico);
        mRV_host = dialogView.findViewById(R.id.RL_host);
        mRV_host.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRV_host.getContext(),
                manager.getOrientation());
        mRV_host.addItemDecoration(dividerItemDecoration);
        mRV_host.setLayoutManager(manager);
    }

    public RV_dialog            setAdapter(RecyclerView.Adapter adapter, boolean isLoading) {
        mRV_host.setAdapter(adapter);
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

    public RV_dialog            setLayoutManager(RecyclerView.LayoutManager manager) {
        mRV_host.setLayoutManager(manager);
        return this;
    }

}
