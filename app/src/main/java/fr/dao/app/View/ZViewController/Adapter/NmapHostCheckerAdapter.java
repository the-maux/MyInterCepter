package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Adapter.Holder.HostSelectionHolder;

public class                    NmapHostCheckerAdapter extends RecyclerView.Adapter<HostSelectionHolder> {
    private String              TAG = this.getClass().getName();
    private Activity mActivity;
    private List<Host> mHosts;

    public                      NmapHostCheckerAdapter(Activity activity, List<Host> hosts) {
        this.mHosts = hosts;
        this.mActivity = activity;
    }

    public HostSelectionHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostSelectionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_host_checkbox, parent, false));
    }

    public void                 onBindViewHolder(HostSelectionHolder holder, int position) {
        Host host = mHosts.get(position);
        holder.itemView.setOnClickListener(onClickCard(host));
        holder.nameOS.setText(host.ip);
        holder.checkBox.setVisibility(View.INVISIBLE);
        MyGlideLoader.setOsIcon(host, holder.imageOS);
    }

    private View.OnClickListener onClickCard(final Host host) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((NmapActivity)mActivity).newTarget(title);
            }
        };
    }

    public int                  getItemCount() {
        return mHosts.size();
    }
}
