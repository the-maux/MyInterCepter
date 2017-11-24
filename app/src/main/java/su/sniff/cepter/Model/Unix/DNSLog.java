package su.sniff.cepter.Model.Unix;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.ConsoleLogHolder;

/**
 * Created by maxim on 09/10/2017.
 */

public class                            DNSLog {
    private String                      TAG = "DNSLog";
    public enum Type {
        Query,
        Forward,
        Reply,
        Other
    }
    public Type                         currentType;
    public String                       host;
    public String                       data;
    public ArrayList<DNSLog>            logs = new ArrayList<>();
    public RecyclerView.Adapter<ConsoleLogHolder> adapter = null;
    public int                          color;

    public                              DNSLog(String line) {
        //Log.d(TAG, "DNSLog(" + line + ")");
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
        host = splitted[1];
    }

    public boolean                      isSameDomain(DNSLog dnsLog) {
        return dnsLog.host.contains(host);
    }
    public void                         setAdapter(RecyclerView.Adapter<ConsoleLogHolder> adapter) {
        this.adapter = adapter;
    }
    public void                         addLog(DNSLog dnsLog) {
        Log.d(TAG, "addLog:" + dnsLog.data + "] to [" + this.host + "]");
        logs.add(dnsLog);
        this.currentType = dnsLog.currentType;
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}