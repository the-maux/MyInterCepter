package su.sniff.cepter.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.util.ArrayList;

public class ProtocolAdapter extends ArrayAdapter<String> {
    ArrayList<String> f1z;

    public ProtocolAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.f1z = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.raw_list, null);
        }
        TextView tt = (TextView) convertView.findViewById(R.id.label);
        tt.setText((CharSequence) this.f1z.get(position));
        tt.setTextSize(2, (float) globalVariable.raw_textsize);
        tt.setBackgroundResource(R.color.ftp);
        if (((String) this.f1z.get(position)).indexOf("ARP") > 0) {
            tt.setBackgroundResource(R.color.arp);
        }
        if (((String) this.f1z.get(position)).indexOf("ICMP") > 0) {
            tt.setBackgroundResource(R.color.icmp);
        }
        if (((String) this.f1z.get(position)).indexOf("SMB") > 0) {
            tt.setBackgroundResource(R.color.smb);
        }
        if (((String) this.f1z.get(position)).indexOf("HTTP") > 0 || ((String) this.f1z.get(position)).indexOf("HTTPS") > 0 || ((String) this.f1z.get(position)).indexOf("AOL") > 0) {
            tt.setBackgroundResource(R.color.http);
        }
        if (((String) this.f1z.get(position)).indexOf("DNS") > 0 || ((String) this.f1z.get(position)).indexOf("UDP") > 0 || ((String) this.f1z.get(position)).indexOf("SNMP") > 0 || ((String) this.f1z.get(position)).indexOf("DHCP") > 0 || ((String) this.f1z.get(position)).indexOf("NTP") > 0 || ((String) this.f1z.get(position)).indexOf("RADIUS") > 0) {
            tt.setBackgroundResource(R.color.udp);
        }
        if (((String) this.f1z.get(position)).indexOf("FTP") > 0 || ((String) this.f1z.get(position)).indexOf("POP3") > 0 || ((String) this.f1z.get(position)).indexOf("SMTP") > 0 || ((String) this.f1z.get(position)).indexOf("IMAP") > 0 || ((String) this.f1z.get(position)).indexOf("IMAPS") > 0 || ((String) this.f1z.get(position)).indexOf("POP3S") > 0 || ((String) this.f1z.get(position)).indexOf("TELNET") > 0 || ((String) this.f1z.get(position)).indexOf("SSH") > 0 || ((String) this.f1z.get(position)).indexOf("SMTPS") > 0 || ((String) this.f1z.get(position)).indexOf("TNS") > 0 || ((String) this.f1z.get(position)).indexOf("MYSQL") > 0 || ((String) this.f1z.get(position)).indexOf("TCP") > 0) {
            tt.setBackgroundResource(R.color.ftp);
        }
        tt.setTypeface(Typeface.MONOSPACE);
        return convertView;
    }
}
