package su.sniff.cepter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class globalVariable {
    public static String PCAP_PATH;
    public static int adapt_num;
    public static ArrayAdapter<String> adapter;
    public static ArrayAdapter<String> adapter2;
    public static String codepage = "Cp1251";
    public static int cookies_c = 0;
    public static ArrayList<String> cookies_domain = new ArrayList();
    public static ArrayList<String> cookies_domain2 = new ArrayList();
    public static ArrayList<String> cookies_getreq = new ArrayList();
    public static ArrayList<String> cookies_getreq2 = new ArrayList();
    public static ArrayList<Drawable> cookies_icon;
    public static ArrayList<String> cookies_ip = new ArrayList();
    public static ArrayList<String> cookies_ip2 = new ArrayList();
    public static int cookies_show = 0;
    public static ArrayList<String> cookies_value = new ArrayList();
    public static ArrayList<String> cookies_value2 = new ArrayList();
    public static int dnss;
    public static String gw_ip;
    public static int killer = 0;
    public static int lock = 0;
    public static String netmask;
    public static String own_ip;
    public static Activity parent;
    public static int raw_autoscroll = 0;
    public static int raw_textsize = 9;
    public static int resurrection;
    public static int savepcap;
    public static int screenlock = 0;
    public static int showhttp = 0;
    public static int strip = 0;
}
