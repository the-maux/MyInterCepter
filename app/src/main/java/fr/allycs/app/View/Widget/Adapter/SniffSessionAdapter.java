package fr.allycs.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Misc.MyFragment;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.SniffSession;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Holder.SniffSessionHolder;

public class SniffSessionAdapter extends RecyclerView.Adapter<SniffSessionHolder> {
    private String              TAG = this.getClass().getName();
    private MyFragment          mFragment;
    private List<SniffSession>  mSniffsession;
    private Singleton           mSingleton = Singleton.getInstance();

    public SniffSessionAdapter(MyFragment fragment, List<SniffSession> sessions) {
        this.mFragment = fragment;
        this.mSniffsession = sessions;
    }

    public SniffSessionHolder   onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SniffSessionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sniffsession, parent, false));
    }

    public void                 onBindViewHolder(SniffSessionHolder holder, int position) {
        final SniffSession sniffSession = mSniffsession.get(position);
        holder.title.setText();
        holder.subtitle.setText();
        List<Host> devices = sniffSession.listDevices();
        int nbrDnsIntercepted = (sniffSession.logDnsSpoofed() == null) ? sniffSession.logDnsSpoofed().size() : 0 ;
        String description = "Recorded the " + new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(sniffSession.date) +
                ", sniffant " + devices.size() + " devices " +
                "spoofant le dns avec " + nbrDnsIntercepted +
                " interception.\nDump pcap sous le fichier : GROUPE_ADEO_19_01_18h51.pcap";
        holder.description.setText(description);
    }

    public int                  getItemCount() {
        return mSniffsession.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);
        mHosts.clear();
        for (Host domain : mOriginalList) {
            if (domain.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(domain);
        }
        notifyDataSetChanged();*/
    }
}
