package fr.dao.app.Core.Nmap;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.View.Scan.NmapOutputFragment;

/*
https://hackertarget.com/7-nmap-nse-scripts-recon/
    Utilisation: nmap [Type(s) de scan] [Options] {spécifications des cibles}

           -A Active la détection du système d'exploitation et des versions
           -O Active la détection d'OS
           -sS/sT/sA/sW/sM: Scans TCP SYN/Connect()/ACK/Window/Maimon
           -sU: Scan UDP
           -iL <inputfilename>: Lit la liste des hôtes/réseaux cibles à partir du fichier
           -sL: List Scan - Liste simplement les cibles à scanner
           -PN: Considérer tous les hôtes comme étant connectés -- saute l'étape de découverte des hôtes
           -sO: Scan des protocoles supportés par la couche IP
           -sT: TCP Connect scan
           -sV: Teste les ports ouverts pour déterminer le service en écoute et sa version
               --version-light: Limite les tests aux plus probables pour une identification plus rapide
               --version-intensity <niveau>: De 0 (léger) à 9 (tout essayer)
               --version-all: Essaie un à un tous les tests possibles pour la détection des versions
               --version-trace: Affiche des informations détaillées du scan de versions (pour débogage)
           -T[0-5]: Choisit une politique de temporisation (plus élevée, plus rapide)
           -D <decoy1,decoy2[,ME],...>: Obscurci le scan avec des leurres
           -F fast
           --spoof-mac <adresse MAC, préfixe ou nom du fabriquant>: Usurpe une adresse MAC
           --log-errors: Journalise les erreurs/alertes dans un fichier au format normal
           --packet-trace: Affiche tous les paquets émis et reçus
           -v: Rend Nmap plus verbeux (-vv pour plus d'effet)
           --badsum: Envoi des paquets TCP/UDP avec une somme de controle erronnée
           --scan-delay/--max-scan-delay <time>: Ajuste le delais entre les paquets de tests.
           -p <plage de ports>: Ne scanne que les ports spécifiés
               Exemple: -p22; -p1-65535; -pU:53,111,137,T:21-25,80,139,8080
 */
public class                        NmapControler {
    private String                  TAG = "NmapControler";
    private NmapControler           mInstance = this;
    private Singleton               mSingleton = Singleton.getInstance();
    private String                  PATH_NMAP = mSingleton.Settings.FilesPath + "nmap/nmap ";
    private boolean                 mIsLiveDump, isRunning;
    private NetworkDiscoveryControler mNnetworkDiscoveryControler;
    private Map<String, String>     mNmapParams;
    private ArrayList<String>       mMenuCommand;
    private String                  mActualItemMenu = "Ping scan";//Default
    private List<Host>              mHost = null;
    private Date                    startParsing;


    /*
    http://macvendors.co/api
    **   HostDiscoveryActivity
    * --Script =
     *              nbstat => U:137
     *              dns-service-discovery => U:5353
     *              upnp-info => U:1900
     *              Windows check => T:135 https://nmap.org/nsedoc/scripts/msrpc-enum.html msrpc
     *                            => T:445 microsoft-ds
    *
    */
    public                          NmapControler(ArrayList<Host> hosts, NetworkDiscoveryControler networkDiscoveryControler,
                                                  Network ap, Context context) {/* Parsing mode */
        String NMAP_ARG_SCAN = " -PN -sS -T3 -sU " +
                "--script nbstat.nse,dns-service-discovery,upnp-info " +
                "--min-parallelism 100 " +
                "-p T:21,T:22,T:23,T:25,T:80,T:110,T:111,T:135,T:139,T:3128,T:443,T:445,T:2049,T:2869," +
                "U:53,U:1900,U:3031,U:5353  ";
        mIsLiveDump = false;
        mNnetworkDiscoveryControler = networkDiscoveryControler;
        mActualItemMenu = "Basic Host discovery";
        StringBuilder hostCmd = new StringBuilder("");
        for (Host host : hosts) {
            if (mSingleton.Settings.getUserPreferences().NmapMode > host.Deepest_Scan)//To not scan again automaticaly already scanned host
                hostCmd.append(" ").append(host.ip);
        }
        String cmd = PATH_NMAP + NMAP_ARG_SCAN + hostCmd.toString();
        Log.d(TAG, "CMD:["+ cmd + "]");
        setTitleToolbar(null, "Scanning " + hostCmd.toString().split(" ").length + " devices");
        hostDiscoveryFromNmap(cmd, hosts, ap, context);
    }

