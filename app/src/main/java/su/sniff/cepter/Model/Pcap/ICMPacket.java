package su.sniff.cepter.Model.Pcap;

public class ICMPacket {
    private byte[]      header; // size of 8
    private byte        type;
    private byte        code; // 0 ECHO answer, 8 ECHO request
                              // 3 Can't reach, 4 Data too big
                              // 11 Expiration du TTL, 12 Problème header
                              // 17 Demande de masque de sous-réseau, 18 Réponse à une demande de masque de sous-réseau
    private byte[]      checksum;
    private byte[]      id; // used for ping
    private byte[]      sequence; // used for ping
    private byte[]      data;

    public ICMPacket(int type) {
        this.header = new byte[8];
        this.type = 0x08;
        this.code = 0x00;
        this.checksum = new byte[2];
        this.checksum[0] = 0x00;
        this.checksum[1] = 0x08;
        //this.id = new byte[2];
        //this.sequence = new byte[2];
        //this.data = new byte[1];
    }

}
