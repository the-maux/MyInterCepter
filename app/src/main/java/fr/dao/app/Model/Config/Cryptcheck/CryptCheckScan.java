package fr.dao.app.Model.Config.Cryptcheck;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

    public int                          resultOffset = 0;
    public float                        grade_score = 0, grade_protocol = 0, grade_key_exchange = 0, grade_cipher_strengths = 0;
    public boolean                      isTLS10 = false, isTLS11 = false, isTLS12 = false, isTLS13 = false;

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

    public ArrayList<CryptcheckResult>  getResults() {
        return results;
    }

    public ArrayList<Ciphers>           getProtos(boolean isTLS10OK, boolean isTLS11OK, boolean isTLS12OK, boolean isTLS13OK) {
        HashMap<String, ArrayList<Ciphers>> proto_cypher = new HashMap<>();
        sortThis(proto_cypher, results.get(resultOffset).handshakes.ciphers);
        sortThis(proto_cypher, results.get(resultOffset).handshakes.ciphersPreferences);
        ArrayList<Ciphers> ciphers = new ArrayList<>();
        if (proto_cypher.get("TLSv1_0") != null && isTLS10OK)  {
            ciphers.add(new Ciphers("TLSv1_0"));
            ciphers.addAll(proto_cypher.get("TLSv1_0"));
        }
        if (proto_cypher.get("TLSv1_1") != null && isTLS11OK)  {
            ciphers.add(new Ciphers("TLSv1_1"));
            ciphers.addAll(proto_cypher.get("TLSv1_1"));
        }
        if (proto_cypher.get("TLSv1_2") != null && isTLS12OK)  {
            ciphers.add(new Ciphers("TLSv1_2"));
            ciphers.addAll(proto_cypher.get("TLSv1_2"));
        }
        if (proto_cypher.get("TLSv1_3") != null && isTLS13OK)  {
            ciphers.add(new Ciphers("TLSv1_3"));
            ciphers.addAll(proto_cypher.get("TLSv1_3"));
        }
        return ciphers;
    }


    public ArrayList<Ciphers>           getProtos() {
        HashMap<String, ArrayList<Ciphers>> proto_cypher = new HashMap<>();
        sortThis(proto_cypher, results.get(resultOffset).handshakes.ciphers);
        sortThis(proto_cypher, results.get(resultOffset).handshakes.ciphersPreferences);
        ArrayList<Ciphers> ciphers = new ArrayList<>();
        if (proto_cypher.get("TLSv1_0") != null)  {
            isTLS10 = true;
            ciphers.add(new Ciphers("TLSv1_0"));
            ciphers.addAll(proto_cypher.get("TLSv1_0"));
        }
        if (proto_cypher.get("TLSv1_1") != null)  {
            isTLS11 = true;
            ciphers.add(new Ciphers("TLSv1_1"));
            ciphers.addAll(proto_cypher.get("TLSv1_1"));
        }
        if (proto_cypher.get("TLSv1_2") != null)  {
            isTLS12 = true;
            ciphers.add(new Ciphers("TLSv1_2"));
            ciphers.addAll(proto_cypher.get("TLSv1_2"));
        }
        if (proto_cypher.get("TLSv1_3") != null)  {
            isTLS13 = true;
            ciphers.add(new Ciphers("TLSv1_3"));
            ciphers.addAll(proto_cypher.get("TLSv1_3"));
        }
        return ciphers;
    }

    public void                         updateOffset(int position) {
        this.resultOffset = position;
    }

    public void dump() {

        Log.i("CryptCheckScan", "---------------------------");
        Log.i("CryptCheckScan", "SCAN:" + host + "\t[SCORE:" + grade_score +";PROTO" + grade_protocol + ";KEYEXCHANGE" + grade_key_exchange + ";CIPHER" + grade_cipher_strengths+ "]");
        for (CryptcheckResult result : results) {
            Log.i("CryptCheckScan", "SCAN:" + result.ip + " -> " + result.grade);
            Log.i("CryptCheckScan", "\tcert[" + result.handshakes.certs.size() + "]& cipher[" + (result.handshakes.ciphers.size() + result.handshakes.ciphersPreferences.size()) + "]");
        }

    }



    private void                        sortThis(HashMap<String, ArrayList<Ciphers>> proto_cypher, ArrayList<Ciphers> ciphers) {
        for (Ciphers cipher : ciphers) {
            if (cipher.protocol.contentEquals("TLSv1"))
                cipher.protocol = "TLSv1_0";
            if (proto_cypher.get(cipher.protocol) != null && cipher.name == null) {
                continue;/* Protection against title doublon*/
            } else if (proto_cypher.get(cipher.protocol) == null) {
                proto_cypher.put(cipher.protocol, new ArrayList<Ciphers>());
            }
            if (cipher.name != null && !cipher.name.isEmpty())
                proto_cypher.get(cipher.protocol).add(cipher);
        }

    }
}
