package su.sniff.cepter.Controller.Network;

import android.support.v4.view.MotionEventCompat;
import su.sniff.cepter.BuildConfig;

import java.util.ArrayList;
import java.util.List;

public class                    IPv4CIDR {
    int                             baseIPnumeric;
    int                             netmaskNumeric;

    public                          IPv4CIDR(String symbolicIP, String netmask) throws NumberFormatException {
        String[] st = symbolicIP.split("\\.");
        if (st.length != 4) {
            throw new NumberFormatException("Invalid IP address: " + symbolicIP);
        }
        int i = 24;
        this.baseIPnumeric = 0;
        for (String parseInt : st) {
            int value = Integer.parseInt(parseInt);
            if (value != (value & MotionEventCompat.ACTION_MASK)) {
                throw new NumberFormatException("Invalid IP address: " + symbolicIP);
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

    public                          IPv4CIDR(String IPinCIDRFormat) throws NumberFormatException {
        String[] st = IPinCIDRFormat.split("\\/");
        if (st.length != 2) {
            throw new NumberFormatException("Invalid CIDR format '" + IPinCIDRFormat + "', should be: xx.xx.xx.xx/xx");
        }
        String symbolicIP = st[0];
        Integer numericCIDR = new Integer(st[1]);
        if (numericCIDR  > 32) {
            throw new NumberFormatException("CIDR can not be greater than 32");
        }
        st = symbolicIP.split("\\.");
        if (st.length != 4) {
            throw new NumberFormatException("Invalid IP address: " + symbolicIP);
        }
        int i = 24;
        this.baseIPnumeric = 0;
        for (String parseInt : st) {
            int value = Integer.parseInt(parseInt);
            if (value != (value & MotionEventCompat.ACTION_MASK)) {
                throw new NumberFormatException("Invalid IP address: " + symbolicIP);
            }
            this.baseIPnumeric += value << i;
            i -= 8;
        }
        if (numericCIDR < 8) {
            throw new NumberFormatException("Netmask CIDR can not be less than 8");
        }
        this.netmaskNumeric = -1;
        this.netmaskNumeric <<= 32 - numericCIDR;
    }

    public String                   getIP() {
        return convertNumericIpToSymbolic(this.baseIPnumeric);
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

    public boolean                  contains(String IPaddress) {
        Integer checkingIP = 0;
        String[] st = IPaddress.split("\\.");
        if (st.length != 4) {
            throw new NumberFormatException("Invalid IP address: " + IPaddress);
        }
        int i = 24;
        for (String parseInt : st) {
            int value = Integer.parseInt(parseInt);
            if (value != (value & MotionEventCompat.ACTION_MASK)) {
                throw new NumberFormatException("Invalid IP address: " + IPaddress);
            }
            checkingIP = checkingIP + (value << i);
            i -= 8;
        }
        if ((this.baseIPnumeric & this.netmaskNumeric) == (checkingIP  & this.netmaskNumeric)) {
            return true;
        }
        return false;
    }

    public boolean                  contains(IPv4CIDR child) {
        Integer subnetID = child.baseIPnumeric;
        Integer subnetMask = child.netmaskNumeric;
        if ((subnetID  & this.netmaskNumeric) == (this.baseIPnumeric & this.netmaskNumeric)) {
            if ((this.netmaskNumeric < subnetMask ) && this.baseIPnumeric <= subnetID ) {
                return true;
            }
        }
        return false;
    }
}
