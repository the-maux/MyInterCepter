package fr.allycs.app.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.Misc.GlideApp;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.HostSelectionHolder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class                    OSAdapter extends RecyclerView.Adapter<HostSelectionHolder> {
    private String              TAG = this.getClass().getName();
    private Context             mCtx;
    private List<String>        mOsList, mOsListSelected;

    public                      OSAdapter(Activity activity, ArrayList<String> osList, List<String> osListSelected) {
        this.mOsList = osList;
        this.mCtx = activity;
        this.mOsListSelected = osListSelected;
    }
    @Override
    public HostSelectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostSelectionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host_checkbox, parent, false));
    }

    @Override public void       onBindViewHolder(HostSelectionHolder holder, int position) {
        final String os = mOsList.get(position);
        holder.nameOS.setText(os.replace("_", "/"));
        setOsIcon(mCtx, os, holder.imageOS);
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mOsListSelected.add(os);
                else
                    mOsListSelected.remove(os);
            }
        });
    }

    private void setOsIcon(Context mCtx, String os, CircleImageView imageOS) {
        int                 ImageRessource;

        if (os == null) {
            ImageRessource = R.drawable.monitor;
        } else if (os.contains("Windows")) {
            ImageRessource = R.drawable.winicon;
        } else if (os.contains("Apple")) {
            ImageRessource = R.drawable.ios;
        } else if (os.contains("Android") || os.contains("Mobile") || os.contains("Samsung")) {
            ImageRessource = R.drawable.android;
        } else if (os.contains("Cisco")) {
            ImageRessource = R.drawable.cisco;
        } else if (os.contains("Raspberry")) {
            ImageRessource = R.drawable.rasp;
        } else if (os.contains("QUANTA")) {
            ImageRessource = R.drawable.quanta;
        } else if (os.contains("Bluebird")) {
            ImageRessource = R.drawable.bluebird;
        } else if (os.contains("Ios")) {
            ImageRessource = R.drawable.ios;
        } else if (!(!os.contains("Unix") && !os.contains("Linux") && !os.contains("BSD"))) {
            ImageRessource = R.drawable.linuxicon;
        } else
            ImageRessource = R.drawable.monitor;
        GlideApp.with(mCtx)
                .load(ImageRessource)
                .apply(new RequestOptions()
                        .fitCenter()
                        .override(100, 100))
                .placeholder(R.drawable.monitor)
                .into(imageOS);

    }


    @Override public int         getItemCount() {
        return mOsList.size();
    }
}