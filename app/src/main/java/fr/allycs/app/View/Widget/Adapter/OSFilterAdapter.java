package fr.allycs.app.View.Widget.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;
import fr.allycs.app.Core.Nmap.Fingerprint;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Adapter.Holder.HostSelectionHolder;

public class                    OSFilterAdapter extends RecyclerView.Adapter<HostSelectionHolder> {
    private String              TAG = "OSFilterAdapter";
    private Context             mCtx;
    private List<Os>            mOsList, mOsListSelected;

    public                      OSFilterAdapter(Activity activity, ArrayList<Os> osList, List<Os> osListSelected) {
        this.mOsList = osList;
        this.mCtx = activity;
        this.mOsListSelected = osListSelected;
    }

    public HostSelectionHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostSelectionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_host_checkbox, parent, false));
    }

    public void                 onBindViewHolder(HostSelectionHolder holder, int position) {
        final Os os = mOsList.get(position);
        holder.nameOS.setText(os.name().replace("_", "/"));
        Fingerprint.setOsIcon(mCtx, os, holder.imageOS);
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mOsListSelected.add(os);
                else
                    mOsListSelected.remove(os);
            }
        });
    }

    public int                  getItemCount() {
        return mOsList.size();
    }

}