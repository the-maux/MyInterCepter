package fr.dao.app.Model.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Scan.ExploitScanner;
import fr.dao.app.Model.Target.Host;

import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.CouchDB;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.DNS;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.FTP;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.HTTP;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.IMAP;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.LDAP;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.MongoDB;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.MySQL;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.NTP;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.POP;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.SMB;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.SMTP;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.SSH;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.SSL;
import static fr.dao.app.Core.Scan.ExploitScanner.TypeScanner.TELNET;

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
    private static NmapParam            mInstance = null;
    private ArrayList<String>           mMenuCmd = new ArrayList<>(), mMenuCommandScript = new ArrayList<>();
    private Map<String, String>         mParamsForCmd = new HashMap<>(), mNmapParamsScript = new HashMap<>();
    private Map<ExploitScanner.TypeScanner, String> nmapNSE;

    private NmapParam() {
        initMenuClassic();
        initMenuScript();
        initMenuScriptVulnerability();
    }
    public static NmapParam             getInstance() {
        if (mInstance == null)
            mInstance = new NmapParam();
        return mInstance;
    }

    /**
     * Classic Menu scan
     */
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
    public ArrayList<String>            getmMenuCmd() {
        return mMenuCmd;
    }
    public String                       getNameTypeScan(int offset) {
        return mMenuCmd.get(offset);
    }
    public String                       getParamTypeScan(String itemMenu) {
        return mParamsForCmd.get(itemMenu);
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

    /**
     * Script Menu
     */
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

    /**
     *     * --Script =
     *              nbstat => U:137
     *              dns-service-discovery => U:5353
     *              upnp-info => U:1900
     *              Windows check => T:135 https://nmap.org/nsedoc/scripts/msrpc-enum.html msrpc
     *                            => T:445 microsoft-ds
     */
    public String                       getHostQuickDiscoverArgs() {
        return " -PN -sS -T3 -sU " +
                "--script nbstat.nse,dns-service-discovery,upnp-info " +
                "--min-parallelism 100 " +
                "-p " +
                "T:21,T:22,T:23,T:25,T:80,T:110,T:111,T:135,T:139,T:3128,T:443,T:445,T:2049,T:2869," +
                "U:53,U:1900,U:3031,U:5353 ";
    }
    public static String                buildHostCmdArgs(ArrayList<Host> hosts) {
        StringBuilder hostCmd = new StringBuilder("");
        for (Host host : hosts) {
            if (Singleton.getInstance().Settings.getUserPreferences().NmapMode > host.Deepest_Scan)
                hostCmd.append(" ").append(host.ip);//CleverScan, don't re-scan
        }
        return hostCmd.toString();
    }

    /**
     * VulnerabilityScan
     * nmap --script=broadcast-dns-service-discovery
     */
    public String                       getFullScanForVulns() {//TODO: dump all ports
        return " -PN -sS -sU " + /* Deleted but why not : -T3 */
                "-p " +
                "T:21,T:22,T:23,T:25,T:80,T:110,T:111,T:135,T:139,T:3128,T:443,T:445,T:2049,T:2869," +
                "U:53,U:1900,U:3031,U:5353 ";
    }
    private void                        initMenuScriptVulnerability() {
        nmapNSE = new HashMap<>();
        nmapNSE.put(FTP, "ftp-anon.nse,ftp-bounce.nse,ftp-brute.nse,ftp-libopie.nse,ftp-proftpd-backdoor.nse,ftp-vsftpd-backdoor.nse,ftp-vuln-cve2010-4221.nse");
        nmapNSE.put(SSH, "ssh2-enum-algos.nse,ssh-hostkey.nse,sshv1.nse");
        nmapNSE.put(TELNET, "telnet-brute.nse,telnet-encryption.nse");
        nmapNSE.put(SMTP, "smtp-brute.nse,smtp-commands.nse,smtp-enum-users.nse,smtp-open-relay.nse,smtp-strangeport.nse,smtp-vuln-cve2010-4344.nse,smtp-vuln-cve2011-1720.nse,smtp-vuln-cve2011-1764.nse");
        nmapNSE.put(DNS, "ns-blacklist.nse,dns-brute.nse,dns-cache-snoop.nse,dns-check-zone.nse,dns-client-subnet-scan.nse,dns-fuzz.nse,dns-ip6-arpa-scan.nse,dns-nsec3-enum.nse,dns-nsec-enum.nse,dns-nsid.nse,dns-random-srcport.nse,dns-random-txid.nse,dns-recursion.nse,dns-service-discovery.nse,dns-srv-enum.nse,dns-update.nse,dns-zeustracker.nse,dns-zone-transfer.nse");
        nmapNSE.put(HTTP, "http-adobe-coldfusion-apsa1301.nse,http-affiliate-id.nse,http-apache-negotiation.nse,http-auth-finder.nse,http-auth.nse,http-awstatstotals-exec.nse,http-axis2-dir-traversal.nse,http-backup-finder.nse,http-barracuda-dir-traversal.nse,http-brute.nse,http-cakephp-version.nse,http-chrono.nse,http-coldfusion-subzero.nse,http-comments-displayer.nse,http-config-backup.nse,http-cors.nse,http-date.nse,http-default-accounts.nse,http-domino-enum-passwords.nse,http-drupal-enum-users.nse,http-drupal-modules.nse,http-email-harvest.nse,http-enum.nse,http-exif-spider.nse,http-favicon.nse,http-fileupload-exploiter.nse,http-form-brute.nse,http-form-fuzzer.nse,http-frontpage-login.nse,http-generator.nse,http-git.nse,http-gitweb-projects-enum.nse,http-google-malware.nse,http-grep.nse,http-headers.nse,http-huawei-hg5xx-vuln.nse,http-icloud-findmyiphone.nse,http-icloud-sendmsg.nse,http-iis-webdav-vuln.nse,http-joomla-brute.nse,http-litespeed-sourcecode-download.nse,http-majordomo2-dir-traversal.nse,http-malware-host.nse,http-methods.nse,http-method-tamper.nse,http-open-proxy.nse,http-open-redirect.nse,http-passwd.nse,http-phpmyadmin-dir-traversal.nse,http-phpself-xss.nse,http-php-version.nse,http-plesk-backdoor.nse,http-proxy-brute.nse,http-put.nse,http-qnap-nas-info.nse,http-rfi-spider.nse,http-robots.txt.nse,http-robtex-reverse-ip.nse,http-robtex-shared-ns.nse,http-sitemap-generator.nse,http-slowloris-check.nse,http-slowloris.nse,http-sql-injection.nse,http-stored-xss.nse,http-title.nse,http-tplink-dir-traversal.nse,http-trace.nse,http-traceroute.nse,http-unsafe-output-escaping.nse,http-userdir-enum.nse,http-vhosts.nse,http-virustotal.nse,http-vlcstreamer-ls.nse,http-vmware-path-vuln.nse,http-vuln-cve2009-3960.nse,http-vuln-cve2010-0738.nse,http-vuln-cve2010-2861.nse,http-vuln-cve2011-3192.nse,http-vuln-cve2011-3368.nse,http-vuln-cve2012-1823.nse,http-vuln-cve2013-0156.nse,http-waf-detect.nse,http-waf-fingerprint.nse,http-wordpress-brute.nse,http-wordpress-enum.nse,http-wordpress-plugins.nse");
        nmapNSE.put(SSL, "ssl-cert.nse,ssl-date.nse,ssl-enum-ciphers.nse,ssl-google-cert-catalog.nse,ssl-known-keys.nse,sslv2.nse");
        nmapNSE.put(SMB, "smb-brute.nse,smb-check-vulns.nse,smb-enum-domains.nse,smb-enum-groups.nse,smb-enum-processes.nse,smb-enum-sessions.nse,smb-enum-shares.nse,smb-enum-users.nse,smb-flood.nse,smb-ls.nse,smb-mbenum.nse,smb-os-discovery.nse,smb-print-text.nse,smb-psexec.nse,smb-security-mode.nse,smb-server-stats.nse,smb-system-info.nse,smbv2-enabled.nse,smb-vuln-ms10-054.nse,smb-vuln-ms10-061.nse");
        nmapNSE.put(POP, "pop3-brute.nse,pop3-capabilities.nse");
        nmapNSE.put(NTP, "ntp-info.nse,ntp-monlist.nse");
        nmapNSE.put(IMAP, "imap-brute.nse,imap-capabilities.nse");
        nmapNSE.put(MySQL, "mysql-audit.nse,mysql-brute.nse,mysql-databases.nse,mysql-dump-hashes.nse,mysql-empty-password.nse,mysql-enum.nse,mysql-info.nse,mysql-query.nse,mysql-users.nse,mysql-variables.nse,mysql-vuln-cve2012-2122.nse");
        nmapNSE.put(MongoDB, "mongodb-brute.nse,mongodb-databases.nse,mongodb-info.nse");
        nmapNSE.put(CouchDB, "couchdb-databases.nse,couchdb-stats.nse");
        nmapNSE.put(LDAP, "ldap-brute.nse,ldap-novell-getpass.nse,ldap-rootdse.nse,ldap-search.nse");
    }
    public String                       getScriptForScanFromTypeScanner(ExploitScanner.TypeScanner scanner) {
        return nmapNSE.get(scanner);
    }
}
