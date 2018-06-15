package fr.dao.app.Model.Net;

import android.util.Log;

import java.security.PublicKey;
import java.util.ArrayList;

import static fr.dao.app.Model.Net.HttpTrame.typeOfRequest.DELETE;
import static fr.dao.app.Model.Net.HttpTrame.typeOfRequest.GET;
import static fr.dao.app.Model.Net.HttpTrame.typeOfRequest.POST;
import static fr.dao.app.Model.Net.HttpTrame.typeOfRequest.PUT;
import static fr.dao.app.Model.Net.HttpTrame.typeOfRequest.RESSOURCE;
import static fr.dao.app.Model.Net.HttpTrame.typeOfRequest.UNKNOW;

public class                HttpTrame {
    private String          TAG = "HttpTrame";
    public String           host = "No Info";
    public String           request = "No Info";
    public String           userAgent = "No Info";
    public ArrayList<String> keys = new ArrayList<>(), values = new ArrayList<>();
    public String           dump = "No Info";
    public int              offsett;
    public String           cookie = "No Info";
    public typeOfRequest    type = typeOfRequest.UNKNOW;
    public boolean          importante = false;
    public enum             typeOfRequest {
        GET, POST, PUT, DELETE, UNKNOW, RESSOURCE
    }

    public HttpTrame(ArrayList<String> buffer) {
        Log.d(TAG, "request:[" + buffer.get(0) +"]");
        request = buffer.get(1).substring(39, buffer.get(1).length()).replace("HTTP/1.1", "");
        StringBuilder tmp = new StringBuilder("");
        for (String line : buffer) {
            tmp.append(line).append('\n');
            if (line.contains("Host:"))
                initHost(line);
            else if (line.contains("Cookie:"))
                initCookie(line);
            else if (line.contains("User-Agent:"))
                initUserAgent(line);
            else {
                if (line.contains(":")) {
                    keys.add(line.substring(0, line.indexOf(':')));
                    values.add(line.substring(line.indexOf(':') + 1, line.length()));
                    Log.d(TAG, "[" + line.substring(0, line.indexOf(':')) + "] -> [" + line.substring(line.indexOf(':') + 1, line.length()) + "]");
                }
            }
        }
        dump = tmp.toString();
        analyse();
    }

    private void            analyse() {
        if (request.contains(".js") || request.contains(".css"))
            type = RESSOURCE;
        else if (request.contains("GET"))
            type = GET;
        else if (request.contains("POST"))
            type = POST;
        else if (request.contains("PUT"))
            type = PUT;
        else if (request.contains("DELETE"))
            type = DELETE;
        else if (!request.contains("/"))
            type = UNKNOW;
    }

    private void            initUserAgent(String line) {
        userAgent = line.substring(line.indexOf("User-Agent:"), line.length());
    }

    private void            initCookie(String line) {
        cookie = line.replace("Cookie","");
    }

    private void            initHost(String line) {
        host = line.replace("Host:", "");
    }

    /* TODO parser la trame pour HOST / HEADER ETC*/

    public String           getDump() {
        return dump;
    }

}
