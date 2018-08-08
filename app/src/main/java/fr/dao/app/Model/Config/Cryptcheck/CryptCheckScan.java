package fr.dao.app.Model.Config.Cryptcheck;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.Cipher;

import fr.dao.app.Model.Config.CryptCheckModel;

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

    public int                     resultOffset = 0;

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

    public ArrayList<CryptcheckResult> getResults() {
        return results;
    }

    public ArrayList<Ciphers>       getProtos() {
        HashMap<String, ArrayList<Ciphers>> proto_cypher = new HashMap<>();
        for (Ciphers cipher : results.get(resultOffset).handshakes.ciphers) {
            if (proto_cypher.get(cipher.protocol) == null) {
                proto_cypher.put(cipher.protocol, new ArrayList<Ciphers>());
            }
            proto_cypher.get(cipher.protocol).add(cipher);
        }
        for (Ciphers cipher : results.get(resultOffset).handshakes.ciphersPreferences) {
            if (proto_cypher.get(cipher.protocol) == null) {
                proto_cypher.put(cipher.protocol, new ArrayList<Ciphers>());
            }
            proto_cypher.get(cipher.protocol).add(cipher);
        }
        ArrayList<Ciphers> ciphers = new ArrayList<>();
        ciphers.add(new Ciphers("TLSv1"));
        ciphers.addAll(proto_cypher.get("TLSv1_1"));
        ciphers.add(new Ciphers("TLSv1_2"));
        ciphers.addAll(proto_cypher.get("TLSv1_2"));
        ciphers.add(new Ciphers("TLSv1_2"));
        ciphers.addAll(proto_cypher.get("TLSv1_2"));
        return ciphers;
    }
}
