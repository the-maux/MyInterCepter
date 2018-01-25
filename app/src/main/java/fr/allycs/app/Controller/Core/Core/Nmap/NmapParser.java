package fr.allycs.app.Controller.Core.Core.Nmap;

public class                NmapParser {
    private String          TAG = "";
    private NmapControler   mNmapControler;
    private String[]        mStdoutDump;

    public                  NmapParser(NmapControler nmapControler) {
        this.mNmapControler = nmapControler;
    }

    public void             parseStdout(String dumpOutputBuilder) {
        this.mStdoutDump = dumpOutputBuilder.split("\n");

    }
}
