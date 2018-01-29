package fr.allycs.app.Controller.Core.Nmap;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Model.Target.ExternalHost;

public class                        ListNmap {
    private String                  TAG = "ListMap";
    private NmapControler           mNmapControler;
    private ListNmap                mInstance = this;
    private List<NmapParser>        parserList;
    private List<ExternalHost> externalHosts;
    private int                     parsingOver = 0;

    ListNmap(NmapControler nmapControler) {
        parserList = new ArrayList<>();
        externalHosts = new ArrayList<>();
        mNmapControler = nmapControler;
    }

    public boolean                  isLoadingOver() {
        return parsingOver == parserList.size();
    }

    void                            addParsing(NmapParser parser) {
        parserList.add(parser);
    }

    void                            nmapParsingOver(ExternalHost host, boolean parsingSucceed) {
        Log.d(TAG, host.ip + " just ended to parse");
        if (parsingSucceed)
            externalHosts.add(host);
        parsingOver = parsingOver + 1;
        if (parsingOver >= parserList.size()) {
            Log.d(TAG, "Nmap finished to parse " + parsingOver + " dump");
            mNmapControler.onParsingNmapOver(externalHosts);
        } else {
            Log.d(TAG, "Nmap parsing over [" + parsingOver + "/" + parserList.size() +"]");
        }
    }

}
