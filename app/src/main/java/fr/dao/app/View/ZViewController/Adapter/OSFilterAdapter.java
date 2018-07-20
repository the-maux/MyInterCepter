package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;

import fr.dao.app.Model.Unix.Os;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.Holder.HostSelectionHolder;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;

public class                    OSFilterAdapter extends RecyclerView.Adapter<HostSelectionHolder> {
    private String              TAG = "OSFilterAdapter";
    private Context             mCtx;
    private ArrayList<Integer>  mOsList, mOsListSelected = new ArrayList<>();

    public                      OSFilterAdapter(Activity activity, ArrayList<Integer> osList) {
        this.mOsList = osList;
        this.mCtx = activity;
    }

    public HostSelectionHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostSelectionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_host_checkbox, parent, false));
    }

    public void                 onBindViewHolder(HostSelectionHolder holder, int position) {
        final int os = mOsList.get(position);
        holder.nameOS.setText(Os.toString(os).replace("_", "/"));
        MyGlideLoader.setOsIcon(os, holder.imageOS);
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

    public int                  getItemCount() {
        return mOsList.size();
    }

    public ArrayList<Integer>        getSelected() {
        return (mOsListSelected.isEmpty()) ? mOsList : mOsListSelected;
    }

}