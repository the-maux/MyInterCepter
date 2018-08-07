package fr.dao.app.Model.Config.Cryptcheck;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Date;

public class                                Handshakes {
    public ArrayList<certificates>          certs;
    public ArrayList<dh>                    dhs;
    public ArrayList<ProtocolCrypt>         protocols;
    public ArrayList<Ciphers>               ciphers;
    public ArrayList<CiphersPreference>     ciphersPreferences;

    public class                        certificates {
        public String                   subject;
        public String                   serial;
        public String                   issuer;
        public Date                     not_before;
        public Date                     not_after;
        public String                   fingerprint;
        private State                   state;
        //public Chain        chainCertification;


        public class                key {
            private String          type;
            private int             size;
            private String          fingerprint;
            private State           state;
        }


    }

    public class                        dh {
        public int                      size;
        public String                   fingerprint;
    }

    public class                        ProtocolCrypt {
        String                          protocol;
        State                           state;
    }

    public class                        Ciphers {
        String                          protocol;
        String                          name;
        String                          key_echange;
        String                          authentification;
        Ciphers.Encryption              encryption;
        Hmac                            hmac;
        Ciphers.States                  states;

        public class                Encryption {
            public String           type;
            public String           KeySize;
            public String           BlockSize;
            public String           Mode;

            public Encryption(JsonArray array) {
                type = array.get(0).getAsString();
                KeySize = array.get(1).getAsString();
                BlockSize = array.get(2).getAsString();
                Mode = array.get(3).getAsString();
            }
        }
        public class                Hmac {
            public String           name;
            public int              size;
        }
        public class                States {
            public class        critical {
                public boolean  dss;
                public boolean  anonymous;
                public boolean  isNull;
                public boolean  export;
                public boolean  des;
                public boolean  md5;
                public boolean  rc4;
                public boolean  sweet32;
            }
            public class        error {
                public boolean  pfs;
            }
            public class        warning {
                public boolean  dhe;
            }
            public class        good {
                public boolean  aead;
            }
        }
    }

    public class                        CiphersPreference {

    }
}
