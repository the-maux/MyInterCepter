package fr.dao.app.Model.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
public class                            NmapParam {
    private String                      TAG = "NmapParam";
    private ArrayList<String>           mMenuCmd = new ArrayList<>(), mMenuCommandScript = new ArrayList<>();
    private Map<String, String>         mParamsForCmd = new HashMap<>(), mNmapParamsScript = new HashMap<>();
    private static NmapParam            mInstance = null;

    private NmapParam() {
        initMenuClassic();
        initMenuScript();
    }

    public static NmapParam             getInstance() {
        if (mInstance == null)
            mInstance = new NmapParam();
        return mInstance;
    }

    private void                        initMenuClassic() {
        mMenuCmd.add("Quick scan");
        mParamsForCmd.put(mMenuCmd.get(0), " -T4 -F -v ");
        mMenuCmd.add("Search services");
        mParamsForCmd.put(mMenuCmd.get(1), " -sV -v ");
        mMenuCmd.add("Regular scan");
        mParamsForCmd.put(mMenuCmd.get(2), " -PN -sS -sU -v " +
                "--script nbstat.nse,dns-service-discovery " +
                " " +
                "-p T:21,T:22,T:23,T:25,T:80,T:110,T:135,T:139,T:3128,T:443,T:445,T:2869,U:53,U:3031,U:5353  ");
        mMenuCmd.add("Quick scan");
        mParamsForCmd.put(mMenuCmd.get(3), " -T4 -A -v");
        mMenuCmd.add("Os fingerprint");
        mParamsForCmd.put(mMenuCmd.get(4), " -O -v ");
        mMenuCmd.add("Intrusive scan");
        mParamsForCmd.put(mMenuCmd.get(5), " -sS -sU -T4 -A -v -PE -PP -PS80,443 -PA3389 -PU40125 -PY -g 53 ");
        mMenuCmd.add("Light service discovery");
        mParamsForCmd.put(mMenuCmd.get(6), " -sV --version-intensity 5 ");
        mMenuCmd.add("Intense Scan");
        mParamsForCmd.put(mMenuCmd.get(7), " -T4 -A -v");
        mMenuCmd.add("Intense scan plus UDP");
        mParamsForCmd.put(mMenuCmd.get(8), " -sV -sS -sU -T4 -A -v ");
        mMenuCmd.add("Intense scan, all TCP ports");
        mParamsForCmd.put(mMenuCmd.get(9), " -sV -p 1-65535 -T4 -A -v ");
        mMenuCmd.add("Agressive service discovery");
        mParamsForCmd.put(mMenuCmd.get(10), " -sV --version-intensity 0 ");
        mMenuCmd.add("Ping scan");
        mParamsForCmd.put(mMenuCmd.get(11), " -sn -v ");
        mMenuCmd.add("Traceroute");
        mParamsForCmd.put(mMenuCmd.get(12), " -sn --traceroute -v ");
    }

    private void                        initMenuScript() {
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

    public String                       getParamTypeScan(String itemMenu) {
       return mParamsForCmd.get(itemMenu);
    }

    public String                       getNameTypeScan(int offset) {
        return mMenuCmd.get(offset);
    }

    public ArrayList<String>            getmMenuCmd() {
        return mMenuCmd;
    }

    public static int                   getFocusedScan(String focusedElem) {
        if (mInstance != null) {
            final CharSequence[] cmdItems = mInstance.mMenuCmd.toArray(new CharSequence[mInstance.mMenuCmd.size()]);
            int i = 0;
            for (; i < cmdItems.length; i++) {//To get actual focus
                if (cmdItems[i].toString().contains(focusedElem))
                    break;
            }
            return i;
        } else
            return 0;
    }
    public static int                   getFocusedScript(String focusedElem) {
        if (mInstance != null) {
            final CharSequence[] cmdItems = mInstance.mMenuCommandScript.toArray(new CharSequence[mInstance.mMenuCommandScript.size()]);
            int i = 0;
            for (; i < cmdItems.length; i++) {//To get actual focus
                if (cmdItems[i].toString().contains(focusedElem))
                    break;
            }
            return i;
        } else
            return 0;
    }
}
