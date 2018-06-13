package fr.dao.app.View.ZViewController.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Net.HttpTrame;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.SettingHolder;


public class                    HTTProxyAdapter extends RecyclerView.Adapter<SettingHolder> {
    private String              TAG = "DnsSpoofConfAdapter";
    private MyActivity          mActivity;
    private List<HttpTrame>     httpTrames = new ArrayList<>();
    private Singleton           mSingleton = Singleton.getInstance();

    public                      HTTProxyAdapter(MyActivity activity) {
        this.mActivity = activity;
    }

    public SettingHolder        onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SettingHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settings, parent, false));
    }

    public void                 onBindViewHolder(SettingHolder holder, int position) {
        HttpTrame trameHttp = httpTrames.get(position);
        holder.title.setText(" Trame " + trameHttp.offsett);
        holder.subtitle.setText(trameHttp.getDump());
    }

    public int                  getItemCount() {
        return httpTrames.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);
        mHosts.reset();
        for (Host title : mOriginalList) {
            if (title.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(title);
        }
        notifyDataSetChanged();*/
    }

    public void             addTrameOnAdapter(HttpTrame poppedTrame) {
        httpTrames.add(poppedTrame);
        notifyItemInserted(0);
    }
}
