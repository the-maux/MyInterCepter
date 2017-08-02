package su.sniff.cepter.View.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import su.sniff.cepter.R;

/**
 * Created by maxim on 10/07/2017.
 */

public class                DNSAdapter extends ArrayAdapter<String> {
    private Context         context;
    private List<String>    DnsSpoof;

    public                  DNSAdapter(Context context, List<String> listDnsSpoof) {
        super(context, R.layout.adapter_dns, R.id.monitor, listDnsSpoof);
        this.context = context;
        this.DnsSpoof = listDnsSpoof;
    }

    @NonNull
    @Override
    public View             getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.adapter_dns, parent, false);
        ((TextView) convertView.findViewById(R.id.monitor)).setText(DnsSpoof.get(position));
        return convertView;
    }
}
