package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.HostCheckBoxHolder;

public class OSAdapter extends RecyclerView.Adapter<HostCheckBoxHolder> {
    private String              TAG = this.getClass().getName();
    private Activity activity;
    private List<String>          osList, osListSelected;

    public                      OSAdapter(Activity activity, ArrayList<String> osList, List<String> osListSelected) {
        this.osList = osList;
        this.activity = activity;
        this.osListSelected = osListSelected;
    }
    @Override
    public HostCheckBoxHolder   onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostCheckBoxHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host_checkbox, parent, false));
    }

    @Override
    public void                 onBindViewHolder(HostCheckBoxHolder holder, int position) {
        final String os = osList.get(position);
        holder.nameOS.setText(os.replace("_", "/"));
        Host.setOsIcon(activity, os, holder.imageOS);
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    osListSelected.add(os);
                else
                    osListSelected.remove(os);
            }
        });
    }

    @Override
    public int                  getItemCount() {
        return osList.size();
    }
}