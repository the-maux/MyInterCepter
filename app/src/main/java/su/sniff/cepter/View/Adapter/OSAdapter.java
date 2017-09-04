package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.HostCheckBoxHolder;

public class                    OSAdapter extends RecyclerView.Adapter<HostCheckBoxHolder> {
    private String              TAG = this.getClass().getName();
    private Context             mCtx;
    private List<String>        mOsList, mOsListSelected;

    public                      OSAdapter(Activity activity, ArrayList<String> osList, List<String> osListSelected) {
        this.mOsList = osList;
        this.mCtx = activity;
        this.mOsListSelected = osListSelected;
    }
    @Override
    public HostCheckBoxHolder   onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostCheckBoxHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host_checkbox, parent, false));
    }

    @Override public void       onBindViewHolder(HostCheckBoxHolder holder, int position) {
        final String os = mOsList.get(position);
        holder.nameOS.setText(os.replace("_", "/"));
        Host.setOsIcon(mCtx, os, holder.imageOS);
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

    @Override public int         getItemCount() {
        return mOsList.size();
    }
}