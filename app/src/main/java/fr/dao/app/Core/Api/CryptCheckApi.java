package fr.dao.app.Core.Api;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import fr.dao.app.Model.Config.CryptCheckModel;
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
        Log.d(TAG, ">>>> " + URL + site);

/*        Ion.with(cryptFrgmnt)
                .load(URL + site)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    public void onCompleted(Exception e, String result) {
                        if (result == null)
                            e.printStackTrace();
                        else
                            Log.d(TAG, "sendSuccess::Received:" + result.length());
                        cryptFrgmnt.onResponseServer(result);
                    }
                });
**/
        useJsoup(site, cryptFrgmnt);
    }

    private void                        useJsoup(final String site, final CryptFrgmnt cryptFrgmnt) throws IOException {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect(URL + site).get();
                    CryptCheckModel siteAnal = new CryptCheckModel(doc.getElementsByClass("row"));
                    cryptFrgmnt.onResponseServer(siteAnal);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
