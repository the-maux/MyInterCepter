package fr.dao.app.View.ZViewController.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Dialog.RV_dialog;
import fr.dao.app.View.ZViewController.PortAdapter;


public class                    HostDetailAdapter extends RecyclerView.Adapter<HostDetailAdapter.HostDetailHolder> {
    private String              TAG = "HostDetailAdapter";
    private MyActivity          myActivity;
    private RecyclerView        mRV;
    private ClipboardManager    clipboardManager;
    private ArrayList<String[]> listDetail = new ArrayList<String[]>();
    private Host                mFocusedHost;

    public                      HostDetailAdapter(MyActivity activity, Host host) {
        this.clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        this.myActivity = activity;
        this.mFocusedHost = host;
    }

    public HostDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostDetailHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_hostdetail, parent, false));
    }

    public void                 onBindViewHolder(HostDetailHolder holder, int position) {
        final String[] line = listDetail.get(position);
        holder.title.setText(line[0]);
        holder.subtitle.setText(line[1]);
        holder.relative_layout.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                onItemClick(line, false);
                return true;
            }
        });
        holder.relative_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onItemClick(line, true);
            }
        });
    }

    private void                onItemClick(String[] line, boolean vib) {
        if (vib)
            Utils.vibrateDevice(myActivity, 100);
        if (line[0].contains("getPorts")) {
            PortAdapter adapter = new PortAdapter(myActivity, mFocusedHost.getPorts().portArrayList());
            new RV_dialog(myActivity)
                    .setAdapter(adapter, false)
                    .setTitle("getPorts : " + mFocusedHost.ip )
                    .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } else {
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(line[0], line[1]));
                myActivity.showSnackbar(line[1] + " copied");
            }
        }
    }

    public int                  getItemCount() {
        return listDetail.size();
    }

    public void                 onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRV = recyclerView;
    }

    public RecyclerView         getRecyclerview() {
        return mRV;
    }

    public void                 updateList(ArrayList<String[]> arrayList, Host host) {
        listDetail.clear();
        listDetail.addAll(arrayList);
        notifyItemRangeInserted(0, arrayList.size());
    }

    public class                HostDetailHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout relative_layout;
        public TextView title, subtitle;

        HostDetailHolder(View itemView) {
            super(itemView);
            relative_layout = itemView.findViewById(R.id.rootViewCard);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.title_valu);
        }
    }
}
