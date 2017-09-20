package su.sniff.cepter.Model.Pcap;

/**
 * Created by maxim on 20/09/2017.
 */

public class            DnsIntercept {
    public String       domainAsked;
    public String       domainSpoofed;
    public boolean      actif;

    public              DnsIntercept(String domainAsked, String domainIntecpted) {
        this.domainAsked = domainAsked;
        this.domainSpoofed = domainIntecpted;
    }

    public void         setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean      isActif() {
        return actif;
    }
}
