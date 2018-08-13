package fr.dao.app.Core.Api;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;

import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.View.Cryptcheck.CryptFrgmnt;

public class                            CryptCheckApi {
    private static String               TAG = "Cryptcheck";
    private static final CryptCheckApi  ourInstance = new CryptCheckApi();
    private static final String         URL = "https://cryptcheck.fr/https/";
    private static final String         URLStat = "https://tls.imirhil.fr/https/";
    public static CryptCheckApi         getInstance() {
        return ourInstance;
    }

    private                             CryptCheckApi() {
    }

    public void                         callForSite(final CryptFrgmnt cryptFrgmnt, final String site) throws IOException {
        Ion.with(cryptFrgmnt)
                .load(URL + site + ".json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null || result == null) {
                            cryptFrgmnt.onResponseServer("Server didn't answer");
                        }
                        CryptCheckScan scan = new GsonBuilder().create().fromJson(result, CryptCheckScan.class);
                        getStat(cryptFrgmnt, site, scan);
                    }
                });
    }

    private void                        getStat(final CryptFrgmnt cryptFrgmnt, final String site, final CryptCheckScan scan) {
        Ion.with(cryptFrgmnt)
                .load(URLStat + site + ".json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray hosts = result.getAsJsonArray("hosts");
                        for (JsonElement resultHost : hosts) {
                            for (CryptCheckScan.CryptcheckResult cryptcheckResult : scan.results) {
                                String ip = resultHost.getAsJsonObject().getAsJsonObject("host").getAsJsonPrimitive("ip").getAsString();
                                if (ip.contentEquals(cryptcheckResult.ip)) {
                                    JsonObject grade = resultHost.getAsJsonObject().getAsJsonObject("grade").getAsJsonObject("details");
                                    scan.grade_score = grade.getAsJsonPrimitive("score").getAsInt();
                                    scan.grade_protocol = grade.getAsJsonPrimitive("protocol").getAsInt();
                                    scan.grade_key_exchange = grade.getAsJsonPrimitive("key_exchange").getAsInt();
                                    scan.grade_cipher_strengths = grade.getAsJsonPrimitive("cipher_strengths").getAsInt();
                                }
                            }
                        }
                        cryptFrgmnt.onResponseServer(scan);
                    }
                });
    }

}
