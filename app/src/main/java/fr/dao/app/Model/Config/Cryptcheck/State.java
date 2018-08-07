package fr.dao.app.Model.Config.Cryptcheck;

public class                State {
    public class        critical {
        public boolean  mdc2_sign;
        public boolean  md2_sign;
        public boolean  md4_sign;
        public boolean  md5_sign;
        public boolean  sha_sign;
        public boolean  sha1_sign;
        public boolean  rsa;
        public boolean  sslv2;
        public boolean  sslv3;
        public boolean  dss;
        public boolean  anonymous;
        public boolean  monull;
        public boolean  export;
        public boolean  des;
        public boolean  md5;
        public boolean  rc4;
        public boolean  sweet32;
    }
    public class        error {
        public boolean  rsa;
        public boolean  tlsv1_0;
        public boolean  dhe;
    }
    public class        warning {
        public boolean  hsts;
        public boolean  tlsv1_1;
        public boolean  dhe;
    }
    public class        great {
        public boolean  hsts;
    }
}
