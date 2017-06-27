package su.sniff.cepter;

import android.support.v4.view.MotionEventCompat;
import java.util.ArrayList;
import java.util.List;

public class IPv4 {
    int baseIPnumeric;
    int netmaskNumeric;

    public IPv4(String symbolicIP, String netmask) throws NumberFormatException {
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

    public IPv4(String IPinCIDRFormat) throws NumberFormatException {
        String[] st = IPinCIDRFormat.split("\\/");
        if (st.length != 2) {
            throw new NumberFormatException("Invalid CIDR format '" + IPinCIDRFormat + "', should be: xx.xx.xx.xx/xx");
        }
        String symbolicIP = st[0];
        Integer numericCIDR = new Integer(st[1]);
        if (numericCIDR.intValue() > 32) {
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
        if (numericCIDR.intValue() < 8) {
            throw new NumberFormatException("Netmask CIDR can not be less than 8");
        }
        this.netmaskNumeric = -1;
        this.netmaskNumeric <<= 32 - numericCIDR.intValue();
    }

    public String getIP() {
        return convertNumericIpToSymbolic(Integer.valueOf(this.baseIPnumeric));
    }

    private String convertNumericIpToSymbolic(Integer ip) {
        StringBuffer sb = new StringBuffer(15);
        for (int shift = 24; shift > 0; shift -= 8) {
            sb.append(Integer.toString((ip.intValue() >>> shift) & MotionEventCompat.ACTION_MASK));
            sb.append('.');
        }
        sb.append(Integer.toString(ip.intValue() & MotionEventCompat.ACTION_MASK));
        return sb.toString();
    }

    public String getNetmask() {
        StringBuffer sb = new StringBuffer(15);
        for (int shift = 24; shift > 0; shift -= 8) {
            sb.append(Integer.toString((this.netmaskNumeric >>> shift) & MotionEventCompat.ACTION_MASK));
            sb.append('.');
        }
        sb.append(Integer.toString(this.netmaskNumeric & MotionEventCompat.ACTION_MASK));
        return sb.toString();
    }

    public String getCIDR() {
        int i = 0;
        while (i < 32 && (this.netmaskNumeric << i) != 0) {
            i++;
        }
        return convertNumericIpToSymbolic(Integer.valueOf(this.baseIPnumeric & this.netmaskNumeric)) + "/" + i;
    }

    public List<String> getAvailableIPs(Integer numberofIPs) {
        ArrayList<String> result = new ArrayList();
        int numberOfBits = 0;
        while (numberOfBits < 32 && (this.netmaskNumeric << numberOfBits) != 0) {
            numberOfBits++;
        }
        Integer numberOfIPs = Integer.valueOf(0);
        for (int n = 0; n < 32 - numberOfBits; n++) {
            numberOfIPs = Integer.valueOf(Integer.valueOf(numberOfIPs.intValue() << 1).intValue() | 1);
        }
        Integer baseIP = Integer.valueOf(this.baseIPnumeric & this.netmaskNumeric);
        int i = 1;
        while (i < numberOfIPs.intValue() && i < numberofIPs.intValue()) {
            result.add(convertNumericIpToSymbolic(Integer.valueOf(baseIP.intValue() + i)));
            i++;
        }
        return result;
    }

    public String getHostAddressRange() {
        int numberOfBits = 0;
        while (numberOfBits < 32 && (this.netmaskNumeric << numberOfBits) != 0) {
            numberOfBits++;
        }
        Integer numberOfIPs = Integer.valueOf(0);
        for (int n = 0; n < 32 - numberOfBits; n++) {
            numberOfIPs = Integer.valueOf(Integer.valueOf(numberOfIPs.intValue() << 1).intValue() | 1);
        }
        Integer baseIP = Integer.valueOf(this.baseIPnumeric & this.netmaskNumeric);
        String firstIP = convertNumericIpToSymbolic(Integer.valueOf(baseIP.intValue() + 1));
        return firstIP + " - " + convertNumericIpToSymbolic(Integer.valueOf((baseIP.intValue() + numberOfIPs.intValue()) - 1));
    }

    public Integer getNumberOfHosts() {
        int numberOfBits = 0;
        while (numberOfBits < 32 && (this.netmaskNumeric << numberOfBits) != 0) {
            numberOfBits++;
        }
        Double x = Double.valueOf(Math.pow(2.0d, (double) (32 - numberOfBits)));
        if (x.doubleValue() == -1.0d) {
            x = Double.valueOf(1.0d);
        }
        return Integer.valueOf(x.intValue());
    }

    public String getWildcardMask() {
        Integer wildcardMask = Integer.valueOf(this.netmaskNumeric ^ -1);
        StringBuffer sb = new StringBuffer(15);
        for (int shift = 24; shift > 0; shift -= 8) {
            sb.append(Integer.toString((wildcardMask.intValue() >>> shift) & MotionEventCompat.ACTION_MASK));
            sb.append('.');
        }
        sb.append(Integer.toString(wildcardMask.intValue() & MotionEventCompat.ACTION_MASK));
        return sb.toString();
    }

    public String getBroadcastAddress() {
        if (this.netmaskNumeric == -1) {
            return "0.0.0.0";
        }
        int numberOfBits = 0;
        while (numberOfBits < 32 && (this.netmaskNumeric << numberOfBits) != 0) {
            numberOfBits++;
        }
        Integer numberOfIPs = Integer.valueOf(0);
        for (int n = 0; n < 32 - numberOfBits; n++) {
            numberOfIPs = Integer.valueOf(Integer.valueOf(numberOfIPs.intValue() << 1).intValue() | 1);
        }
        return convertNumericIpToSymbolic(Integer.valueOf(Integer.valueOf(this.baseIPnumeric & this.netmaskNumeric).intValue() + numberOfIPs.intValue()));
    }

    private String getBinary(Integer number) {
        String result = BuildConfig.FLAVOR;
        Integer ourMaskBitPattern = Integer.valueOf(1);
        int i = 1;
        while (i <= 32) {
            if ((number.intValue() & ourMaskBitPattern.intValue()) != 0) {
                result = "1" + result;
            } else {
                result = "0" + result;
            }
            if (!(i % 8 != 0 || i == 0 || i == 32)) {
                result = "." + result;
            }
            ourMaskBitPattern = Integer.valueOf(ourMaskBitPattern.intValue() << 1);
            i++;
        }
        return result;
    }

    public String getNetmaskInBinary() {
        return getBinary(Integer.valueOf(this.netmaskNumeric));
    }

    public boolean contains(String IPaddress) {
        Integer checkingIP = Integer.valueOf(0);
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
            checkingIP = Integer.valueOf(checkingIP.intValue() + (value << i));
            i -= 8;
        }
        if ((this.baseIPnumeric & this.netmaskNumeric) == (checkingIP.intValue() & this.netmaskNumeric)) {
            return true;
        }
        return false;
    }

    public boolean contains(IPv4 child) {
        Integer subnetID = Integer.valueOf(child.baseIPnumeric);
        Integer subnetMask = Integer.valueOf(child.netmaskNumeric);
        if ((subnetID.intValue() & this.netmaskNumeric) == (this.baseIPnumeric & this.netmaskNumeric)) {
            if ((this.netmaskNumeric < subnetMask.intValue()) && this.baseIPnumeric <= subnetID.intValue()) {
                return true;
            }
        }
        return false;
    }
}
