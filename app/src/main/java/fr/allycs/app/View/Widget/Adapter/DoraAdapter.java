package fr.allycs.app.View.Widget.Adapter;

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

import fr.allycs.app.Controller.Core.Core.RootProcess;
import fr.allycs.app.Model.Unix.DoraProcess;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Holder.DoraHolder;


public class                    DoraAdapter extends RecyclerView.Adapter<DoraHolder> {
    private String              TAG = "DoraAdapter";
    private Activity            mActivity;
    private List<DoraProcess>   mHosts;
    private List<Boolean>       mListHostRunning;
    private boolean             mIsRunning;

    public                      DoraAdapter(Activity mActivity, List<DoraProcess> hostsSelected) {
        this.mHosts = hostsSelected;
        this.mActivity = mActivity;
        this.mListHostRunning = new ArrayList<>(Arrays.asList(new Boolean[hostsSelected.size()]));
        Collections.fill(mListHostRunning, Boolean.FALSE);
    }

    public void                 setIsRunning(boolean mIsRunning) {
        this.mIsRunning = mIsRunning;
        Collections.fill(mListHostRunning, mIsRunning);
        notifyDataSetChanged();
    }

    private void                playProcess(int position, DoraHolder holder) {
        mHosts.get(position).exec();
        Log.d(TAG, "Play process:" + mHosts.get(position).mhost.ip);
        mListHostRunning.set(position, true);
        holder.fab.setImageDrawable(mActivity.getDrawable(R.drawable.ic_pause));
    }

    private void                killProcess(int position, DoraHolder holder) {
        Log.d(TAG, "kill process:" + mHosts.get(position).mhost.ip + " mPid:" + mHosts.get(position).mPid);
        RootProcess.kill(mHosts.get(position).mPid);
        mListHostRunning.set(position, false);
        holder.fab.setImageDrawable(mActivity.getDrawable(R.drawable.ic_media_play));
    }

    public DoraHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DoraHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dora, parent, false));
    }

    public void                 onBindViewHolder(DoraHolder holder, int position) {
        final DoraProcess host = mHosts.get(position);
        holder.diagnose.setText(new String(new char[host.getVisu()]).replace("\0", "#"));
        String IP = host.mhost.ip + (host.mhost.getName().contains("(-)") ? "" : (" - " +
                host.mhost.getName().replace("(", "[").replace(")", "]")));
        holder.IP.setText(IP);
        if (host.mhost.getName().contains(host.mhost.ip)) {
            holder.ipHostname.setVisibility(View.GONE);
        } else {
            holder.ipHostname.setText(host.mhost.name);
        }
        holder.uptime.setText("Uptime:    " + host.getmUptime());
        holder.stat.setText("sent: " + host.sent + " / rcv: " + host.rcv);
        int pourc = host.getPourcentage();
        if (pourc == 0) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(mActivity, R.color.material_light_white));
        } else if (pourc <= 60) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(mActivity, R.color.material_red_500));
        } else if (pourc <= 90) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(mActivity, R.color.material_orange_500));
        } else {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(mActivity, R.color.material_green_600));
        }
        holder.diagnosPourcentage.setText(pourc + "%");
        holder.fab.setImageDrawable(mActivity.getDrawable(host.mIsRunning ? R.drawable.ic_pause : R.drawable.ic_media_play));
        holder.fab.setOnClickListener(onClickFab(position, holder));
        holder.stopFab.setOnClickListener(onClickStop(position, holder));
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
                if (mIsRunning) killProcess(position, holder);
                else playProcess(position, holder);
            }
        };
    }

    public int                  getItemCount() {
        return mHosts.size();
    }

}
