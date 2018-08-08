package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.dao.app.Model.Config.Cryptcheck.Ciphers;
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
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mActivity.getResources().getDimension(R.dimen.text_size_3));
            holder.rootViewItemCryptcheck.setBackgroundColor(mActivity.getResources().getColor(R.color.primary_white));
            holder.keyExchange.setText("");
            holder.Authentification.setText("");
            holder.Encryption_Type.setText("");
            holder.Encryption_KZ.setText("");
            holder.Encryption_BZ.setText("");
            holder.Encryption_Mode.setText("");
            holder.MAC_Type.setText("");
            holder.MAC_KS.setText("");
        } else {
            holder.name.setText(cipher.name.toUpperCase());
            holder.keyExchange.setText(cipher.key_echange.toUpperCase());
            holder.Authentification.setText(cipher.authentification.toUpperCase());
           if (cipher.encryption != null) {
                holder.Encryption_Type.setText(cipher.encryption.get(0).toString().toUpperCase());
                holder.Encryption_KZ.setText(cipher.encryption.get(1).toString().toUpperCase());
                holder.Encryption_BZ.setText(cipher.encryption.get(2).toString().toUpperCase());
                holder.Encryption_Mode.setText(cipher.encryption.get(3).toString().toUpperCase());
            }
            if (cipher.hmac != null) {
                Log.i(TAG, "");
                holder.MAC_Type.setText(cipher.hmac.name.toUpperCase());
                String size = (cipher.hmac.size + "").toUpperCase();
                holder.MAC_KS.setText(size);
            } else {
                Log.e(TAG, "no hmac");
            }
            holder.rootViewItemCryptcheck.setBackgroundColor(mActivity.getResources().getColor(R.color.material_blue_grey_100));
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
