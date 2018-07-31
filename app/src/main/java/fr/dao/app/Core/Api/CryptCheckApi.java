package fr.dao.app.Core.Api;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

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
        useJsoup(site);
    }

    private void                        useJsoup(final String site) throws IOException {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect(URL + site).get();
                    Elements elems = doc.getElementsByClass("row");
                    int a = 0;
                    Log.w(TAG, "Il y a " + elems.size() + " tag dans cette page");
                    for (Element elem : elems) {
                        switch (a) {
                            case 0:
                                parseHeader(elem);
                                break;
                            case 1:
                                parseHeaderServer(elem, true);
                                break;
                            case 2:
                                parseWarningServer(elem, true);
                                break;
                            case 3:
                                parseBodyServer(elem, true);
                                break;
                            case 4:
                                parseHeaderServer(elem, false);
                                break;
                            case 5:
                                parseWarningServer(elem, false);
                                break;
                            case 6:
                                parseBodyServer(elem, false);
                                break;
                            default:
                                Log.e(TAG, "DEFAULT" + elem.outerHtml());
                                break;
                        }
                        a = a + 1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void                        parseHeader(Element elem) {
        /**
         <div class="col-sm-11">
         <h1> [HTTPS] blog.valvin.fr <span class="small">(Tue, 31 Jul 2018 10:53:33 +0000)</span> </h1>
         </div>
         <div class="col-sm-1">
         <a class="btn btn-default" href="/https/blog.valvin.fr/refresh">Refresh</a>
         </div>
         </div>
         * */
        Log.w(TAG, "HeaderBig" + elem.outerHtml());
    }

    private void                        parseHeaderServer(Element elem, boolean isIpv6) {
        /**
         <div class="col-sm-12">
         <h2> <span class="label label-state-warning">E</span> 2001:bc8:4700:2200::1:c2b : 443 <span class="small">(blog.valvin.fr)</span></h2>
         </div>
         </div>
         * */
        if (isIpv6) {
            Log.i(TAG, "Header" + elem.outerHtml());
        } else {
            Log.d(TAG, "Header" + elem.outerHtml());
        }
    }

    private void                        parseWarningServer(Element elem, boolean isIpv6) {
        if (isIpv6) {
            Log.i(TAG, "Warning" + elem.outerHtml());
        } else {
            Log.d(TAG, "Warning" + elem.outerHtml());
        }

        /**
         *
         *
         <div class="row">
         <div class="col-sm-12">
         <div class="alert alert-warning">
         This server doesn't support HSTS
         </div>
         <div class="alert alert-warning">
         This server supports TLSv1.1
         </div>
         <div class="alert alert-warning">
         This server supports DHE ciphers
         </div>
         <div class="alert alert-good">
         This server supports AEAD ciphers
         </div>
                 <!--
                 <h3><span class="translation_missing" title="translation missing: en.Checks">Checks</span></h3>
                 <table class="table table-bordered table-condensed table-striped">
                 <thead>
                 <th><span class="translation_missing" title="translation missing: en.Severity">Severity</span></th>
                 <td>
                 <span class="translation_missing" title="translation missing: en.Checks">Checks</span>
                 (
                 <span class="label label-success">OK</span>
                 <span class="label label-danger">KO</span>
                 <span class="label label-default">N/A</span>
                 )
                 </td>
                 </thead>
                 <tbody>
                     <tr>
                         <th><span class="label label-state-critical">critical</span></th>
                         <td><span class="label label-success">mdc2_sign</span> <span class="label label-success">md2_sign</span> <span class="label label-success">md4_sign</span> <span class="label label-success">md5_sign</span> <span class="label label-success">sha_sign</span> <span class="label label-success">sha1_sign</span> <span class="label label-success">rsa</span> <span class="label label-success">dh</span> <span class="label label-success">sslv2</span> <span class="label label-success">sslv3</span> <span class="label label-success">dss</span> <span class="label label-success">anonymous</span> <span class="label label-success">null</span> <span class="label label-success">export</span> <span class="label label-success">des</span> <span class="label label-success">md5</span> <span class="label label-success">rc4</span> <span class="label label-success">sweet32</span></td>
                     </tr>
                     <tr>
                         <th><span class="label label-state-error">error</span></th>
                         <td><span class="label label-success">rsa</span> <span class="label label-success">dh</span> <span class="label label-success">tlsv1_0</span> <span class="label label-success">pfs</span></td>
                     </tr>
                     <tr>
                         <th><span class="label label-state-warning">warning</span></th>
                         <td><span class="label label-danger">hsts</span> <span class="label label-danger">tlsv1_1</span> <span class="label label-danger">dhe</span></td>
                     </tr>
                     <tr>
                         <th><span class="label label-state-good">good</span></th>
                         <td><span class="label label-danger">fallback_scsv</span> <span class="label label-danger">hsts</span> <span class="label label-success">aead</span></td>
                     </tr>
                     <tr>
                         <th><span class="label label-state-great">great</span></th>
                         <td><span class="label label-danger">hsts</span></td>
                     </tr>
                     <tr>
                         <th><span class="label label-state-best">best</span></th>
                         <td></td>
                     </tr>
                 </tbody>
                 </table>
                 -->
         *
         */
    }

    private void                        parseBodyServer(Element elem, boolean b) {
        if (b) {
            Log.i(TAG, "Body" + elem.outerHtml());
        } else {
            Log.d(TAG, "Body" + elem.outerHtml());
        }

        /**
         *
         *
         <div class="row">
         <div class="col-sm-12">
         <table class="table table-bordered table-condensed table-striped center">
         <thead>
             <tr>
                 <th rowspan="2">Name</th>
                 <th rowspan="2">Key exchange</th>
                 <th rowspan="2">Authentication</th>
                 <th colspan="4">Encryption</th>
                 <th colspan="2">MAC</th>
                 <th rowspan="2">PFS</th>
             </tr>
             <tr>
                 <th>Type</th>
                 <th>Key size</th>
                 <th>Block size</th>
                 <th>Mode</th>
                 <th>Type</th>
                 <th>Size</th>
             </tr>
         </thead>
         <tbody>
         <tr>
         <th colspan="12"><span class="label label-state-default">TLSv1_2</span></th>
         </tr>
         <tr>
             <th><span class="label label-state-success">&nbsp;</span>&nbsp;ECDHE-RSA-AES128-GCM-SHA256</th>
             <td class="label-state-">ECDH</td>
             <td class="label-state-">RSA</td>
             <td class="label-state-">AES</td>
             <td class="label-state-">128</td>
             <td class="label-state-">128</td>
             <td class="label-state-success">GCM</td>
             <td class="label-state-">SHA256</td>
             <td class="label-state-">256</td>
             <td class="label-state-">PFS</td>
         </tr>
         <tr>
             <th><span class="label label-state-success">&nbsp;</span>&nbsp;ECDHE-RSA-AES256-GCM-SHA384</th>
             <td class="label-state-">ECDH</td>
             <td class="label-state-">RSA</td>
             <td class="label-state-">AES</td>
             <td class="label-state-">256</td>
             <td class="label-state-">256</td>
             <td class="label-state-success">GCM</td>
             <td class="label-state-">SHA384</td>
             <td class="label-state-">384</td>
             <td class="label-state-">PFS</td>
         </tr>
         <tr>
             <th><span class="label label-state-default">&nbsp;</span>&nbsp;ECDHE-RSA-AES128-SHA</th>
             <td class="label-state-">ECDH</td>
             <td class="label-state-">RSA</td>
             <td class="label-state-">AES</td>
             <td class="label-state-">128</td>
             <td class="label-state-">128</td>
             <td class="label-state-">CBC</td>
             <td class="label-state-warning">SHA1</td>
             <td class="label-state-">160</td>
             <td class="label-state-">PFS</td>
         </tr>
         <tr>
             <th><span class="label label-state-default">&nbsp;</span>&nbsp;ECDHE-RSA-AES128-SHA256</th>
             <td class="label-state-">ECDH</td>
             <td class="label-state-">RSA</td>
             <td class="label-state-">AES</td>
             <td class="label-state-">128</td>
             <td class="label-state-">128</td>
             <td class="label-state-">CBC</td>
             <td class="label-state-">SHA256</td>
             <td class="label-state-">256</td>
             <td class="label-state-">PFS</td>
         </tr>
         <tr>
             <th><span class="label label-state-default">&nbsp;</span>&nbsp;ECDHE-RSA-AES256-SHA</th>
             <td class="label-state-">ECDH</td>
             <td class="label-state-">RSA</td>
             <td class="label-state-">AES</td>
             <td class="label-state-">256</td>
             <td class="label-state-">256</td>
             <td class="label-state-">CBC</td>
             <td class="label-state-warning">SHA1</td>
             <td class="label-state-">160</td>
             <td class="label-state-">PFS</td>
         </tr>
         <tr>
             <th><span class="label label-state-default">&nbsp;</span>&nbsp;ECDHE-RSA-AES256-SHA384</th>
             <td class="label-state-">ECDH</td>
             <td class="label-state-">RSA</td>
             <td class="label-state-">AES</td>
             <td class="label-state-">256</td>
             <td class="label-state-">256</td>
             <td class="label-state-">CBC</td>
             <td class="label-state-">SHA384</td>
             <td class="label-state-">384</td>
             <td class="label-state-">PFS</td>
         </tr>
         <tr>
             <th><span class="label label-state-warning">&nbsp;</span>&nbsp;DHE-RSA-AES128-GCM-SHA256</th>
             <td class="label-state-warning">DH</td>
             <td class="label-state-">RSA</td>
             <td class="label-state-">AES</td>
             <td class="label-state-">128</td>
             <td class="label-state-">128</td>
             <td class="label-state-success">GCM</td>
             <td class="label-state-">SHA256</td>
             <td class="label-state-">256</td>
             <td class="label-state-">PFS</td>
         *
         */
    }
}
