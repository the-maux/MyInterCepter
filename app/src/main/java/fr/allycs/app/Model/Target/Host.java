package fr.allycs.app.Model.Target;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.Core.Conf.Singleton;

import fr.allycs.app.Controller.Misc.GlideApp;
import fr.allycs.app.Controller.Network.BonjourService.Service;
import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class                Host {
    private String          TAG = "Host";
    @SerializedName("ip")
    private String          ip = "Unknown";
    @SerializedName("name")
    private String          name = "Unknown";
    @SerializedName("mac")
    private String          mac = "Unknown";
    @SerializedName("hostname")
    private String          hostname;
    @SerializedName("os")
    private String          os = "Unknown";
    @SerializedName("vendor")
    private String          vendor = "Unknown";
    private ArrayList<Service> ServiceActivOnHost = null;
    private boolean         isServiceActiveOnHost = false;
    private boolean         selected = false;
    public  boolean         isItMyDevice = false;
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
            isItMyDevice();
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, buffer);
            e.getStackTrace();
        }
    }

    private void            isItMyDevice() {
        if (ip.contains(Singleton.getInstance().network.myIp)) {
            isItMyDevice = true;
        }
    }

    /**
     * Log mhost created in console
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
        return selected;
    }

    public void             setSelected(boolean selected) {
        this.selected = selected;
    }

    public String           getGenericId() {
        return mac.replace(":", "");
    }

    public String           getDumpInfo() {
        return dumpInfo;
    }

    public static void      setOsIcon(Context context, Host host, CircleImageView osImageView) {
        int                 ImageRessource;

        switch (host.getOsType()) {
            case Windows2000:
                ImageRessource = R.drawable.winicon;
                break;
            case WindowsXP:
                ImageRessource = R.drawable.winicon;
                break;
            case Windows10:
                ImageRessource = R.drawable.winicon;
                break;
            case Windows7_8_10:
                ImageRessource = R.drawable.winicon;
                break;
            case Cisco:
                ImageRessource = R.drawable.cisco;
                break;
            case Raspberry:
                ImageRessource = R.drawable.rasp;
                break;
            case QUANTA:
                ImageRessource = R.drawable.quanta;
                break;
            case Bluebird:
                ImageRessource = R.drawable.bluebird;
                break;
            case Apple:
                ImageRessource = R.drawable.ios;
                break;
            case Ios:
                ImageRessource = R.drawable.ios;
                break;
            case Unix:
                ImageRessource = R.drawable.linuxicon;
                break;
            case Linux_Unix:
                ImageRessource = R.drawable.linuxicon;
                break;
            case OpenBSD:
                ImageRessource = R.drawable.linuxicon;
                break;
            case Android:
                ImageRessource = R.mipmap.ic_logo_android_trans_round;
                break;
            case Mobile:
                ImageRessource = R.mipmap.ic_logo_android_trans_round;
                break;
            case Samsung:
                ImageRessource = R.mipmap.ic_logo_android_trans_round;
                break;
            case Unknow:
                ImageRessource = R.drawable.monitor;
                break;
            default:
                ImageRessource = R.drawable.monitor;
                break;
        }
        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .override(100, 100);
        GlideApp.with(context)
                .load(ImageRessource)
                .apply(myOptions)
                .placeholder(R.drawable.monitor)
                .into(osImageView);
    }

    private void            guessOsType(String InfoDevice) {
        InfoDevice = InfoDevice.toLowerCase();
        if (InfoDevice.contains("bluebird")) {
            osType = Os.Bluebird;
        } else if (InfoDevice.contains("cisco")) {
            osType = Os.Cisco;
        } else if (InfoDevice.contains("quanta")) {
            osType = Os.QUANTA;
        } else if (InfoDevice.contains("android") || InfoDevice.contains("mobile") || InfoDevice.contains("samsung") ||
                    InfoDevice.contains("murata") || InfoDevice.contains("huawei") || InfoDevice.contains("oneplus") ||
                    InfoDevice.contains("lg") || InfoDevice.contains("motorola")) {
            osType = Os.Android;
        } else if (InfoDevice.contains("windows 7")) {
            osType = Os.Windows7_8_10;
        } else if (InfoDevice.contains("windows 2000")) {
            osType = Os.WindowsXP;
        } else if (InfoDevice.contains("windows")) {
            osType = Os.Windows10;
        } else if (InfoDevice.contains("apple")) {
            osType = Os.Apple;
        } else if (InfoDevice.contains("raspberry")) {
            osType = Os.Raspberry;
        }  else if (InfoDevice.contains("ios")) {
            osType = Os.Ios;
        } else if (!(!InfoDevice.contains("unix") && !InfoDevice.contains("linux") && !InfoDevice.contains("bsd"))) {
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

    public boolean          isServiceActiveOnHost() {
        return isServiceActiveOnHost;
    }
    /**
     * Update service by BonjourManager
     */
    public void             updateServiceHost(Service service) {
        if (ServiceActivOnHost == null) {
            ServiceActivOnHost = new ArrayList<>();
            isServiceActiveOnHost = true;
        }
        ServiceActivOnHost.add(service);
    }

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