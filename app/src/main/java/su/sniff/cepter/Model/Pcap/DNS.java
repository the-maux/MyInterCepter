package su.sniff.cepter.Model.Pcap;

import android.util.Log;

/**
 * Created by maxim on 11/08/2017.
 */

public class            DNS {
    public String       time;
    public String       ipSrc;
    public String       ipDst;
    public String       domain;
    public String       detailTrame;

    public              DNS(String line) {
        //TODO: bug on DNS parsing when not filtered on 'port 53' only and no -verbose
        String[] lineSub = line.split(" ");
        time = lineSub[0].substring(0, lineSub[0].indexOf("."));
        ipSrc = lineSub[17];
        ipDst = lineSub[19].replace(".domain:", "");
        domain = lineSub[25];
        detailTrame = lineSub[1] + lineSub[2] + lineSub[3] + lineSub[4] +//jss fatigué, flem
                lineSub[5] +lineSub[6] +lineSub[7] +lineSub[8] +lineSub[9] +lineSub[10] +
                lineSub[11] +lineSub[12] +lineSub[13] +lineSub[14] +lineSub[15] +lineSub[16];
    }
}
