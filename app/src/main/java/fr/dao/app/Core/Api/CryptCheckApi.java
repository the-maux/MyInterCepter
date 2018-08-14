package fr.dao.app.Core.Api;

import android.os.Handler;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;

import fr.dao.app.Core.Dora;
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
    private int                         retrie = 0;

    private                             CryptCheckApi() {
    }

    public void                         callForSite(final CryptFrgmnt cryptFrgmnt, final String site) throws IOException {
        Log.d(TAG, ">>>>   " + site);
        Ion.with(cryptFrgmnt)
                .load(URL + site + ".json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null || result == null) {
                            cryptFrgmnt.onResponseServer("Server didn't answer");
                            return;
                        }

                        if (result.get("pending") != null && !result.get("pending").isJsonNull() &&
                                result.get("pending").getAsBoolean() && retrie++ <= 9) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    try {
                                        cryptFrgmnt.scanInProgress();
                                        callForSite(cryptFrgmnt, site);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }, 1000);
                            return;
                        }
                        CryptCheckScan scan = new GsonBuilder().create().fromJson(result, CryptCheckScan.class);
                        if (scan == null || scan.results == null) {
                            cryptFrgmnt.onResponseServer("Error in scanning");
                            return;
                        }
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
                        scan.getProtos(false, false, false, false); //Hack to init the boolean, stfu /!\ i do what i want
                        cryptFrgmnt.onResponseServer(scan);
                    }
                });
    }

}
