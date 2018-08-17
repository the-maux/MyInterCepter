package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.dao.app.R;

public class                CryptCheckHolder extends RecyclerView.ViewHolder {
    public View             rootView, headerProto;
    public ConstraintLayout rootViewItemCryptcheck;
    public TextView         name;
    public TextView         keyExchange;
    public TextView         Authentification;
    public TextView         Encryption_Type, Encryption_KZ, Encryption_BZ, Encryption_Mode;
    public TextView         MAC_Type, MAC_KS;
    public TextView         PFS;

    public                  CryptCheckHolder(View v) {
        super(v);
        rootView = v;
        headerProto = rootView.findViewById(R.id.headerProto);
        rootViewItemCryptcheck =  rootView.findViewById(R.id.rootViewItemCryptcheck);
        name = rootView.findViewById(R.id.Name);
        keyExchange = rootView.findViewById(R.id.KEY_EXCHANGE);
        Authentification = rootView.findViewById(R.id.AUTHENTIFICATION);
        Encryption_Type = rootView.findViewById(R.id.ENCRYPTION_TYPE);
        Encryption_KZ = rootView.findViewById(R.id.ENCRYPTION_KZ);
        Encryption_BZ = rootView.findViewById(R.id.ENCRYPTION_BZ);
        Encryption_Mode = rootView.findViewById(R.id.ENCRYPTION_MODE);
        MAC_Type = rootView.findViewById(R.id.MAC_TYPE);
        MAC_KS = rootView.findViewById(R.id.MAC_KS);
        PFS = rootView.findViewById(R.id.PFS);
    }
}
