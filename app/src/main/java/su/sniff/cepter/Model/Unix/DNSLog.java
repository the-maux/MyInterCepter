package su.sniff.cepter.Model.Unix;

import su.sniff.cepter.R;

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
    public int      color;

    public          DNSLog(String line) {
        String[] splitted = line.split(" ");
        switch (splitted[0]) {
            case "query[A]":
                color = R.color.material_green_600;
                data = line.substring("query[A]".length()+1, line.length());
                DNSType = Type.Query;
                break;
            case "forwarded":
                color = R.color.material_amber_700;
                data = line.substring("forwarded".length()+1, line.length());
                DNSType = Type.Forward;
                break;
            case "reply":
                color = R.color.material_cyan_700;
                data = line.substring("reply".length()+1, line.length());
                DNSType = Type.Reply;
                break;
            default:
                color = R.color.material_light_white;
                data = line;
                DNSType = Type.Other;
                break;
        }
        host = splitted[1];
        data = line;
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


'onClick'
 */