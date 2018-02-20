package fr.allycs.app.Core.Network;

import android.support.v4.view.MotionEventCompat;

import java.util.ArrayList;
import java.util.List;

public class                        IPv4Utils {
    private int                     baseIPnumeric;
    private int                     netmaskNumeric;

    public                          IPv4Utils(NetworkInformation network) throws NumberFormatException {
        String[] st = network.myIp.split("\\.");
        String netmask = network.netmask;
        if (st.length != 4) {
            throw new NumberFormatException("Invalid IP address: " + network.myIp);
        }
        int i = 24;
        this.baseIPnumeric = 0;
        for (String parseInt : st) {
            int value = Integer.parseInt(parseInt);
            if (value != (value & MotionEventCompat.ACTION_MASK)) {
                throw new NumberFormatException("Invalid IP address: " + network.myIp);
            }
            this.baseIPnumeric += value << i;
            i -= 8;
        }
        st = netmask.split("\\.");
        if (st.length != 4) {
            throw new NumberFormatException("Invalid netmask address: " + netmask);
        }
        i = 24;
        this.netmaskNumeric = 0;
        if (Integer.parseInt(st[0]) < MotionEventCompat.ACTION_MASK) {
            throw new NumberFormatException("The first byte of netmask can not be less than 255");
        }
        for (String parseInt2 : st) {
            int value = Integer.parseInt(parseInt2);
            if (value != (value & MotionEventCompat.ACTION_MASK)) {
                throw new NumberFormatException("Invalid netmask address: " + netmask);
            }
            this.netmaskNumeric += value << i;
            i -= 8;
        }
        boolean encounteredOne = false;
        int ourMaskBitPattern = 1;
        for (i = 0; i < 32; i++) {
            if ((this.netmaskNumeric & ourMaskBitPattern) != 0) {
                encounteredOne = true;
            } else if (encounteredOne) {
                throw new NumberFormatException("Invalid netmask: " + netmask + " (bit " + (i + 1) + ")");
            }
            ourMaskBitPattern <<= 1;
        }
    }

    private String                  convertNumericIpToSymbolic(Integer ip) {
        StringBuffer sb = new StringBuffer(15);
        for (int shift = 24; shift > 0; shift -= 8) {
            sb.append(Integer.toString((ip  >>> shift) & MotionEventCompat.ACTION_MASK));
            sb.append('.');
        }
        sb.append(Integer.toString(ip  & MotionEventCompat.ACTION_MASK));
        return sb.toString();
    }

    public List<String>             getAvailableIPs(Integer numberofIPs) {
        ArrayList result = new ArrayList();
        int numberOfBits = 0;
        while (numberOfBits < 32 && (this.netmaskNumeric << numberOfBits) != 0) {
            numberOfBits++;
        }
        Integer numberOfIPs = 0;
        for (int n = 0; n < 32 - numberOfBits; n++) {
            numberOfIPs = numberOfIPs << 1 | 1;
        }
        Integer baseIP = this.baseIPnumeric & this.netmaskNumeric;
        int i = 1;
        while (i < numberOfIPs  && i < numberofIPs ) {
            result.add(convertNumericIpToSymbolic(baseIP + i));
            i++;
        }
        return result;
    }

    public Integer                  getNumberOfHosts() {
        int numberOfBits = 0;
        while (numberOfBits < 32 && (this.netmaskNumeric << numberOfBits) != 0) {
            numberOfBits++;
        }
        Double x = Math.pow(2.0d, (double) (32 - numberOfBits));
        if (x == -1.0d) {
            x = 1.0d;
        }
        return x.intValue();
    }
}
