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
                .load(URL + site + ".json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray hosts = result.getAsJsonArray("hosts");
                        for (JsonElement resultHost : hosts) {
                            for (CryptCheckScan.CryptcheckResult cryptcheckResult : scan.results) {
                                if (resultHost.getAsJsonObject().getAsJsonObject("host").getAsJsonObject("ip").getAsString().contentEquals(cryptcheckResult.ip)) {
                                    cryptcheckResult.grade_score = resultHost.getAsJsonObject().getAsJsonObject("grade").getAsJsonObject("score").getAsInt();
                                    cryptcheckResult.grade_protocol = resultHost.getAsJsonObject().getAsJsonObject("grade").getAsJsonObject("protocol").getAsInt();
                                    cryptcheckResult.grade_key_exchange = resultHost.getAsJsonObject().getAsJsonObject("grade").getAsJsonObject("key_exchange").getAsInt();
                                    cryptcheckResult.grade_cipher_strengths= resultHost.getAsJsonObject().getAsJsonObject("grade").getAsJsonObject("cipher_strengths").getAsInt();
                                }
                            }
                        }
                        cryptFrgmnt.onResponseServer(scan);
                    }
                });
    }

}
