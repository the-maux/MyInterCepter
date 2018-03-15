package fr.dao.app.View.Widget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.dao.app.Model.Net.Port;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Widget.Adapter.Holder.PortHolder;

public class                    PortAdapter extends RecyclerView.Adapter<PortHolder> {
    private String              TAG = "PortAdapter";
    private Activity            mActivity;
    private ArrayList<Port>     mPorts;

    public                      PortAdapter(MyActivity activity, ArrayList<Port> ports) {
        this.mActivity = activity;
        this.mPorts = ports;
    }

    @NonNull
    public PortHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PortHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_port, parent, false));
    }

    public void                 onBindViewHolder(PortHolder holder, int position) {
        if (position == 0) {

        } else {
            Port port = mPorts.get(position - 1);
            String portTitle = port.getPort() + "/" + port.protocol;
            holder.port.setText(portTitle);
            holder.state.setText(port.state.toString());
            holder.service.setText(port.service);
        }
    }

    public int                  getItemCount() {
        return mPorts.size() + 1;
    }

}
