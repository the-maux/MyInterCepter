package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.dao.app.R;

public class                CryptCheckHolder extends RecyclerView.ViewHolder {
    public View             rootView;
    public ConstraintLayout relativeLayout;
    public TextView         name;
    public TextView         KeyExchange_Type, KeyExchange_KS;
    public TextView         Authentification_Type, Authentification_KS;
    public TextView         Encryption_Type, Encryption_SZ, Encryption_BZ, Encryption_Mode;
    public TextView         MAC_Type, MAC_KS;
    public TextView         PFS;

    public                  CryptCheckHolder(View v) {
        super(v);
        rootView = v;
        relativeLayout =  rootView.findViewById(R.id.rootViewItemCryptcheck);
        name = rootView.findViewById(R.id.Name);
        KeyExchange_Type = rootView.findViewById(R.id.TypeKE);
        KeyExchange_KS = rootView.findViewById(R.id.KeySize);
        Authentification_Type = rootView.findViewById(R.id.typeKEAuthentification);
        Authentification_KS = rootView.findViewById(R.id.keySizeAuthentification);

        Encryption_Type = rootView.findViewById(R.id.typeKEEncryption);
        Encryption_SZ = rootView.findViewById(R.id.KeySizeEncryption);
        Encryption_BZ = rootView.findViewById(R.id.BlockSizeEncryption);
        Encryption_Mode = rootView.findViewById(R.id.ModeEncryption);
        MAC_Type = rootView.findViewById(R.id.typeKEMAC);
        MAC_KS = rootView.findViewById(R.id.keySizeMAC);
        PFS = rootView.findViewById(R.id.PFS);
    }
}
