package fr.allycs.app.Controller.Core.Nmap;

import android.util.Log;

import java.io.IOError;

import fr.allycs.app.Controller.Network.Fingerprint;
import fr.allycs.app.Model.Target.ExternalHost;

public class                NmapParser {
    private String          TAG = "";
    private ListNmap        mListNmapParser;
    private String[]        mStdoutDump;
    private boolean         mIsLoading = false;
    private ExternalHost    host;
    public String           TARGETED_IP;

    NmapParser(ListNmap listNmapParser, String ip) {
        this.mListNmapParser = listNmapParser;
        host = new ExternalHost();
        TARGETED_IP = ip;
        host.ip = ip;
    }

    void                    parseStdout(String nmapStdout) {
        try {
            mIsLoading = true;
            dumpNmap(nmapStdout);
            this.mStdoutDump = nmapStdout.split("\n");
            for (int i = 0; i < this.mStdoutDump.length; i++) {
                String line = this.mStdoutDump[i];
                if (line.contains("MAC Address")) {
                    parseMacAddress(line.replace("MAC Address: ", "").split(" "));
                } else if (line.contains("Device type:")) {
                    host.deviceType = line.replace("Device type: ", "");
                } else if (line.contains("Running")) {
                    parseOs(line.replace("Running: ", ""));
                } else if (line.contains("Too many fingerprints match this host to give specific OS details")) {
                    host.TooManyFingerprintMatchForOs = true;
                } else if (line.contains("STATE SERVICE")) {
                    parsePortList(line, i);
                }
            }
            host.dumpInfo = nmapStdout;
            mIsLoading = false;
            Fingerprint.initExternalHost(host);
            host.dumpMe();
            mListNmapParser.nmapParsingOver(host, true);
        } catch (IOError exception) {
            exception.getStackTrace();
            mListNmapParser.nmapParsingOver(host, false);
        }
    }


    private void        dumpNmap(String nmapStdout) {
        Log.d(TAG, "NMAP DUMP:");
        for (String line: nmapStdout.split("\n")) {
            Log.d(TAG, "\t" + line);
        }
    }

    private void        parsePortList(String line, int i) {

    }

    private void        parseMacAddress(String[] line) {
        host.mac = line[0];
        host.vendor = line[1];
    }

    private void        parseOs(String line) {
        String OsDetail = getLineContaining("OS details");
        if (OsDetail != null)
            host.osDetail = OsDetail;
        host.os = line;
    }

    private String      getLineContaining(String substring) {
        for (String line : this.mStdoutDump) {
            if (line.contains(substring))
                return line;
        }
        return null;
    }
}
