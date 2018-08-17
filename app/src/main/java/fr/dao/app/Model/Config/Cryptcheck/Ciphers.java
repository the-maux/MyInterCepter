package fr.dao.app.Model.Config.Cryptcheck;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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
    public ArrayList encryption;
    @SerializedName("hmac")
    public Hmac                     hmac;
    @SerializedName("states")
    public Ciphers.States           states;

    public boolean                  isTitle = false;

    public Ciphers(String name) {
        this.isTitle = true;
        this.name = name;
    }

    public void setStates(States states) {
        this.states = states;
    }

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
        @SerializedName("critical")
        public Critical         critical;
        @SerializedName("error")
        public Error            error;
        @SerializedName("warning")
        public Warning          warning;
        @SerializedName("good")
        public Good             good;

        public class    Critical {
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
        public class    Error {
            @SerializedName("pfs")
            public boolean  pfs;
        }
        public class Warning {
            @SerializedName("dhe")
            public boolean  dhe;
        }
        public class        Good {
            @SerializedName("aead")
            public boolean  aead;
        }
    }
}