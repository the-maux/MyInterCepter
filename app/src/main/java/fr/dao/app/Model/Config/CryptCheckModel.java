package fr.dao.app.Model.Config;

import android.util.Log;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class                        CryptCheckModel {
    private String                  TAG = "CryptCheckModel";
    public String                   domain = "None";
    public String                   dateScan = "None";
    public static final int         GOOD = 0x01, WARNING = 0x02, ERROR = 0x03;
    public ArrayList<Server>        servers = new ArrayList<>();
    public boolean                  analysed = false;

    public                          CryptCheckModel(Elements elems) {
        int a = 0;
        Log.w(TAG, "Il y a " + elems.size() + " tag dans cette page");
        Element header = elems.get(0);
        for (int i = 1; i < elems.size(); i = i +2) {
            Element headerServer = elems.get(i);

            Server server = new Server();
            Elements tmp = elems.get(i+1).children();
            /* Ici tu check si dans les child il y a un comment
            *           Si oui tu le vires
            *  Ensuite tu checks si y a un table dedans
            *       Si oui alors cest le body
            *  Sinon c'est les warnings
            *
            *           */

            if (tmp.hasClass("table")) {
                parseBodyServer(tmp, server);
            } else {
                parseWarningServer(tmp, server);
                parseBodyServer(elems.get(i+2), server);
            }
            servers.add(server);
            Log.d(TAG, "------------------------------------------------------------------");

        }
        analysed = true;
    }

    private void                        parseHeader(Element elem, Server server) {
        /**
         <div class="col-sm-11">
         <h1> [HTTPS] blog.valvin.fr <span class="small">(Tue, 31 Jul 2018 10:53:33 +0000)</span> </h1>
         </div>
         <div class="col-sm-1">
         <a class="btn btn-default" href="/https/blog.valvin.fr/refresh">Refresh</a>
         </div>
         </div>
         * */
        dateScan =  elem.getElementsByTag("span").outerHtml();
        Log.w(TAG, "Date" + elem.getElementsByTag("span").outerHtml());
    }

    private void                        parseHeaderServer(Element elem, Server server) {
        /**
         <div class="col-sm-12">
         <h2> <span class="label label-state-warning">E</span> 2001:bc8:4700:2200::1:c2b : 443 <span class="small">(blog.valvin.fr)</span></h2>
         </div>
         </div>
         * */
        Elements spans = elem.getElementsByTag("span");
        server.grade = spans.get(0).text();
        server.ip = spans.get(1).text();
        Log.i(TAG, "IPV6=>[" + server.ip + "] GRADE:" + server.grade);
    }

    private void                        parseWarningServer(Element elem, Server server) {
        Elements divs = elem.getElementsByTag("div");
        Log.d(TAG, "LES ANNONCES");
        for (Element div : divs) {
            server.annonces.add(new Annonce(div));
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

    private void                        parseBodyServer(Element elem, Server server) {
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


    private class                       Server {
        public String                   ip = "None";
        public ArrayList<CypherProto>   protos = new ArrayList<>();
        public ArrayList<Annonce>       annonces = new ArrayList<>();
        public String                   grade = "None";

    }

    private class           Annonce {
        public int          type;
        public String       msg;

        public              Annonce(Element elem) {
            String className = elem.className();
            if (className.contains("")) {
                type = GOOD;
                Log.d(TAG, elem.text());
            } else if (className.contains("")) {
                type = WARNING;
                Log.i(TAG, elem.text());
            } else {
                type = ERROR;
                Log.e(TAG, elem.text());
            }
            msg = elem.text();
        }
    }

    private class           CypherProto {
        String              name;
        String              KeyExchange[] = new String[2];
        String              Authentification[] = new String[2];
        String              Encryption[] = new String[4];
        String              MAC[] = new String[2];
        boolean             PFS;


        public              CypherProto(String html) {
            /**
             *
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
             */

        }

    }

}
