package fr.dao.app.View.ZViewController.Adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Net.Port;
import fr.dao.app.Model.Net.PortState;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Ports;
import fr.dao.app.R;
import fr.dao.app.View.Scan.VulnsScanActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.GenericLittleCardAvatarHolder;


public class                        ExploutAdapter extends RecyclerView.Adapter<GenericLittleCardAvatarHolder> {
    private String                  TAG = "DnsSpoofConfAdapter";
    private Singleton               mSingleton = Singleton.getInstance();
    private VulnsScanActivity       mActivity;
    private Host                    mHost;
    private Ports                   mPorts;

    public                          ExploutAdapter(VulnsScanActivity activity, Host host) {
        this.mActivity = activity;
        this.mHost = host;
        this.mPorts = mHost.getPorts();
    }

    public GenericLittleCardAvatarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GenericLittleCardAvatarHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_generic_little, parent, false));
    }

    public void                     onBindViewHolder(GenericLittleCardAvatarHolder holder, int position) {
        Port port = mPorts.getPortForAdapter(position);
        holder.card_view.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.vulnsPrimary));
        holder.title.setText(port.service);
        holder.subtitle.setText("Port: " + port.getPort());
        setLogo(holder.logo, position, port);
        setIcon(holder.icon, position);
        holder.icon.setVisibility(View.GONE);
        if (port.state == PortState.OPEN || port.state == PortState.OPEN_FILTERED)
            holder.card_view.setEnabled(true);//        setStatus(holder.status);
        else
            holder.card_view.setEnabled(false);//        setStatus(holder.status);
        holder.card_view.setOnClickListener(onCardClicked(position));
    }

    private void                    setIcon(ImageView icon, int position) {
        int res;
        switch (position) {
            case 0:
                res = R.drawable.webserver_icon;
                break;
            default:
                res = R.drawable.webserver_icon;
                break;
        }
        GlideRequest builder = GlideApp.with(mActivity)
                .load(res)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        builder.into(icon);
    }

    private void                    setLogo(CircleImageView logo, int position, Port port) {
        int res;
        switch (position) {
            case 0:
                res = R.drawable.ic_play_arrow_black_24dp;
                break;
            default:
                res = R.mipmap.ic_action;
                break;
        }
        if (port.state == PortState.FILTERED || port.state == PortState.CLOSED_FILTERED)
            logo.setBorderColor(ContextCompat.getColor(mActivity, R.color.filtered_color));
        else
            logo.setBorderColor(ContextCompat.getColor(mActivity,
                    port.isOpen() ? R.color.online_color : R.color.offline_color));
        logo.setBorderWidth(3);
        GlideRequest builder = GlideApp.with(mActivity)
                .load(res)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        builder.into(logo);
    }

    private View.OnClickListener    onCardClicked(final int position) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                switch (position) {
                    case 0:
                        //GOTO TO FRAGMENT HTTP
                        break;
                    default:
                        //GOTO TO OTHER FRAGMENT
                        break;
                }
            }
        };
    }

    public int                      getItemCount() {
        return mPorts.portArrayList().size();// VulnScanner.TypeScanner.values().length;
    }

}
