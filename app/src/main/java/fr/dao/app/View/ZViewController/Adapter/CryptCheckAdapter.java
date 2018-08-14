package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;

import fr.dao.app.Model.Config.Cryptcheck.Ciphers;
import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.Holder.CryptCheckHolder;

public class                    CryptCheckAdapter extends RecyclerView.Adapter<CryptCheckHolder> {
    private String              TAG = "CryptCheckAdapter";
    private ArrayList<Ciphers>  protos, originalProto;
    private Activity            mActivity;
    private boolean             isTLS10 = true, isTLS11 = true, isTLS12 = true, isTLS13 = true;
    
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
            holder.name.setGravity(Gravity.LEFT);
            holder.name.setPadding(20, 8, 0, 0);
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mActivity.getResources().getDimension(R.dimen.text_size_4));
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
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mActivity.getResources().getDimension(R.dimen.text_size_2));
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

    public void                 putOnListOfTrame(final CryptCheckScan scan) {
        protos = scan.getProtos(isTLS10, isTLS11, isTLS12, isTLS13);
        notifyDataSetChanged();
    }

    public void                 reset() {
        if (protos != null)
            protos.clear();
        notifyDataSetChanged();
    }

    public void                 clear() {
        if (protos != null) {
            this.protos.clear();
            notifyDataSetChanged();
        }
    }

    public void                 sort(CompoundButton buttonView, final CryptCheckScan scan) {
        switch (buttonView.getId()) {
            case R.id.radioButtonTLS10:
                isTLS10 = !isTLS10;
                break;
            case R.id.radioButtonTLS2:
                isTLS11 = !isTLS11;
                break;
            case R.id.radioButtonTLS3:
                isTLS12 = !isTLS12;
                break;
        }
        protos = scan.getProtos(isTLS10, isTLS11, isTLS12, isTLS13);
        notifyDataSetChanged();
    }
}
