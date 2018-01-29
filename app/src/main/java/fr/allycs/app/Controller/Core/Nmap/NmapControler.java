package fr.allycs.app.Controller.Core.Nmap;

import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.Model.Target.ExternalHost;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.Scan.NmapOutputFragment;

/*
    Utilisation: nmap [Type(s) de scan] [Options] {spécifications des cibles}

           -A Active la détection du système d'exploitation et des versions
           -O Active la détection d'OS
           -sS/sT/sA/sW/sM: Scans TCP SYN/Connect()/ACK/Window/Maimon
           -sU: Scan UDP
           -iL <inputfilename>: Lit la liste des hôtes/réseaux cibles à partir du fichier
           -sL: List Scan - Liste simplement les cibles à scanner
           -PN: Considérer tous les hôtes comme étant connectés -- saute l'étape de découverte des hôtes
           -sO: Scan des protocoles supportés par la couche IP
           -sV: Teste les ports ouverts pour déterminer le service en écoute et sa version
               --version-light: Limite les tests aux plus probables pour une identification plus rapide
               --version-intensity <niveau>: De 0 (léger) à 9 (tout essayer)
               --version-all: Essaie un à un tous les tests possibles pour la détection des versions
               --version-trace: Affiche des informations détaillées du scan de versions (pour débogage)
           -T[0-5]: Choisit une politique de temporisation (plus élevée, plus rapide)
           -D <decoy1,decoy2[,ME],...>: Obscurci le scan avec des leurres
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
    private boolean                 mIsLiveDump;
    private boolean                 mIsOneByOnExecuted = false;

    private Map<String, String>     mNmapParams;
    private ArrayList<String>       mMenuCommand;
    private String                  mActualItemMenu = "Ping scan";//Default
    private List<Host>              mHost = null;
    private String                  mExternalHostToScan = null;
    private NmapParser              mNmapParser = null;


    public                          NmapControler(List<String> ips) {/* Parsing mode */
        mIsLiveDump = false;
        mIsOneByOnExecuted = false;
        mActualItemMenu = "Basic Host discovery";
        ListNmap listNmap = new ListNmap(this);
        for (String ip : ips) {

            mExternalHostToScan = ip.replace(":", "");
            //Log.d(TAG, "Scanning : [" + ip.replace(":", "") + "]");
            final NmapParser parser = new NmapParser(listNmap, ip.replace(":", ""));
            listNmap.addParsing(startAsParse(parser));
        }
    }

    public                          NmapControler(boolean execureAllCommandTogether) {/*Live mode*/
        Log.d(TAG, "Nmap STDOUT LIVE MODE");
        initMenu();
        mIsLiveDump = true;
        mIsOneByOnExecuted = execureAllCommandTogether;
    }

    private void                    initMenu() {
        mMenuCommand = new ArrayList<>();
        mNmapParams = new HashMap<>();
        mMenuCommand.add("Ping scan");
        mNmapParams.put(mMenuCommand.get(0), " -sn");
        mMenuCommand.add("Quick scan");
        mNmapParams.put(mMenuCommand.get(1), " -T4 -F");
        mMenuCommand.add("Quick scan plus");
        mNmapParams.put(mMenuCommand.get(2), " -sV -T4 -O -F --version-light");
        mMenuCommand.add("Quick traceroute");
        mNmapParams.put(mMenuCommand.get(3), " -sn --traceroute");
        mMenuCommand.add("Regular scan");
        mNmapParams.put(mMenuCommand.get(4), " ");
        mMenuCommand.add("Intrusive scan");
        mNmapParams.put(mMenuCommand.get(5), " -sS -sU -T4 -A -v -PE -PP -PS80,443 -PA3389 -PU40125 -PY -g 53 ");
        mMenuCommand.add("Intense Scan");
        mNmapParams.put(mMenuCommand.get(6), " -T4 -A -v");
        mMenuCommand.add("Intense scan plus UDP");
        mNmapParams.put(mMenuCommand.get(7), " -sS -sU -T4 -A -v");
        mMenuCommand.add("Intense scan, all TCP ports");
        mNmapParams.put(mMenuCommand.get(8), " -p 1-65535 -T4 -A -v");
        mMenuCommand.add("Intense scan, no ping");
        mNmapParams.put(mMenuCommand.get(9), " -T4 -A -v -Pn ");
        mMenuCommand.add("Basic Host discovery");
        mNmapParams.put(mMenuCommand.get(10), " -O -A -v ");

    }

    public ArrayList<String>        getMenuCommmands() {
        return mMenuCommand;
    }

    public String                   getNmapParamFromMenuItem(String itemMenu) {
        return mNmapParams.get(itemMenu);
    }

    private String                  buildHostFilterCommand() {
        StringBuilder res = new StringBuilder("");
        if (mIsLiveDump) {
            for (Host host : mHost) {
                res.append(host.ip).append(" ");
            }
        } else {
            return mExternalHostToScan;
        }
        return res.toString();
    }

    private String                  buildCommand(NmapParser parser) {
        String Binary = mSingleton.FilesPath + "nmap/nmap ";
        String parameter = ((parser == null) ? getNmapParamFromMenuItem(mActualItemMenu) : " -O -A -v ");
        String hostFilter = ((parser == null) ? buildHostFilterCommand() : parser.TARGETED_IP + " ");
        return Binary + parameter + " " + hostFilter;
    }

    public void                     startAsLive(final NmapOutputFragment nmapOutputFragment,
                                            final ProgressBar progressBar) {
        if (mHost == null && mExternalHostToScan == null) {
            Log.e(TAG, "No client selected when launched");
        } else {
            final String cmd = buildCommand(null).replace("\n", "")
                                             .replace("  ", " ");
            String trimmed_cmd = cmd
                    .replace("nmap/nmap", "nmap")
                    .replace(mSingleton.FilesPath, "");
            Log.d(TAG, cmd);
            nmapOutputFragment.printCmdInTerminal(trimmed_cmd);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String tmp;
                        StringBuilder dumpOutputBuilder = new StringBuilder();
                        BufferedReader reader = new RootProcess("Nmap", mSingleton.FilesPath)
                                .exec(cmd).getReader();
                        while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                            if (tmp.charAt(0) == '\n')
                                tmp = tmp.substring(1);
                            nmapOutputFragment.flushOutput(tmp + '\n', progressBar);
                        }
                        dumpOutputBuilder.append(tmp);
                        Log.d(TAG, "Nmap final dump:" + dumpOutputBuilder.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    private NmapParser              startAsParse(final NmapParser parser) {
        if (mHost == null && mExternalHostToScan == null) {
            Log.e(TAG, "No client selected when launched");
        } else {

            final String cmd = buildCommand(parser).replace("\n", "")
                    .replace("  ", " ");
            String trimmed_cmd = cmd
                    .replace("nmap/nmap", "nmap")
                    .replace(mSingleton.FilesPath, "");
        //    Log.d(TAG, trimmed_cmd);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String tmp;
                        StringBuilder dumpOutputBuilder = new StringBuilder();
                        BufferedReader reader = new RootProcess("Nmap", mSingleton.FilesPath)
                                .exec(cmd).getReader();
                        while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                            dumpOutputBuilder.append(tmp).append('\n');
                        }
                        dumpOutputBuilder.append(tmp);
                        parser.parseStdout(dumpOutputBuilder.toString().substring(1));
                        Log.d(TAG, "Nmap final dump:" + dumpOutputBuilder.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return parser;
        }
        return null;
    }

    public void                     onParsingNmapOver(List<ExternalHost> externalHosts) {
        Log.i(TAG, "ALL NMAP PARSING OVER, WE CAN CLOSE");
    }

    public void                     setmActualItemMenu(String itemMenu) {
        this.mActualItemMenu = itemMenu;
    }

    public void                     setHosts(List<Host> hosts) {
        this.mHost = hosts;
    }

    public boolean                  ismOneByOnExecuted() {
        return mIsOneByOnExecuted;
    }
}
