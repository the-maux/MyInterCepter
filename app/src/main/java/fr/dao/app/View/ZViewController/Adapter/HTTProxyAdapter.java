package fr.dao.app.View.ZViewController.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Tcpdump.Proxy;
import fr.dao.app.Model.Net.HttpTrame;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.SettingHolder;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;


public class                    HTTProxyAdapter extends RecyclerView.Adapter<SettingHolder> {
    private String              TAG = "DnsSpoofConfAdapter";
    private MyActivity          mActivity;
    private ArrayList<HttpTrame> httpTrames;
    private mode                typeOfFilter;
    public enum                 mode {
        HTTP, SECRET, RESSOURCE
    }

    public                      HTTProxyAdapter(MyActivity activity, mode filteredType) {
        this.mActivity = activity;
        this.typeOfFilter = filteredType;
        if (Proxy.isRunning()) {
            httpTrames = Proxy.getProxy().getActualTrameStack();
            filterTheStack();
        } else
            httpTrames = new ArrayList<>();
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
        if (this.typeOfFilter == mode.HTTP)
            initUserAgent(trameHttp.userAgent.toLowerCase(), holder.rightLogo);
        else if (this.typeOfFilter == mode.RESSOURCE)
            initPicRessource(trameHttp.request, holder.rightLogo);
    }

    private void                initPicRessource(String s, ImageView rightLogo) {
        int res;
        if (s.contains(".js"))
            res = R.drawable.javascript;
        else if (s.contains(".css"))
            res = R.drawable.css;
        else
            return;
        MyGlideLoader.loadDrawableInImageView(mActivity, res, rightLogo, true);
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

    public void                 addTrameOnAdapter(HttpTrame poppedTrame) {
        if (trameIsRight(poppedTrame))
            httpTrames.add(poppedTrame);
        notifyItemInserted(0);
    }

    private void                filterTheStack() {
        ArrayList<HttpTrame> httpTrames = new ArrayList<>();
        for (HttpTrame trame : this.httpTrames) {
            if (trameIsRight(trame))
                httpTrames.add(trame);
        }
        this.httpTrames.clear();
        this.httpTrames = httpTrames;
    }

    private boolean             trameIsRight(HttpTrame trame) {
        return (trame.type == HttpTrame.typeOfRequest.GET || trame.type == HttpTrame.typeOfRequest.POST ||
                trame.type == HttpTrame.typeOfRequest.DELETE || trame.type == HttpTrame.typeOfRequest.PUT) &&
                    typeOfFilter == mode.HTTP ||
                trame.type == HttpTrame.typeOfRequest.RESSOURCE &&
                typeOfFilter == mode.RESSOURCE || trame.importante;
    }


}
