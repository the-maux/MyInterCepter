package fr.dao.app.Model.Config.Cryptcheck;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class                                Handshakes {
    @SerializedName("certs")
    public ArrayList<certificates>          certs;
    @SerializedName("dh")
    public ArrayList<dh>                    dhs;
    @SerializedName("protocols")
    public ArrayList<ProtocolCrypt>         protocols;
    @SerializedName("ciphers")
    public ArrayList<Ciphers>               ciphers;
    @SerializedName("ciphers_preference")
    public ArrayList<Ciphers>               ciphersPreferences;


    public class                        certificates {
        @SerializedName("subject")
        public String                   subject;
        @SerializedName("serial")
        public String                   serial;
        @SerializedName("issuer")
        public String                   issuer;
        @SerializedName("lifetime")
        public lifetime                 lifetime;
        @SerializedName("fingerprint")
        public String                   fingerprint;
        @SerializedName("states")
        public State                    state;
        //public Chain        chainCertification;
        @SerializedName("key")
        public key                      key;

        public class                key {
            @SerializedName("type")
            private String          type;
            @SerializedName("size")
            private int             size;
            @SerializedName("fingerprint")
            private String          fingerprint;
            @SerializedName("states")
            private State           state;
        }
        public class                lifetime {
            @SerializedName("not_before")
            public Date                     not_before;
            @SerializedName("not_after")
            public Date                     not_after;
        }

    }

    public class                        dh {
        @SerializedName("size")
        public int                      size;
        @SerializedName("fingerprint")
        public String                   fingerprint;
    }

    public class                        ProtocolCrypt {
        @SerializedName("protocol")
        String                          protocol;
        @SerializedName("states")
        State                           states;
    }

}
