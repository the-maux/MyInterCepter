package fr.dao.app.Model.Config.Cryptcheck;

import java.util.ArrayList;
import java.util.Date;

public class                            CryptCheckScan {
    public Date                         date;
    public String                       hote;
    public int                          port;
    public ArrayList<CryptcheckResult>  results;
    public String                       type;
    public Date                         updated_at;

    public class                     CryptcheckResult {
        public String                hostname;
        public String                ip;
        public int                   port;
        public ArrayList<Handshakes> handshakes;
        public State                 state;
        public String                grade;
    }

}
