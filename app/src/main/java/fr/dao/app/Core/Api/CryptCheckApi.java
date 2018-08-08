package fr.dao.app.Core.Api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import fr.dao.app.Model.Config.CryptCheckModel;
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

    public void                         callForSite(final CryptFrgmnt cryptFrgmnt, String site) throws IOException {
        Log.d(TAG, ">>>> " + URL + site + ".json");

        Ion.with(cryptFrgmnt)
                .load(URL + site + ".json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    public void onCompleted(Exception e, JsonObject result) {
                        CryptCheckScan scan = new GsonBuilder().create().fromJson(result, CryptCheckScan.class);
                        cryptFrgmnt.onResponseServer(scan);
                    }
                });
        // useJsoup(site, cryptFrgmnt);
    }

}
