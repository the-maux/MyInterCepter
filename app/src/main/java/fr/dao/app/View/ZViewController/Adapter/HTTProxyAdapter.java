package fr.dao.app.View.ZViewController.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Net.HttpTrame;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.SettingHolder;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;


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
        holder.title.setText(trameHttp.host);
        holder.subtitle.setText(trameHttp.request);
        holder.switch_sw.setVisibility(View.GONE);
        holder.rightLogo.setVisibility(View.VISIBLE);
        initUserAgent(trameHttp.userAgent.toLowerCase(), holder.rightLogo);

    }

    private void                initUserAgent(String userAgent, ImageView rightLogo) {
        int res;
        if (userAgent.contains("firefox"))
            res = R.drawable.firefox;
        else if (userAgent.contains("chrome"))
            res = R.drawable.chrome;
        else if (userAgent.contains("chromium"))
            res = R.drawable.chromium;
        else if (userAgent.contains("opera"))
            res = R.drawable.opera;
        else if (userAgent.contains("edge") || userAgent.contains("microsoft"))
            res = R.drawable.edge;
        else
            res = R.mipmap.ic_secure2;
        MyGlideLoader.loadDrawableInImageView(mActivity, res, rightLogo, true);
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
        mActivity.setToolbarTitle(null, httpTrames.size() + " Request");
        notifyItemInserted(0);
    }
}