    /*
    **   NmapActivity
    */
    public                          NmapControler(boolean execureAllCommandTogether) {/*Live mode*/
        initMenuOptionScan();
        mIsLiveDump = true;
    }

    private void                    hostDiscoveryFromNmap(final String cmd, final ArrayList<Host> hosts, final Network ap, final Context context) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String tmp;
                    StringBuilder outputBuilder = new StringBuilder();
                    BufferedReader reader = new RootProcess("Nmap", mSingleton.Settings.FilesPath)
                            .exec(cmd).getReader();
                    while ((tmp = reader.readLine()) != null && !tmp.startsWith("Nmap done")) {
                        outputBuilder.append(tmp).append('\n');
                    }
                    /*
                     * Hello dear, If you're here
                     * Trying to understand why the condition is
                     * outputBuilder.toString().isEmpty()
                     * I love you, thank you, for existing
                     * You're not alone, we are connected
                     */
                    if (outputBuilder.toString().isEmpty() || tmp.isEmpty() || !tmp.startsWith("Nmap done")) {
                        Log.d(TAG, "Error in nmap execution, Nmap didn't end");
                        outputBuilder.append(tmp);
                        Log.e(TAG, outputBuilder.toString());
                        setTitleToolbar("Network scan", "Nmap Error");
                        return;
                    }
                    outputBuilder.append(tmp);
                    String FullDUMP = outputBuilder.toString().substring(1);
                    Log.d(TAG, "\t\t LastLine[" + tmp + "]");
                    startParsing = Calendar.getInstance().getTime();
                    new NmapHostDiscoveryParser(mInstance, hosts, FullDUMP, ap, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void                    initMenuOptionScan() {
        mMenuCommand = new ArrayList<>();
        mNmapParams = new HashMap<>();
        mMenuCommand.add("Quick scan");
        mNmapParams.put(mMenuCommand.get(0), " -T4 -F -v ");
        mMenuCommand.add("Search services");
        mNmapParams.put(mMenuCommand.get(1), " -sV -v ");
        mMenuCommand.add("Regular scan");
        mNmapParams.put(mMenuCommand.get(2), " -PN -sS -sU -v " +
                "--script nbstat.nse,dns-service-discovery " +
                " " +
                "-p T:21,T:22,T:23,T:25,T:80,T:110,T:135,T:139,T:3128,T:443,T:445,T:2869,U:53,U:3031,U:5353  ");
        mMenuCommand.add("Quick scan");
        mNmapParams.put(mMenuCommand.get(3), " -T4 -A -v");
        mMenuCommand.add("Os fingerprint");
        mNmapParams.put(mMenuCommand.get(4), " -O -v ");
        mMenuCommand.add("Intrusive scan");
        mNmapParams.put(mMenuCommand.get(5), " -sS -sU -T4 -A -v -PE -PP -PS80,443 -PA3389 -PU40125 -PY -g 53 ");
        mMenuCommand.add("Light service discovery");
        mNmapParams.put(mMenuCommand.get(6), " -sV --version-intensity 5 ");
        mMenuCommand.add("Intense Scan");
        mNmapParams.put(mMenuCommand.get(7), " -T4 -A -v");
        mMenuCommand.add("Intense scan plus UDP");
        mNmapParams.put(mMenuCommand.get(8), " -sV -sS -sU -T4 -A -v ");
        mMenuCommand.add("Intense scan, all TCP ports");
        mNmapParams.put(mMenuCommand.get(9), " -sV -p 1-65535 -T4 -A -v ");
        mMenuCommand.add("Agressive service discovery");
        mNmapParams.put(mMenuCommand.get(10), " -sV --version-intensity 0 ");
        mMenuCommand.add("Ping scan");
        mNmapParams.put(mMenuCommand.get(11)," -sn -v " );
        mMenuCommand.add("Traceroute");
        mNmapParams.put(mMenuCommand.get(12), " -sn --traceroute -v ");
    }

    private void                    initMenuScript() {
        ArrayList<String> mMenuCommandScript = new ArrayList<>();
        Map<String, String> mNmapParamsScript = new HashMap<>();
        mMenuCommandScript.add("Nbstat");
        mNmapParamsScript.put(mMenuCommandScript.get(0), " -PN -T4 -sS -sU -v --script nbstat.nse " +
                "-p T:21,T:22,T:23,T:25,T:80,T:110,T:135,T:139,T:3128,T:443,T:445,U:53,U:3031,U:5353  ");
        mMenuCommandScript.add("");
        mNmapParamsScript.put(mMenuCommandScript.get(1), " -PN -T4 -sS -sU -v --script dns-service-discovery " +
                "-p T:21,T:22,T:23,T:25,T:80,T:110,T:135,T:139,T:3128,T:443,T:445,U:53,U:3031,U:5353  ");
        mMenuCommandScript.add("Heartbleed check");
        mNmapParamsScript.put(mMenuCommandScript.get(2), " -sV -p 443 –script=ssl-heartbleed.nse ");
        mMenuCommandScript.add("Samba search");
        mNmapParamsScript.put(mMenuCommandScript.get(3), " -sV --script=smb* ");
        mMenuCommandScript.add("Bruteforcing subdomaine");
        mNmapParamsScript.put(mMenuCommandScript.get(4), " -p 80 --script dns-brute.nse ");//TODO only title, not subtitle
        mMenuCommandScript.add("Geoloc traceroute");
        mNmapParamsScript.put(mMenuCommandScript.get(5), " --traceroute --script traceroute-geolocation.nse ");
        mMenuCommandScript.add("Agressive Http fingerprint");
        mNmapParamsScript.put(mMenuCommandScript.get(6), " --script http-enum ");
        mMenuCommandScript.add("Search for http hostname");
        mNmapParamsScript.put(mMenuCommandScript.get(7), " --script http-title -sV -p 80 ");
        mMenuCommandScript.add("Samba os discovery");
        mNmapParamsScript.put(mMenuCommandScript.get(8), " -p 445 --script smb-os-discovery");
        mMenuCommandScript.add("Http headers");
        mNmapParamsScript.put(mMenuCommandScript.get(9), " -sV --script=http-headers ");
    }

    private String                  buildCommand() {
        StringBuilder res = new StringBuilder("");
        if (mIsLiveDump) {
            for (Host host : mHost) {
                res.append(host.ip).append(" ");
            }
        }
        String hostFilter = res.toString();
        String parameter = getNmapParamFromMenuItem(mActualItemMenu);
        String cmd = PATH_NMAP + parameter + " " + hostFilter + " -d";
        return cmd.replace("  ", " ").replace("\n", "");
    }

    private String                  build(NmapOutputFragment nmapOutputFragment) {
        String cmd = buildCommand();
        Log.i(TAG, cmd);
        nmapOutputFragment.printCmdInTerminal(cmd
                .replace("nmap/nmap", "nmap")
                .replace(mSingleton.Settings.FilesPath, ""));
        return cmd;
    }

    public void                     start(final NmapOutputFragment nmapOutputFragment,
                                            final ProgressBar progressBar) {
        if (mHost == null) {
            Log.e(TAG, "No client selected when launched");
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String tmp;
                        StringBuilder dumpOutputBuilder = new StringBuilder();
                        BufferedReader reader = new RootProcess("Nmap", mSingleton.Settings.FilesPath)
                                .exec(build(nmapOutputFragment)).getReader();
                        while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                            if (!tmp.isEmpty()) {
                                if (tmp.charAt(0) == '\n')
                                    tmp = tmp.substring(1);
                                nmapOutputFragment.flushOutput(tmp + '\n', null);
                            }
                        }
                        dumpOutputBuilder.append(tmp);
                        Log.d(TAG, "Nmap final dump:" + dumpOutputBuilder.toString());
                        nmapOutputFragment.flushOutput(tmp + '\n', progressBar);
                    } catch (IOException e) {
                        e.printStackTrace();
                        nmapOutputFragment.flushOutput(null, progressBar);
                    }
                }
            }).start();
        }
    }

    public void                     onHostActualized(ArrayList<Host> hosts) {
        Log.d(TAG, "All node was parsed in :" + Utils.TimeDifference(startParsing));
        mNnetworkDiscoveryControler.onNmapScanOver(hosts);
    }

    public void                     setmActualItemMenu(String itemMenu) {
        this.mActualItemMenu = itemMenu;
    }

    public void                     setHosts(List<Host> hosts) {
        this.mHost = hosts;
    }

    public void                     setTitleToolbar(String title, String subtitle) {
        mNnetworkDiscoveryControler.setToolbarTitle(title, subtitle);
    }

    public String                   getActualCmd() {
        return mActualItemMenu;
    }

    public ArrayList<String>        getMenuCommmands() {
        return mMenuCommand;
    }

    public String                   getNmapParamFromMenuItem(String itemMenu) {
        return mNmapParams.get(itemMenu);
    }
}
