package fr.dao.app.Model.Config;

import android.util.Log;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class                        CryptCheckModel {
    private String                  TAG = "CryptCheckModel";
    public String                   domain = "None";
    public String                   dateScan = "None";
    public static final int         GOOD = 0x01, WARNING = 0x02, ERROR = 0x03;
    public ArrayList<Server>        servers = new ArrayList<>();

    public                          CryptCheckModel(Elements elems) {
        int a = 0;
        Log.w(TAG, "Il y a " + elems.size() + " tag dans cette page");
        Element header = elems.get(0);
        parseHeader(header);
        for (int i = 1; i < elems.size(); i = i +2) {
            Server server = new Server();
            Element headerServer = elems.get(i);
            parseHeaderServer(headerServer, server);
            Elements tmp = elems.get(i+1).children();
            if (!tmp.outerHtml().contains("<h3><span class=\"translation_missing\" title=\"translation missing: en.Checks\">Checks</span></h3>")) {
                parseBodyServer(tmp, server);
            } else {
                parseWarningServer(tmp.get(0).children(), server);
                parseBodyServer(elems.get(i+2).children(), server);
                i = i+ 1;
            }
            servers.add(server);
            Log.d(TAG, "------------------------------------------------------------------");

        }
    }

    private void                    parseHeader(Element elem) {
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

    private void                    parseHeaderServer(Element elem, Server server) {
        /**
         <div class="col-sm-12">
         <h2> <span class="label label-state-Warning">E</span> 2001:bc8:4700:2200::1:c2b : 443 <span class="small">(blog.valvin.fr)</span></h2>
         </div>
         </div>
         * */
        Elements spans = elem.getElementsByTag("span");
        server.grade = spans.get(0).text();
        server.ip = spans.get(1).text();
        Log.i(TAG, "IPV6=>[" + server.ip + "] GRADE:" + server.grade);
    }

    private void                    parseWarningServer(Elements divs, Server server) {
        Log.d(TAG, "LES ANNONCES");
        for (Element div : divs) {
            server.annonces.add(new Annonce(div));
        }
        /**
         *
         *
         <div class="row">
         <div class="col-sm-12">
         <div class="alert alert-Warning">
            This server doesn't support HSTS
         </div>
         <div class="alert alert-Warning">
            This server supports TLSv1.1
         </div>
         <div class="alert alert-Warning">
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
         <th><span class="label label-state-Critical">Critical</span></th>
         <td><span class="label label-success">mdc2_sign</span> <span class="label label-success">md2_sign</span> <span class="label label-success">md4_sign</span> <span class="label label-success">md5_sign</span> <span class="label label-success">sha_sign</span> <span class="label label-success">sha1_sign</span> <span class="label label-success">rsa</span> <span class="label label-success">dh</span> <span class="label label-success">sslv2</span> <span class="label label-success">sslv3</span> <span class="label label-success">dss</span> <span class="label label-success">anonymous</span> <span class="label label-success">null</span> <span class="label label-success">export</span> <span class="label label-success">des</span> <span class="label label-success">md5</span> <span class="label label-success">rc4</span> <span class="label label-success">sweet32</span></td>
         </tr>
         <tr>
         <th><span class="label label-state-error">error</span></th>
         <td><span class="label label-success">rsa</span> <span class="label label-success">dh</span> <span class="label label-success">tlsv1_0</span> <span class="label label-success">pfs</span></td>
         </tr>
         <tr>
         <th><span class="label label-state-Warning">Warning</span></th>
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

    private void                    parseBodyServer(Elements divs, Server server) {
        Elements table = divs.get(0).getElementsByClass("table");
        Element tbody = table.get(0).child(1);
        for (Element element : tbody.children()) {
            try {
                if (element.children().size() == 1) {
                    server.protos.add(new CypherProto(element, true));
                } else {
                    server.protos.add(new CypherProto(element, false));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in boy building");
                e.printStackTrace();
            }
        }
    }



    public class        Server {
        public String                   ip = "None";
        public ArrayList<CypherProto>   protos = new ArrayList<>();
        public ArrayList<Annonce>       annonces = new ArrayList<>();
        public String                   grade = "None";

    }

    public class        Annonce {
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

    public class        CypherProto {
        public final static int color_critic = 0x1, color_warning = 0x1, color_global = 0x1, color_good = 0x1;
        public String   name;
        public String   KeyExchange[] = new String[2];
        public String   Authentification[] = new String[2];
        public String   Encryption[] = new String[4];
        public String   MAC[] = new String[2];
        public boolean  PFS;
        public boolean  isNameTLS;

        public              CypherProto(Element element, boolean isTLSName) {
            if (isTLSName) {
                this.isNameTLS = true;
                name = element.text();
            } else {
                this.isNameTLS = false;
                for (int i = 0; i < element.children().size(); i++) {
                    switch (i) {
                        case 0:
                            this.name = element.children().get(i).text();
                            break;
                        case 1:
                            this.KeyExchange[0] = element.children().get(i).text();
                            break;
                        case 2:
                            this.KeyExchange[1] = element.children().get(i).text();
                            break;
                        case 3:
                            this.Authentification[0] = element.children().get(i).text();
                            break;
                        case 4:
                            this.Authentification[1] = element.children().get(i).text();
                            break;
                        case 5:
                            this.Encryption[0] = element.children().get(i).text();
                            break;
                        case 6:
                            this.Encryption[1] = element.children().get(i).text();
                            break;
                        case 7:
                            this.Encryption[2] = element.children().get(i).text();
                            break;
                        case 8:
                            this.Encryption[3] = element.children().get(i).text();
                            break;
                        case 9:
                            this.MAC[0] = element.children().get(i).text();
                            break;
                        case 10:
                            this.MAC[1] = element.children().get(i).text();
                            break;
                        case 11:
                            PFS = !element.children().get(i).text().contains("No PFS");
                            break;
                    }
                }
            }
        }
    }




/*
    4 rouge
    3 orange
    2 jaune
    1 green
    0 blanc

 */
}
