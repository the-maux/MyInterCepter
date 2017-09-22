package su.sniff.cepter.Model.Target;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import su.sniff.cepter.R;

/**
 * Created by AdeTek on 07/07/17.
 */

public class                Host {
    private String          TAG = "Host";
    private String          ip = "Unknown";
    private String          name = "Unknown";
    private String          mac = "Unknown";
    private String          hostname;
    private String          os = "Unknown";
    private String          vendor = "Unknown";
    private boolean         selected = false;
    private String          dumpInfo;
    private Os              osType;

    /**
     * Format : 192.168.0.12 	(theMaux) : [E8-B1-FC-A6-CF-11] [Windows 7\8\10] ; Intel Corporate
     * @param buffer buffer
     */
    public                  Host(String buffer) {
        try {
            buffer = buffer.replace("\t", " ").replace("  ", " ");
            String beg = buffer.substring(0, buffer.indexOf(":") - 1);
            String mid = buffer.substring(buffer.indexOf(":") + 2, buffer.indexOf(";") - 1);
            String end = buffer.substring(buffer.indexOf(";") + 2);
            ip = beg.substring(0, beg.indexOf("(") - 1).replace("\n", "");
            name = beg.substring(beg.indexOf("(")).replace("\n", "");
            mac = mid.substring(0, mid.indexOf(" ")).replace("\n", "");
            os = mid.substring(mid.indexOf(" ") + 1).replace("\n", "");
            vendor = end.replace("\n", "");
            //logHost(buffer);
            dumpInfo = buffer;
            guessOsType(dumpInfo);
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, buffer);
            e.getStackTrace();
        }
    }

    /**
     * Log host created in console
     * @param buffer buffer
     */
    private void            logHost(String buffer) {
        Log.d(TAG, "buffer " + buffer + "");
        Log.d(TAG, "==> ip " + ip + "");
        Log.d(TAG, "==> name " + name + "");
        Log.d(TAG, "==> mac " + mac + "");
        Log.d(TAG, "==> os " + os + "");
        Log.d(TAG, "==> vendor " + vendor + "");
        Log.d(TAG, "----------------");
    }

    private List<Port>      portList;

    public                  Host(String ip, String mac, String os, String Vendor) {
        this.ip = ip;
        this.mac = mac;
        this.os = os;
        this.vendor = Vendor;
    }

    public                  Host(String ip, String mac, String os) {
        this.ip = ip;
        this.mac = mac;
        this.os = os;
    }

    public String           getIp() {
        return ip;
    }

    public void             setIp(String ip) {
        this.ip = ip;
    }

    public String           getMac() {
        return mac.replace("[", "").replace("]", "");
    }

    public String           getOS() {
        return os;
    }

    public String           getVendor() {
        return vendor;
    }

    public List<Port>       getPortList() {
        return portList;
    }

    public void             setPortList(List<Port> portList) {
        this.portList = portList;
    }

    public String           getName() {
        return (name.contains("Unknown") ? getIp() : name);
    }

    public void             setName(String name) {
        this.name = name;
    }

    public boolean          isSelected() {
        return this.selected;
    }

    public void             setSelected(boolean selected) {
        this.selected = selected;
    }

    public String           getDumpInfo() {
        return dumpInfo;
    }

    public static void      setOsIcon(Context context, String InfoDevice, CircleImageView osImageView) {
        int                 ImageRessource;

        if (InfoDevice == null) {
            ImageRessource = R.drawable.monitor;
        } else if (InfoDevice.contains("Windows")) {
            ImageRessource = R.drawable.winicon;
        } else if (InfoDevice.contains("Apple")) {
            ImageRessource = R.drawable.ios;
        } else if (InfoDevice.contains("Android") || InfoDevice.contains("Mobile") || InfoDevice.contains("Samsung")) {
            ImageRessource = R.drawable.android;
        } else if (InfoDevice.contains("Cisco")) {
            ImageRessource = R.drawable.cisco;
        } else if (InfoDevice.contains("Raspberry")) {
            ImageRessource = R.drawable.rasp;
        } else if (InfoDevice.contains("QUANTA")) {
            ImageRessource = R.drawable.quanta;
        } else if (InfoDevice.contains("Bluebird")) {
            ImageRessource = R.drawable.bluebird;
        } else if (InfoDevice.contains("Ios")) {
            ImageRessource = R.drawable.ios;
        } else if (!(!InfoDevice.contains("Unix") && !InfoDevice.contains("Linux") && !InfoDevice.contains("BSD"))) {
            ImageRessource = R.drawable.linuxicon;
        } else
            ImageRessource = R.drawable.monitor;
        Glide.with(context)
                .load(ImageRessource)
                .override(100, 100)
                .fitCenter()
                .crossFade()
                .into(osImageView);
    }

    private void                guessOsType(String InfoDevice) {
        if (InfoDevice.contains("Bluebird")) {
            osType = Os.Bluebird;
        } else if (InfoDevice.contains("Windows 7")) {
            osType = Os.Windows7_8_10;
        } else if (InfoDevice.contains("Windows 2000")) {
            osType = Os.WindowsXP;
        } else if (InfoDevice.contains("Apple")) {
            osType = Os.Apple;
        } else if (InfoDevice.contains("Android") || InfoDevice.contains("Mobile") || InfoDevice.contains("Samsung")) {
            osType = Os.Android;
        } else if (InfoDevice.contains("Cisco")) {
            osType = Os.Cisco;
        } else if (InfoDevice.contains("Raspberry")) {
            osType = Os.Raspberry;
        } else if (InfoDevice.contains("QUANTA")) {
            osType = Os.QUANTA;
        }  else if (InfoDevice.contains("Ios")) {
            osType = Os.Ios;
        } else if (!(!InfoDevice.contains("Unix") && !InfoDevice.contains("Linux") && !InfoDevice.contains("BSD"))) {
            osType = Os.Linux_Unix;
        } else
            osType = Os.Unknow;
    }

    public static Comparator<Host> comparator = new Comparator<Host>() {
        @Override
        public int compare(Host o1, Host o2) {
            String ip1[] = o1.getIp().replace(".", "::").split("::");
            String ip2[] = o2.getIp().replace(".", "::").split("::");
            if (Integer.parseInt(ip1[2]) > Integer.parseInt(ip2[2]))
                return 1;
            else if (Integer.parseInt(ip1[2]) < Integer.parseInt(ip2[2]))
                return -1;
            else if (Integer.parseInt(ip1[3]) > Integer.parseInt(ip2[3]))
                return 1;
            else if (Integer.parseInt(ip1[3]) < Integer.parseInt(ip2[3]))
                return -1;
            return 0;
        }
    };


    public Os               getOsType() {
        return osType;
    }

    @Override
    public boolean          equals(Object obj) {
        return  ip.contains(((Host) obj).getIp()) && mac.contains(((Host) obj).getMac());
    }

    @Override
    public String           toString() {
        return ip + ":" + mac;
    }
}