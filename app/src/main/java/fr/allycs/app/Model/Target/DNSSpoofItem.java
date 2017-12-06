package fr.allycs.app.Model.Target;

public class            DNSSpoofItem extends MyObject {
    public String       ip;
    public String       domain;

    private boolean     mActif;

    public DNSSpoofItem(String ip, String domain) {
        this.ip = ip;
        this.domain = domain.replace("www.", "");
    }

    public void         setActif(boolean actif) {
        this.mActif = actif;
    }
    public boolean      isActif() {
        return mActif;
    }

    @Override
    public String       toString() {
        return ip + ":" + domain;
    }
}
