package su.sniff.cepter.View.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.util.ArrayList;

public class                ProtocolAdapter extends ArrayAdapter<String> {
    private ArrayList<String> f1z;

    public                  ProtocolAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.f1z = objects;
    }

    @NonNull
    public View             getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.adapter_raw, null);
        }
        TextView tt = (TextView) convertView.findViewById(R.id.label);
        tt.setText( f1z.get(position));
        tt.setTextSize(2, (float) globalVariable.raw_textsize);
        tt.setBackgroundResource(R.color.ftp);
        if ((f1z.get(position)).indexOf("ARP") > 0) {
            tt.setBackgroundResource(R.color.arp);
        }
        if ((f1z.get(position)).indexOf("ICMP") > 0) {
            tt.setBackgroundResource(R.color.icmp);
        }
        if ((f1z.get(position)).indexOf("SMB") > 0) {
            tt.setBackgroundResource(R.color.smb);
        }
        if ((f1z.get(position)).indexOf("HTTP") > 0      ||
                (f1z.get(position)).indexOf("HTTPS") > 0 ||
                (f1z.get(position)).indexOf("AOL") > 0) {
            tt.setBackgroundResource(R.color.http);
        }
        if ((f1z.get(position)).indexOf("DNS") > 0        ||
                (f1z.get(position)).indexOf("UDP") > 0    ||
                (f1z.get(position)).indexOf("SNMP") > 0   ||
                (f1z.get(position)).indexOf("DHCP") > 0   ||
                (f1z.get(position)).indexOf("NTP") > 0    ||
                (f1z.get(position)).indexOf("RADIUS") > 0) {
            tt.setBackgroundResource(R.color.udp);
        }
        if ((f1z.get(position)).indexOf("FTP") > 0        ||
                (f1z.get(position)).indexOf("POP3") > 0   ||
                (f1z.get(position)).indexOf("SMTP") > 0   ||
                (f1z.get(position)).indexOf("IMAP") > 0   ||
                (f1z.get(position)).indexOf("IMAPS") > 0  ||
                (f1z.get(position)).indexOf("POP3S") > 0  ||
                (f1z.get(position)).indexOf("TELNET") > 0 ||
                (f1z.get(position)).indexOf("SSH") > 0    ||
                (f1z.get(position)).indexOf("SMTPS") > 0  ||
                (f1z.get(position)).indexOf("TNS") > 0    ||
                (f1z.get(position)).indexOf("MYSQL") > 0  ||
                (f1z.get(position)).indexOf("TCP") > 0) {
            tt.setBackgroundResource(R.color.ftp);
        }
        tt.setTypeface(Typeface.MONOSPACE);
        return convertView;
    }
}
