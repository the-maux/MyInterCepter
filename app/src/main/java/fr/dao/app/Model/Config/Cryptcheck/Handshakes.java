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

    public class                        Ciphers {
        @SerializedName("protocol")
        public String                   protocol;
        @SerializedName("name")
        public String                   name;
        @SerializedName("key_exchange")
        public String                   key_echange;
        @SerializedName("authentication")
        public String                   authentification;
        @SerializedName("encryption")
        private ArrayList               encryption;
        @SerializedName("hmac")
        public Hmac                     hmac;
        @SerializedName("states")
        public Ciphers.States           states;

        public class                Encryption {
            @SerializedName("")
            public String           type;
            @SerializedName("")
            public String           KeySize;
            @SerializedName("")
            public String           BlockSize;
            @SerializedName("")
            public String           Mode;

            public Encryption(JsonArray array) {
                type = array.get(0).getAsString();
                KeySize = array.get(1).getAsString();
                BlockSize = array.get(2).getAsString();
                Mode = array.get(3).getAsString();
            }
        }
        public class                Hmac {
            @SerializedName("name")
            public String           name;
            @SerializedName("size")
            public int              size;
        }
        public class                States {
            public class        critical {
                @SerializedName("dss")
                public boolean  dss;
                @SerializedName("anonymous")
                public boolean  anonymous;
                @SerializedName("null")
                public boolean  isNull;
                @SerializedName("export")
                public boolean  export;
                @SerializedName("des")
                public boolean  des;
                @SerializedName("md5")
                public boolean  md5;
                @SerializedName("rc4")
                public boolean  rc4;
                @SerializedName("sweet32")
                public boolean  sweet32;
            }
            public class        error {
                @SerializedName("pfs")
                public boolean  pfs;
            }
            public class        warning {
                @SerializedName("dhe")
                public boolean  dhe;
            }
            public class        good {
                @SerializedName("aead")
                public boolean  aead;
            }
        }
    }

}
