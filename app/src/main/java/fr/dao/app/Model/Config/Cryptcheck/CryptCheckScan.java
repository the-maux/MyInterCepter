package fr.dao.app.Model.Config.Cryptcheck;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class                            CryptCheckScan {
    @SerializedName("created_at")
    public Date                         date;
    @SerializedName("host")
    public String                       host;
    @SerializedName("port")
    public int                          port;
    @SerializedName("result")
    public ArrayList<CryptcheckResult>  results;
    @SerializedName("type")
    public String                       type;
    @SerializedName("updated_at")
    public Date                         updated_at;

    public class                     CryptcheckResult {
        @SerializedName("hostname")
        public String                hostname;
        @SerializedName("ip")
        public String                ip;
        @SerializedName("port")
        public int                   port;
        @SerializedName("handshakes")
        public Handshakes            handshakes;
        @SerializedName("states")
        public State                 state;
        @SerializedName("grade")
        public String                grade;
    }

}
