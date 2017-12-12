package fr.allycs.app.Model.Unix;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.ConsoleLogHolder;

@Table(name = "DnsLog", id = "_id")
public class                            DNSLog {
    private String                      TAG = "DNSLog";
    public enum Type { Query, Forward, Reply, Other}
    public Type                         currentType;
    @Column(name = "Domain")
    public String                       domain;
    public String                       data;
    public ArrayList<DNSLog>            logs = new ArrayList<>();
    public RecyclerView.Adapter<ConsoleLogHolder> adapter = null;
    public int                          color;

    public                              DNSLog(String line) {
        buildLog(line);
        data = line;
        logs.add(this);
    }

    private void                        buildLog(String line) {
        String[] splitted = line.split(" ");
        data = line;
        switch (splitted[0]) {
            case "query[A]":
                color = R.color.material_green_600;
                currentType = Type.Query;
                break;
            case "forwarded":
                color = R.color.material_amber_700;
                currentType = Type.Forward;
                break;
            case "reply":
                color = R.color.material_cyan_700;
                currentType = Type.Reply;
                break;
            default:
                color = R.color.material_light_white;
                currentType = Type.Other;
                break;
        }
        domain = splitted[1];
    }

    public boolean                      isSameDomain(DNSLog dnsLog) {
        return dnsLog.domain.contains(domain);
    }
    public void                         setAdapter(RecyclerView.Adapter<ConsoleLogHolder> adapter) {
        this.adapter = adapter;
    }
    public void                         addLog(DNSLog dnsLog) {
        if (Singleton.getInstance().DebugMode)
            Log.d(TAG, "addLog:" + dnsLog.data + "] to [" + this.domain + "]");
        logs.add(dnsLog);
        this.currentType = dnsLog.currentType;
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}