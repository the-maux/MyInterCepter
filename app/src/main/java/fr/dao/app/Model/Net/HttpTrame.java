package fr.dao.app.Model.Net;

import android.util.Log;

import java.util.ArrayList;

public class                HttpTrame {
    private String          TAG = "HttpTrame";
    public String           host = "No Info";
    public String           request = "No Info";
    public String           userAgent = "No Info";
    public ArrayList<String> keys = new ArrayList<>(), values = new ArrayList<>();
    public String           dump = "No Info";
    public int              offsett;
    public String           cookie = "No Info";


    public HttpTrame(ArrayList<String> buffer) {
        Log.d(TAG, "request:[" + buffer.get(0) +"]");
        request = buffer.get(1).substring(39, buffer.get(1).length());
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
