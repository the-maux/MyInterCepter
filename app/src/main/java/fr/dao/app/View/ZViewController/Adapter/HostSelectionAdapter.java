package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Adapter.Holder.HostSelectionHolder;


public class                    HostSelectionAdapter extends RecyclerView.Adapter<HostSelectionHolder> {
    private String              TAG = "HostSelectionAdapter";
    private Activity            mActivity;
    private List<Host>          mHosts, mListHostSelected;

    public                      HostSelectionAdapter(Activity activity, ArrayList<Host> hostsList,
                                                     List<Host> hostsSelected) {
        this.mHosts = hostsList;
        this.mActivity = activity;
        if (mListHostSelected != null)
            mListHostSelected.clear();
        this.mListHostSelected = hostsSelected;
    }

    public HostSelectionHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostSelectionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_host_checkbox, parent, false));
    }

    public void                 onBindViewHolder(HostSelectionHolder holder, int position) {
        final Host host = mHosts.get(position);
        holder.nameOS.setText(host.ip);
        MyGlideLoader.setOsIcon(host, holder.imageOS);
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mListHostSelected.add(host);
                else
                    mListHostSelected.remove(host);
            }
        });
    }

    public int                  getItemCount() {
        return mHosts.size();
    }
}
