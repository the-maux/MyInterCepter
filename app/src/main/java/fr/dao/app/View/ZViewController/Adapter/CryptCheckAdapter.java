package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.dao.app.Model.Config.Cryptcheck.Ciphers;
import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.Holder.CryptCheckHolder;

public class                    CryptCheckAdapter extends RecyclerView.Adapter<CryptCheckHolder> {
    private String              TAG = "CryptCheckAdapter";
    private ArrayList<Ciphers>  protos;
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
        Ciphers cipher = protos.get(position);
        if (cipher.isTitle) {
            holder.name.setText(cipher.name);
            holder.KeyExchange_Type.setText("");
            holder.Authentification_Type.setText("");
            holder.Encryption_Type.setText("");
            holder.Encryption_KZ.setText("");
            holder.Encryption_BZ.setText("");
            holder.Encryption_Mode.setText("");
            holder.MAC_Type.setText("");
            holder.MAC_KS.setText("");
        } else {
            Log.i(TAG, "");
            holder.name.setText(cipher.name);
            Log.i(TAG, "");
            holder.KeyExchange_Type.setText(cipher.key_echange);
//            holder.KeyExchange_KS.setText(cypherProto.KeyExchange[1]);
            Log.i(TAG, "");
            holder.Authentification_Type.setText(cipher.authentification);
//            holder.Authentification_KS.setText();
            Log.i(TAG, "");
            if (cipher.encryption != null) {
                holder.Encryption_Type.setText(cipher.encryption.get(0).toString());
                holder.Encryption_KZ.setText(cipher.encryption.get(1).toString());
                holder.Encryption_BZ.setText(cipher.encryption.get(2).toString());
                holder.Encryption_Mode.setText(cipher.encryption.get(3).toString());
            }
            if (cipher.hmac != null) {
                Log.i(TAG, "");
                holder.MAC_Type.setText(cipher.hmac.name);
                holder.MAC_KS.setText(cipher.hmac.size + "");
            } else {
                Log.e(TAG, "no hmac");
            }
       }
    }

    public int                  getItemCount() {
        return protos == null ? 0 : protos.size();
    }

    public void                 putOnListOfTrame(final ArrayList<Ciphers> protoArrayList) {
        this.protos = protoArrayList;
        notifyDataSetChanged();
    }

    public void                 reset() {
        if (protos != null)
            protos.clear();
        notifyDataSetChanged();
    }

}
