package fr.dao.app.View.Widget;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Model.Net.Port;
import fr.dao.app.Model.Target.Ports;
import fr.dao.app.R;
import fr.dao.app.View.Activity.Wireshark.WiresharkActivity;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Widget.Adapter.Holder.PcapHolder;
import fr.dao.app.View.Widget.Adapter.Holder.PortHolder;
import fr.dao.app.View.Widget.Fragment.PcapListerFragment;

public class                    PortAdapter extends RecyclerView.Adapter<PortHolder> {
    private String              TAG = "PortAdapter";
    private Activity            mActivity;
    private ArrayList<Port>     mPorts;

    public                      PortAdapter(MyActivity activity, ArrayList<Port> ports) {
        this.mActivity = activity;
        this.mPorts = ports;
    }

    public PortHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PortHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_little_cardview, parent, false));
    }

    public void                 onBindViewHolder(PortHolder holder, int position) {
        final Port port = mPorts.get(position);
        String portTitle = port.getPort() + "/" + port.protocol;
        holder.port.setText(portTitle);
        holder.state.setText(port.state.toString());
        holder.service.setText(port.service);
    }

    public int                  getItemCount() {
        return mPorts.size();
    }

}
