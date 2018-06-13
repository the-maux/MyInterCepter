package fr.dao.app.Model.Net;

import java.util.ArrayList;

public class                HttpTrame {
    private String          dump;
    public int              offsett;

    public HttpTrame(ArrayList<String> buffer) {
        StringBuilder tmp = new StringBuilder("");
        for (String line : buffer) {
            tmp.append(line).append('\n');
        }
        dump = tmp.toString();
    }

    /* TODO parser la trame pour HOST / HEADER ETC*/

    public String           getDump() {
        return dump;
    }
}
