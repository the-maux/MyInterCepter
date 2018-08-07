package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.dao.app.Model.Config.CryptCheckModel;
import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.Holder.CryptCheckHolder;
import fr.dao.app.View.ZViewController.Adapter.Holder.WiresharkHolder;

public class                    CryptCheckAdapter extends RecyclerView.Adapter<CryptCheckHolder> {
    private String              TAG = "CryptCheckAdapter";
    private ArrayList<CryptCheckModel.CypherProto> protoArrayList;
    private Activity            mActivity;

    public CryptCheckAdapter(Activity activity) {
        this.mActivity = activity;
    }

    @NonNull
    public CryptCheckHolder      onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CryptCheckHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cryptcheck, parent, false));
    }

    public void                 onBindViewHolder(CryptCheckHolder holder, int position) {
        CryptCheckModel.CypherProto cypherProto = protoArrayList.get(position);
        holder.name.setText(cypherProto.name);
        holder.KeyExchange_Type.setText(cypherProto.KeyExchange[0]);
//        setBackgroundColor(cypherProto.type, holder);
        holder.KeyExchange_KS.setText(cypherProto.KeyExchange[1]);
//        setBackgroundColor(cypherProto.type, holder);
        holder.Encryption_Type.setText(cypherProto.Encryption[0]);
//        setBackgroundColor(cypherProto.type, holder);
        holder.Encryption_SZ.setText(cypherProto.Encryption[1]);
//        setBackgroundColor(cypherProto.type, holder);
        holder.Encryption_BZ.setText(cypherProto.Encryption[2]);
//        setBackgroundColor(cypherProto.type, holder);
        holder.Encryption_Mode.setText(cypherProto.Encryption[3]);
//        setBackgroundColor(cypherProto.type, holder);
        holder.MAC_Type.setText(cypherProto.MAC[0]);
//        setBackgroundColor(cypherProto.type, holder);
        holder.MAC_KS.setText(cypherProto.MAC[1]);
//        setBackgroundColor(cypherProto.type, holder);
    }

    public int                  getItemCount() {
        return protoArrayList == null ? 0 : protoArrayList.size();
    }

    public void                 putOnListOfTrame(final ArrayList<CryptCheckModel.CypherProto> protoArrayList) {
        this.protoArrayList = protoArrayList;
        notifyDataSetChanged();
    }

    public void                 reset() {
        if (protoArrayList != null)
            protoArrayList.clear();
        notifyDataSetChanged();
    }

    public void                 putOnListOfTrame(CryptCheckScan scan) {

    }
}
