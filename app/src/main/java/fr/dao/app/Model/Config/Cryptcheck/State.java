package fr.dao.app.Model.Config.Cryptcheck;

import com.google.gson.annotations.SerializedName;

public class                State {
    public class        critical {
        @SerializedName("mdc2_sign")
        public boolean  mdc2_sign;
        @SerializedName("md2_sign")
        public boolean  md2_sign;
        @SerializedName("md4_sign")
        public boolean  md4_sign;
        @SerializedName("md5_sign")
        public boolean  md5_sign;
        @SerializedName("sha_sign")
        public boolean  sha_sign;
        @SerializedName("sha1_sign")
        public boolean  sha1_sign;
        @SerializedName("rsa")
        public boolean  rsa;
        @SerializedName("dh")
        public boolean  dh;
        @SerializedName("sslv2")
        public boolean  sslv2;
        @SerializedName("sslv3")
        public boolean  sslv3;
        @SerializedName("dss")
        public boolean  dss;
        @SerializedName("anonymous")
        public boolean  anonymous;
        @SerializedName("null")
        public boolean  isnull;
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
        @SerializedName("rsa")
        public boolean  rsa;
        @SerializedName("dh")
        public boolean  dh;
        @SerializedName("tlsv1_0")
        public boolean  tlsv1_0;
        @SerializedName("pfs")
        public boolean  pfs;
    }
    public class        warning {
        @SerializedName("hsts")
        public boolean  hsts;
        @SerializedName("tlsv1_1")
        public boolean  tlsv1_1;
        @SerializedName("dhe")
        public boolean  dhe;
    }
    public class        good {
        @SerializedName("hsts")
        public boolean  hsts;
        @SerializedName("aead")
        public boolean  aead;
    }
    public class        great {
        @SerializedName("hsts")
        public boolean  hsts;
    }
}
