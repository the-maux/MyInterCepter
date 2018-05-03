package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.SniffSession;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.Holder.SniffSessionHolder;
import fr.dao.app.View.ZViewController.Dialog.QuestionDialog;

public class                    SniffSessionAdapter extends RecyclerView.Adapter<SniffSessionHolder> {
    private String              TAG = "SniffSessionAdapter";
    private Activity            mActivity;
    private List<SniffSession>  mSniffsession;

    public                      SniffSessionAdapter(Activity activity, List<SniffSession> sessions) {
        this.mActivity = activity;
        this.mSniffsession = sessions;
    }

    public SniffSessionHolder   onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SniffSessionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sniffsession, parent, false));
    }

    public void                 onBindViewHolder(SniffSessionHolder holder, int position) {
        final SniffSession sniffSession = mSniffsession.get(position);
        holder.title.setText(sniffSession.session.Ssid);
        String subtitile = "Scanned " + sniffSession.session.nbrScanned + " times";
        List<Pcap> pcaps = sniffSession.listPcapRecorded();
        List<Host> devices = sniffSession.listDevices();
        int nbrDnsIntercepted = (sniffSession.logDnsSpoofed() == null) ? sniffSession.logDnsSpoofed().size() : 0 ;
        String description =
                "Recorded the " + new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(sniffSession.date) +
                ", sniffant " + devices.size() + " devices " +
                "spoofant le dns avec " + nbrDnsIntercepted +
                " interception.\n" +
                ((pcaps != null && !pcaps.isEmpty()) ?
                    "Sauvegardant " + pcaps.size() + " pcap " : "");
        holder.description.setText(description);
        if (sniffSession.listPcapRecorded() != null && !sniffSession.listPcapRecorded().isEmpty())
            holder.shareButton.setOnClickListener(onShareSession(sniffSession));
        else
            holder.shareButton.setVisibility(View.GONE);
        holder.ACTION1.setOnClickListener(onClickDetail(sniffSession));
        holder.ACTION2.setOnClickListener(onClickDelete(sniffSession));
    }

    private View.OnClickListener    onShareSession(final SniffSession sniffSession) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Utils.vibrateDevice(mActivity, 100);
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/vnd.tcpdump.pcap");
                final File photoFile = sniffSession.listPcapRecorded().get(0).getFile();
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                Intent intent = Intent.createChooser(shareIntent,
                        "Send .pcap saved the " + sniffSession.listPcapRecorded().get(0).getDate());
                mActivity.startActivity(intent);
            }
        };
    }

    private View.OnClickListener    onClickDetail(SniffSession sniffSession) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrateDevice(mActivity, 100);
                new QuestionDialog(mActivity)
                        .setTitle("Non implementé")
                        .setText("")
                        .onPositiveButton("Je vais le faire", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.vibrateDevice(mActivity, 100);

                            }
                        })
                        .show();
            }
        };
    }

    private View.OnClickListener    onClickDelete(final SniffSession session) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QuestionDialog(mActivity)
                        .setTitle("Supprimer le NetworkInformation ?")
                        .setText("Cette action est irreversible, etes vous sur d\'etre certains de vouloir supprimer cette record.")
                        .onPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                session.delete();
                                mSniffsession.remove(session);
                                notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        };
    }

    public int                  getItemCount() {
        return mSniffsession.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);
        mHosts.clear();
        for (Host title : mOriginalList) {
            if (title.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(title);
        }
        notifyDataSetChanged();*/
    }


}
