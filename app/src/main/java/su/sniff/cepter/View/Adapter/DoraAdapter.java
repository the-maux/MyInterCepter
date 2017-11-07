package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;
import su.sniff.cepter.Model.Unix.DoraProcess;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.DoraHolder;


public class                    DoraAdapter extends RecyclerView.Adapter<DoraHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<DoraProcess>   hosts;
    private List<Boolean>       areRunning;
    private boolean             running;


    public                      DoraAdapter(Activity activity, List<DoraProcess> hostsSelected) {
        this.hosts = hostsSelected;
        this.activity = activity;
        this.areRunning = new ArrayList<>(Arrays.asList(new Boolean[hostsSelected.size()]));
        Collections.fill(areRunning, Boolean.FALSE);
    }

    @Override
    public DoraHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DoraHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dora, parent, false));
    }

    @Override
    public void                 onBindViewHolder(DoraHolder holder, int position) {
        final DoraProcess host = hosts.get(position);
        holder.diagnose.setText(new String(new char[host.getVisu()]).replace("\0", "#"));
        String IP = host.mhost.getIp() + (host.mhost.getName().contains("(-)") ? "" : (" - " + host.mhost.getName().replace("(", "[").replace(")", "]")));
        holder.IP.setText(IP);
        holder.uptime.setText("Uptime:    " + host.getmUptime());
        holder.stat.setText("sent: " + host.sent + " / rcv: " + host.rcv);
        int pourc = host.getPourcentage();
        if (pourc == 0) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(activity, R.color.material_light_white));
        } else if (pourc <= 60) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(activity, R.color.material_red_500));
        } else if (pourc <= 90) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(activity, R.color.material_deep_orange_500));
        } else {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(activity, R.color.material_green_600));
        }
        holder.diagnosPourcentage.setText(pourc + "%");
        holder.fab.setImageDrawable(activity.getDrawable(host.mIsRunning ? R.drawable.ic_pause : R.drawable.ic_media_play));
        holder.fab.setOnClickListener(onClickFab(position, holder));
        holder.stopFab.setOnClickListener(onClickStop(position, holder));
    }

    public void                setRunning(boolean running) {
        this.running = running;
        Collections.fill(areRunning, running);
        notifyDataSetChanged();
    }

    private View.OnClickListener onClickStop(final int position, final DoraHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killProcess(position, holder);
            }
        };
    }

    private View.OnClickListener onClickFab(final int position, final DoraHolder holder) {
       return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) killProcess(position, holder);
                else playProcess(position, holder);
            }
        };
    }

    private void                playProcess(int position, DoraHolder holder) {
        hosts.get(position).exec();
        Log.d(TAG, "Play process:" + hosts.get(position).mhost.getIp());
        areRunning.set(position, true);
        holder.fab.setImageDrawable(activity.getDrawable(R.drawable.ic_pause));
    }

    private void                killProcess(int position, DoraHolder holder) {
        Log.d(TAG, "kill process:" + hosts.get(position).mhost.getIp() + " mPid:" + hosts.get(position).mPid);
        RootProcess.kill(hosts.get(position).mPid);
        areRunning.set(position, false);
        holder.fab.setImageDrawable(activity.getDrawable(R.drawable.ic_media_play));
    }

    @Override
    public int                  getItemCount() {
        return hosts.size();
    }

}
