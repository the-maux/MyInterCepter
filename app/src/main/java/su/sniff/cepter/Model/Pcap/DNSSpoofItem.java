package su.sniff.cepter.Model.Pcap;

/**
 * Created by maxim on 20/09/2017.
 */

public class DNSSpoofItem extends MyObject{
    public String       domainAsked;
    public String       domainSpoofed;
    public boolean      actif;

    public DNSSpoofItem(String domainAsked, String domainIntecpted) {
        this.domainAsked = domainAsked;
        this.domainSpoofed = domainIntecpted;
    }

    public void         setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean      isActif() {
        return actif;
    }

    @Override
    public String       toString() {
        return domainAsked +":"+domainSpoofed;
    }
}
