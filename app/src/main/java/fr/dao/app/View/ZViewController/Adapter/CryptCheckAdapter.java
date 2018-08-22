package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import fr.dao.app.Model.Config.Cryptcheck.Ciphers;
import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.R;
import fr.dao.app.View.Cryptcheck.CryptFrgmnt;
import fr.dao.app.View.ZViewController.Adapter.Holder.CryptCheckHolder;

public class                    CryptCheckAdapter extends RecyclerView.Adapter<CryptCheckHolder> {
    private String              TAG = "CryptCheckAdapter";
    private ArrayList<Ciphers>  protos;
    private LinkedHashMap<String, short[]> classification;
    private Activity            mActivity;
    private boolean             isTLS10 = true, isTLS11 = true, isTLS12 = true, isTLS13 = true;
    private int                 red = 0xffd50000, orange = 0xddef6c00, yellow = 0xddffab00, green = 0xff4caf50;

    public CryptCheckAdapter(Activity activity) {
        this.mActivity = activity;
        classification = initClassificaion();
    }

    @NonNull
    public CryptCheckHolder      onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CryptCheckHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cryptcheck, parent, false));
    }

    public void                 onBindViewHolder(CryptCheckHolder holder, int position) {
        Ciphers cipher = protos.get(position);
        holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mActivity.getResources().getDimension(R.dimen.text_size_2));
        holder.name.setText(cipher.name.toUpperCase());
        initHeaderProto(holder.headerProto, cipher.protocol);
        holder.keyExchange.setText(cipher.key_echange.toUpperCase());
        holder.Authentification.setText(cipher.authentification.toUpperCase());
        holder.PFS.setText((cipher.states.error.pfs) ? "PFS" : "No PFS");
        if (cipher.encryption != null) {
            String type = cipher.encryption.get(0).toString();
            if (type.length() > 6) {
                holder.Encryption_Type.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mActivity.getResources().getDimension(R.dimen.text_size_1));
            } else
                holder.Encryption_Type.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mActivity.getResources().getDimension(R.dimen.text_size_2));
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
        }
        setBackgrounds(holder, cipher.name);
        //        holder.rootViewItemCryptcheck.setBackgroundColor(mActivity.getResources().getColor(R.color.material_blue_grey_100));
    }

    private void                setBackgrounds(CryptCheckHolder holder, String name) {
        short[] backgroundsType = classification.get(name);
        holder.keyExchange.setBackgroundColor(getColorFromId(backgroundsType[0]));
        holder.Authentification.setBackgroundColor(getColorFromId(backgroundsType[1]));
        holder.Encryption_Type.setBackgroundColor(getColorFromId(backgroundsType[2]));
        holder.Encryption_KZ.setBackgroundColor(getColorFromId(backgroundsType[3]));
        holder.Encryption_BZ.setBackgroundColor(getColorFromId(backgroundsType[4]));
        holder.Encryption_Mode.setBackgroundColor(getColorFromId(backgroundsType[5]));
        holder.MAC_Type.setBackgroundColor(getColorFromId(backgroundsType[6]));
        holder.MAC_KS.setBackgroundColor(getColorFromId(backgroundsType[7]));
        holder.PFS.setBackgroundColor(getColorFromId(backgroundsType[8]));
        switch (backgroundsType[9]) {
            case 0:
                holder.headerProto.setBackgroundColor(green);
                break;
            case 1:
                holder.headerProto.setBackgroundColor(0xff0097a7);
                break;
            case 2:
                holder.headerProto.setBackgroundColor(yellow);
                break;
            case 3:
                holder.headerProto.setBackgroundColor(orange);
                break;
            case 4:
                holder.headerProto.setBackgroundColor(red);
                break;
        }
    }

    private int                 getColorFromId(short id) {
        switch (id) {
            case 0:
                return ContextCompat.getColor(mActivity, R.color.primary_white);
            case 1:
                return green;//green
            case 2:
                return yellow;//yellow
            case 3:
                return orange;//orange
            case 4:
                return red;//red
            default:
                return ContextCompat.getColor(mActivity, R.color.primary_white);
        }

    }

    private void                initHeaderProto(View headerProto, String protocol) {
        switch (protocol) {
            case "TLSv1_0":
                headerProto.setBackgroundResource(R.drawable.background_tls10);
                break;
            case "TLSv1_1":
                headerProto.setBackgroundResource(R.drawable.background_tls11);
                break;
            case "TLSv1_2":
                headerProto.setBackgroundResource(R.drawable.background_tls12);
                break;
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

    private LinkedHashMap<String, short[]>       initClassificaion() {
        LinkedHashMap classicfication = new LinkedHashMap<String, short[]>();
        classicfication.put("ECDHE-ECDSA-AES128-GCM-SHA256",           new short[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0});
        classicfication.put("ECDHE-ECDSA-AES256-GCM-SHA384",           new short[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0});
        classicfication.put("ECDHE-ECDSA-CHACHA20-POLY1305",           new short[]{0, 0, 1, 0, 0, 1, 1, 0, 0, 0});
        classicfication.put("ECDHE-ECDSA-CHACHA20-POLY1305-D",         new short[]{0, 0, 1, 0, 0, 1, 1, 0, 0, 0});
        classicfication.put("ECDHE-RSA-AES128-GCM-SHA256",             new short[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0});
        classicfication.put("ECDHE-RSA-AES256-GCM-SHA384",             new short[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0});
        classicfication.put("ECDHE-RSA-CHACHA20-POLY1305",             new short[]{0, 0, 1, 0, 0, 1, 1, 0, 0, 0});
        classicfication.put("ECDHE-RSA-CHACHA20-POLY1305-D",           new short[]{0, 0, 1, 0, 0, 1, 1, 0, 0, 0});//GREEN
        classicfication.put("ECDHE-ECDSA-AES128-SHA",                  new short[]{0, 0, 0, 0, 0, 0, 2, 0, 0, 1});
        classicfication.put("ECDHE-ECDSA-AES128-SHA256",               new short[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
        classicfication.put("ECDHE-ECDSA-AES256-SHA",                  new short[]{0, 0, 0, 0, 0, 0, 2, 0, 0, 1});
        classicfication.put("ECDHE-ECDSA-AES256-SHA384",               new short[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
        classicfication.put("ECDHE-ECDSA-DES-CBC3-SHA",                new short[]{0, 0, 0, 4, 4, 0, 2, 0, 0, 1});
        classicfication.put("ECDHE-RSA-AES128-SHA",                    new short[]{0, 0, 0, 0, 0, 0, 2, 0, 0, 1});
        classicfication.put("ECDHE-RSA-AES128-SHA256",                 new short[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
        classicfication.put("ECDHE-RSA-AES256-SHA",                    new short[]{0, 0, 0, 0, 0, 0, 2, 0, 0, 1});
        classicfication.put("ECDHE-RSA-AES256-SHA384",                 new short[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
        classicfication.put("ECDHE-RSA-DES-CBC3-SHA",                  new short[]{0, 0, 0, 4, 4, 0, 2, 0, 0, 1});//GREY
        classicfication.put("DHE-RSA-AES128-GCM-SHA256",               new short[]{0, 2, 0, 0, 0, 1, 0, 0, 0, 2});
        classicfication.put("DHE-RSA-AES128-SHA",                      new short[]{0, 2, 0, 0, 0, 0, 2, 0, 0, 2});
        classicfication.put("DHE-RSA-AES128-SHA256",                   new short[]{0, 2, 0, 0, 0, 0, 0, 0, 0, 2});
        classicfication.put("DHE-RSA-AES256-GCM-SHA384",               new short[]{0, 2, 0, 0, 0, 1, 0, 0, 0, 2});
        classicfication.put("DHE-RSA-AES256-SHA",                      new short[]{0, 2, 0, 0, 0, 0, 2, 0, 0, 2});
        classicfication.put("DHE-RSA-AES256-SHA256",                   new short[]{0, 2, 0, 0, 0, 0, 0, 0, 0, 2});
        classicfication.put("DHE-RSA-CAMELLIA128-SHA",                 new short[]{0, 2, 0, 0, 0, 0, 2, 0, 0, 2});
        classicfication.put("DHE-RSA-CAMELLIA256-SHA",                 new short[]{0, 2, 0, 0, 0, 0, 2, 0, 0, 2});
        classicfication.put("DHE-RSA-CHACHA20-POLY1305",               new short[]{0, 2, 1, 0, 0, 1, 1, 0, 0, 2});
        classicfication.put("DHE-RSA-CHACHA20-POLY1305-D",             new short[]{0, 2, 1, 0, 0, 1, 1, 0, 0, 2});
        classicfication.put("DHE-RSA-SEED-SHA",                        new short[]{0, 2, 0, 0, 0, 0, 2, 0, 0, 2});
        classicfication.put("EDH-RSA-DES-CBC3-SHA",                    new short[]{0, 2, 0, 4, 4, 0, 2, 0, 0, 2});//YELLOW
        classicfication.put("AES128-GCM-SHA256",                       new short[]{0, 3, 0, 0, 0, 1, 0, 0, 3, 3});
        classicfication.put("AES128-SHA",                              new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("AES128-SHA256",                           new short[]{0, 3, 0, 0, 0, 0, 0, 0, 3, 3});
        classicfication.put("AES256-GCM-SHA384",                       new short[]{0, 3, 0, 0, 0, 1, 0, 0, 3, 3});
        classicfication.put("AES256-SHA",                              new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("AES256-SHA256",                           new short[]{0, 3, 0, 0, 0, 0, 0, 0, 3, 3});
        classicfication.put("CAMELLIA128-SHA",                         new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("CAMELLIA256-SHA",                         new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("DES-CBC3-SHA",                            new short[]{0, 3, 0, 4, 4, 0, 2, 0, 3, 3});
        classicfication.put("DH-RSA-AES128-GCM-SHA256",                new short[]{0, 2, 0, 0, 0, 1, 0, 0, 3, 3});
        classicfication.put("DH-RSA-AES128-SHA",                       new short[]{0, 2, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("DH-RSA-AES128-SHA256",                    new short[]{0, 2, 0, 0, 0, 0, 0, 0, 3, 3});
        classicfication.put("DH-RSA-AES256-GCM-SHA384",                new short[]{0, 2, 0, 0, 0, 1, 0, 0, 3, 3});
        classicfication.put("DH-RSA-AES256-SHA",                       new short[]{0, 2, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("DH-RSA-AES256-SHA256",                    new short[]{0, 2, 0, 0, 0, 0, 0, 0, 3, 3});
        classicfication.put("DH-RSA-CAMELLIA128-SHA",                  new short[]{0, 2, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("DH-RSA-CAMELLIA256-SHA",                  new short[]{0, 2, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("DH-RSA-DES-CBC3-SHA",                     new short[]{0, 2, 0, 4, 4, 0, 2, 0, 3, 3});
        classicfication.put("DH-RSA-SEED-SHA",                         new short[]{0, 2, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("ECDH-ECDSA-AES128-GCM-SHA256",            new short[]{0, 0, 0, 0, 0, 1, 0, 0, 3, 3});
        classicfication.put("ECDH-ECDSA-AES128-SHA",                   new short[]{0, 0, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("ECDH-ECDSA-AES128-SHA256",                new short[]{0, 0, 0, 0, 0, 0, 0, 0, 3, 3});
        classicfication.put("ECDH-ECDSA-AES256-GCM-SHA384",            new short[]{0, 0, 0, 0, 0, 1, 0, 0, 3, 3});
        classicfication.put("ECDH-ECDSA-AES256-SHA",                   new short[]{0, 0, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("ECDH-ECDSA-AES256-SHA384",                new short[]{0, 0, 0, 0, 0, 0, 0, 0, 3, 3});
        classicfication.put("ECDH-ECDSA-DES-CBC3-SHA",                 new short[]{0, 0, 0, 4, 4, 0, 2, 0, 3, 3});
        classicfication.put("ECDH-RSA-AES128-GCM-SHA256",              new short[]{0, 0, 0, 0, 0, 1, 0, 0, 3, 3});
        classicfication.put("ECDH-RSA-AES128-SHA",                     new short[]{0, 0, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("ECDH-RSA-AES128-SHA256",                  new short[]{0, 0, 0, 0, 0, 0, 0, 0, 3, 3});
        classicfication.put("ECDH-RSA-AES256-GCM-SHA384",              new short[]{0, 0, 0, 0, 0, 1, 0, 0, 3, 3});
        classicfication.put("ECDH-RSA-AES256-SHA",                     new short[]{0, 0, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("ECDH-RSA-AES256-SHA384",                  new short[]{0, 0, 0, 0, 0, 0, 0, 0, 3, 3});
        classicfication.put("ECDH-RSA-DES-CBC3-SHA",                   new short[]{0, 0, 0, 4, 4, 0, 2, 0, 3, 3});
        classicfication.put("IDEA-CBC-SHA",                            new short[]{0, 3, 0, 0, 4, 0, 2, 0, 3, 3});
        classicfication.put("PSK-3DES-EDE-CBC-SHA",                    new short[]{0, 3, 0, 4, 4, 0, 2, 0, 3, 3});
        classicfication.put("PSK-AES128-CBC-SHA",                      new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("PSK-AES256-CBC-SHA",                      new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("PSK-CHACHA20-POLY1305",                   new short[]{0, 3, 1, 0, 0, 1, 1, 0, 3, 3});
        classicfication.put("SEED-SHA",                                new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("SRP-3DES-EDE-CBC-SHA",                    new short[]{0, 3, 0, 4, 4, 0, 2, 0, 3, 3});
        classicfication.put("SRP-AES-128-CBC-SHA",                     new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("SRP-AES-256-CBC-SHA",                     new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("SRP-RSA-3DES-EDE-CBC-SHA",                new short[]{0, 3, 0, 4, 4, 0, 2, 0, 3, 3});
        classicfication.put("SRP-RSA-AES-128-CBC-SHA",                 new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});
        classicfication.put("SRP-RSA-AES-256-CBC-SHA",                 new short[]{0, 3, 0, 0, 0, 0, 2, 0, 3, 3});//ORANGE
        classicfication.put("ADH-AES128-GCM-SHA256",                   new short[]{4, 2, 0, 0, 0, 1, 0, 0, 0, 4});
        classicfication.put("ADH-AES128-SHA",                          new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("ADH-AES128-SHA256",                       new short[]{4, 2, 0, 0, 0, 0, 0, 0, 0, 4});
        classicfication.put("ADH-AES256-GCM-SHA384",                   new short[]{4, 2, 0, 0, 0, 1, 0, 0, 0, 4});
        classicfication.put("ADH-AES256-SHA",                          new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("ADH-AES256-SHA256",                       new short[]{4, 2, 0, 0, 0, 0, 0, 0, 0, 4});
        classicfication.put("ADH-CAMELLIA128-SHA",                     new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("ADH-CAMELLIA256-SHA",                     new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("ADH-DES-CBC-SHA",                         new short[]{4, 2, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put(" ADH-DES-CBC3-SHA",                       new short[]{4, 2, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("ADH-RC4-MD5",                             new short[]{4, 2, 4, 0, 0, 0, 4, 0, 0, 4});
        classicfication.put("ADH-SEED-SHA",                            new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("AECDH-AES128-SHA",                        new short[]{4, 0, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("AECDH-AES256-SHA",                        new short[]{4, 0, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("AECDH-DES-CBC3-SHA",                      new short[]{4, 0, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("AECDH-NULL-SHA",                          new short[]{4, 0, 4, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("AECDH-RC4-SHA",                           new short[]{4, 0, 4, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("DES-CBC-SHA",                             new short[]{0, 3, 0, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("DH-DSS-AES128-GCM-SHA256",                new short[]{4, 2, 0, 0, 0, 1, 0, 0, 3, 4});
        classicfication.put("DH-DSS-AES128-SHA \t",                    new short[]{4, 2, 0, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("DH-DSS-AES128-SHA256",                    new short[]{4, 2, 0, 0, 0, 0, 0, 0, 3, 4});
        classicfication.put("DH-DSS-AES256-GCM-SHA384",                new short[]{4, 2, 0, 0, 0, 1, 0, 0, 3, 4});
        classicfication.put("DH-DSS-AES256-SHA",                       new short[]{4, 2, 0, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("DH-DSS-AES256-SHA256",                    new short[]{4, 2, 0, 0, 0, 0, 0, 0, 3, 4});
        classicfication.put("DH-DSS-CAMELLIA128-SHA",                  new short[]{4, 2, 0, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("DH-DSS-CAMELLIA256-SHA",                  new short[]{4, 2, 0, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("DH-DSS-DES-CBC-SHA",                      new short[]{4, 2, 0, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("DH-DSS-DES-CBC3-SHA",                     new short[]{4, 2, 0, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("DH-DSS-SEED-SHA",                         new short[]{4, 2, 0, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("DH-RSA-DES-CBC-SHA",                      new short[]{0, 2, 0, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("DHE-DSS-AES128-GCM-SHA256",               new short[]{4, 2, 0, 0, 0, 1, 0, 0, 0, 4});
        classicfication.put("DHE-DSS-AES128-SHA",                      new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("DHE-DSS-AES128-SHA256",                   new short[]{4, 2, 0, 0, 0, 0, 0, 0, 0, 4});
        classicfication.put("DHE-DSS-AES256-GCM-SHA384",               new short[]{4, 2, 0, 0, 0, 1, 0, 0, 0, 4});
        classicfication.put("DHE-DSS-AES256-SHA",                      new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("DHE-DSS-AES256-SHA256",                   new short[]{4, 2, 0, 0, 0, 0, 0, 0, 0, 4});
        classicfication.put("DHE-DSS-CAMELLIA128-SHA",                 new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("DHE-DSS-CAMELLIA256-SHA",                 new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("DHE-DSS-SEED-SHA",                        new short[]{4, 2, 0, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("ECDH-ECDSA-NULL-SHA",                     new short[]{0, 0, 4, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("ECDH-ECDSA-RC4-SHA",                      new short[]{0, 0, 4, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("ECDH-RSA-NULL-SHA",                       new short[]{0, 0, 4, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("ECDH-RSA-RC4-SHA",                        new short[]{0, 0, 4, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("ECDHE-ECDSA-NULL-SHA",                    new short[]{0, 0, 4, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("ECDHE-ECDSA-RC4-SHA",                     new short[]{0, 0, 4, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("ECDHE-RSA-NULL-SHA",                      new short[]{0, 0, 4, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("ECDHE-RSA-RC4-SHA",                       new short[]{0, 0, 4, 0, 0, 0, 2, 0, 0, 4});
        classicfication.put("EDH-DSS-DES-CBC-SHA",                     new short[]{4, 2, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("EDH-DSS-DES-CBC3-SHA",                    new short[]{4, 2, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("EDH-RSA-DES-CBC-SHA",                     new short[]{0, 2, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("EXP-ADH-DES-CBC-SHA",                     new short[]{4, 2, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("EXP-ADH-RC4-MD5",                         new short[]{4, 2, 4, 0, 0, 0, 4, 0, 0, 4});
        classicfication.put("EXP-DES-CBC-SHA",                         new short[]{0, 3, 0, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("EXP-EDH-DSS-DES-CBC-SHA",                 new short[]{4, 2, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("EXP-EDH-RSA-DES-CBC-SHA",                 new short[]{0, 2, 0, 4, 4, 0, 2, 0, 0, 4});
        classicfication.put("EXP-RC2-CBC-MD5",                         new short[]{0, 3, 0, 4, 4, 0, 4, 0, 3, 4});
        classicfication.put("EXP-RC4-MD5",                             new short[]{0, 3, 4, 0, 0, 0, 4, 0, 3, 4});
        classicfication.put("NULL-MD5",                                new short[]{0, 3, 4, 4, 4, 0, 4, 0, 3, 4});
        classicfication.put("NULL-SHA",                                new short[]{0, 3, 4, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("NULL-SHA256",                             new short[]{0, 3, 4, 4, 4, 0, 0, 0, 3, 4});
        classicfication.put("PSK-RC4-SHA",                             new short[]{0, 3, 4, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("RC4-MD5",                                 new short[]{0, 3, 4, 0, 0, 0, 4, 0, 3, 4});
        classicfication.put("RC4-SHA",                                 new short[]{0, 3, 4, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("SRP-DSS-3DES-EDE-CBC-SHA",                new short[]{4, 4, 0, 4, 4, 0, 2, 0, 3, 4});
        classicfication.put("SRP-DSS-AES-128-CBC-SHA",                 new short[]{4, 4, 0, 0, 0, 0, 2, 0, 3, 4});
        classicfication.put("SRP-DSS-AES-256-CBC-SHA",                 new short[]{4, 4, 0, 0, 0, 0, 2, 0, 3, 4});//red
        return classicfication;
    }

    public void                 sort(String text ,final CryptCheckScan scan) {
        switch (text) {
            case CryptFrgmnt.TLS1:
                isTLS10 = true;
                isTLS11 = false;
                isTLS12 = false;
                break;
            case CryptFrgmnt.TLS11:
                isTLS10 = false;
                isTLS11 = true;
                isTLS12 = false;
                break;
            case CryptFrgmnt.TLS12:
                isTLS10 = false;
                isTLS11 = false;
                isTLS12 = true;
                break;
        }
        protos = scan.getProtos(isTLS10, isTLS11, isTLS12, isTLS13);
        notifyDataSetChanged();
    }
}
