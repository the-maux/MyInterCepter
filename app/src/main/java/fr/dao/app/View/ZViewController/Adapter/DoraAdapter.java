package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.dao.app.Model.Unix.DoraProcess;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.Holder.DoraHolder;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;


public class                    DoraAdapter extends RecyclerView.Adapter<DoraHolder> {
    private String              TAG = "DoraAdapter";
    private Activity            mActivity;
    private List<DoraProcess>   mHosts;
    private List<Boolean>       mListHostRunning;
    private boolean             mIsRunning = false;

    public                      DoraAdapter(Activity mActivity, List<DoraProcess> hostsSelected) {
        this.mActivity = mActivity;
        this.mHosts = hostsSelected;
        this.mListHostRunning = new ArrayList<>(Arrays.asList(new Boolean[hostsSelected.size()]));
        Collections.fill(mListHostRunning, Boolean.FALSE);
    }

    public DoraHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DoraHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dora, parent, false));
    }


    public void                 onBindViewHolder(DoraHolder holder, int position) {
        final DoraProcess host = mHosts.get(position);
        holder.diagnose.setText(new String(new char[host.getVisu()]).replace("\0", "#"));
        holder.IP.setText(host.mhost.name.contains("Unknown") ? host.mhost.ip : host.mhost.name);
        holder.uptime.setText(host.getmUptime());
        String t = "\u2191 " + host.sent + "  /  " + "\u2193 " + host.rcv;
        holder.stat.setText(t);
        int pourc = host.getPourcentage();
        if (pourc == 0) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(mActivity, R.color.material_light_white));
        } else if (pourc <= 60) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(mActivity, R.color.stop_color));
        } else if (pourc <= 90) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(mActivity, R.color.material_orange_500));
        } else {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(mActivity, R.color.start_color));
        }
        holder.diagnosPourcentage.setText(pourc + "%");
//        holder.fab.setImageDrawable(mActivity.getDrawable(host.mIsRunning ? R.drawable.ic_pause : R.drawable.ic_media_play));
//        holder.fab.setOnClickListener(onClickFab(position, holder));
        //TODO: replace with status bar in card view
        //holder.stopFab.setOnClickListener(onClickStop(position, holder));
        MyGlideLoader.setOsIcon(host.mhost, holder.hostIcon);
    }

    public void                 setIsRunning(boolean mIsRunning) {
        this.mIsRunning = mIsRunning;
        Collections.fill(mListHostRunning, mIsRunning);
        notifyDataSetChanged();
    }

    /*private void                playProcess(int position, DoraHolder holder) {
        mHosts.get(position).exec();
        Log.d(TAG, "Play process:" + mHosts.get(position).mhost.subtitle);
        mListHostRunning.set(position, true);
        //holder.fab.setImageDrawable(mActivity.getDrawable(R.drawable.wireshark));
    }

    private void                killProcess(int position, DoraHolder holder) {
        Log.d(TAG, "kill process:" + mHosts.get(position).mhost.subtitle + " mPid:" + mHosts.get(position).mPid);
        RootProcess.kill(mHosts.get(position).mPid);
        mListHostRunning.set(position, false);
        //holder.fab.setImageDrawable(mActivity.getDrawable(R.drawable.ic_media_play));
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
    }*/

    public int                  getItemCount() {
        return mHosts.size();
    }

    public void                 updateDoraListHost(List<DoraProcess> doraProcesses) {
        if (!mIsRunning) {
            Log.i(TAG, "Dora update list, cause it's not running");
            this.mHosts = doraProcesses;
            this.mListHostRunning = new ArrayList<>(Arrays.asList(new Boolean[doraProcesses.size()]));
            Collections.fill(mListHostRunning, Boolean.FALSE);
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    notifyDataSetChanged();
                }
            });
        } else {
            Log.e(TAG, "Dora can't' update list, cause it's running");
        }
    }
}
