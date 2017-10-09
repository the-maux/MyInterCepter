package su.sniff.cepter.Model.Target;

/**
 * Created by maxim on 09/10/2017.
 */

public class        DNSLog {
    public enum Type {
        Query,
        Forward,
        Reply,
        Other
    }
    public Type     DNSType;
    public String   host;
    public String   data;

    public          DNSLog(String line) {
        String[] splitted = line.split(" ");
        DNSType = getType(splitted[0]);
        host = splitted[1];
        data = line;
    }

    private Type    getType(String type) {
        switch (type) {
            case "query[A]":
                return Type.Query;
            case "forwarded":
                return Type.Forward;
            case "reply":
                return Type.Reply;
            default:
                return Type.Other;
        }
    }
}
/*
reply uhf.microsoft.com.edgekey.net is <CNAME>
reply e11095.dspg.akamaiedge.net is 80.239.244.109
reply www.microsoft.com is <CNAME>
reply e1863.dspb.akamaiedge.net is 2.20.202.119
reply web.vortex.data.microsoft.com is <CNAME>
reply geo.vortex.data.microsoft.com.akadns.net is <CNAME>
query[A] microsoftwindows.112.2o7.net from 192.168.0.29
forwarded microsoftwindows.112.2o7.net to 8.8.8.8
query[A] assets.onestore.ms from 192.168.0.29
forwarded assets.onestore.ms to 8.8.8.8
reply microsoftwindows.112.2o7.net is 66.235.139.19
reply assets.onestore.ms is <CNAME>
reply assets.onestore.ms.akadns.net is <CNAME>
reply assets.onestore.ms.edgekey.net is <CNAME>
reply e10583.dspg.akamaiedge.net is 23.214.140.177
query[A] arc.msn.com from 192.168.0.12
reply arc.msn.com is <CNAME>
query[A] auth.gfx.ms from 192.168.0.29
forwarded auth.gfx.ms to 8.8.8.8
reply auth.gfx.ms is <CNAME>
reply authgfx.msa.akadns6.net is <CNAME>
reply msagfx.live.com-6.edgekey.net is <CNAME>
reply e13551.dscg.akamaiedge.net is 23.214.170.172
 */