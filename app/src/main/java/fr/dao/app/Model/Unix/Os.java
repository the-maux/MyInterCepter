package fr.dao.app.Model.Unix;


public class                  Os {
    public static final int   Windows2000 = 0x01;
    public static final int   WindowsXP = 0x02;
    public static final int   Windows = 0x03;
    public static final int   Windows7_8_10 = 0x04;
    public static final int   Cisco = 0x05;
    public static final int   QUANTA = 0x06;
    public static final int   Gateway = 0x07;
    public static final int   Raspberry = 0x08;
    public static final int   Bluebird = 0x09;
    public static final int   Apple = 0xA;
    public static final int   Ios = 0xB;
    public static final int   Unix = 0xC;
    public static final int   Linux_Unix = 0xD;
    public static final int   OpenBSD = 0xE;
    public static final int   Android = 0xF;
    public static final int   Mobile = 0x10;
    public static final int   Samsung = 0x11;
    public static final int   Ps4 = 0x12;
    public static final int   Chromecast = 0x13;
    public static final int   Unknow = 0x14;

    public static final String   Windows2000_S = "Windows";
    public static final String   WindowsXP_S = "Windows";
    public static final String   Windows_S = "Windows";
    public static final String   Windows7_8_10_S = "Windows";
    public static final String   Cisco_S = "Cisco";
    public static final String   QUANTA_S = "QUANTA";
    public static final String   Gateway_S = "Gateway";
    public static final String   Raspberry_S = "Raspberry";
    public static final String   Bluebird_S = "Bluebird";
    public static final String   Apple_S = "Apple";
    public static final String   Ios_S = "Apple";
    public static final String   Unix_S = "Unix";
    public static final String   Linux_Unix_S = "Unix";
    public static final String   OpenBSD_S = "OpenBSD";
    public static final String   Android_S = "Android";
    public static final String   Mobile_S = "Mobile";
    public static final String   Samsung_S = "Samsung";
    public static final String   Ps4_S = "Ps4";
    public static final String   Chromecast_S = "Chromecast";
    public static final String   Unknow_S = "Unknow";

    public static int           fromString(String type) {
        if (type.contains(Ps4_S)) {
            return Ps4;
        } else if (type.contains(Samsung_S)) {
            return Samsung;
        } else if (type.contains(Mobile_S)) {
            return Mobile;
        } else if (type.contains(Android_S)) {
            return Android;
        } else if (type.contains(OpenBSD_S)) {
            return OpenBSD;
        } else if (type.contains(Unix_S)) {
            return Unix;
        } else if (type.contains(Apple_S)) {
            return Apple;
        } else if (type.contains(Bluebird_S)) {
            return Bluebird;
        } else if (type.contains(Raspberry_S)) {
            return Raspberry;
        } else if (type.contains(Gateway_S)) {
            return Gateway;
        } else if (type.contains(QUANTA_S)) {
            return QUANTA;
        } else if (type.contains(Cisco_S)) {
            return Cisco;
        } else if (type.contains(Windows_S)) {
            return Windows;
        }
        return Unknow;
    }

    public static String toString(int value) {
        switch (value) {
            case Windows:
                return Windows_S;
            case Windows7_8_10:
                return Windows_S;
            case WindowsXP:
                return Windows_S;
            case Windows2000:
                return Windows_S;
            case Cisco:
                return Cisco_S;
            case QUANTA:
                return QUANTA_S;
            case Gateway:
                return Gateway_S;
            case Raspberry:
                return Raspberry_S;
            case Bluebird:
                return Bluebird_S;
            case Apple:
                return Apple_S;
            case Ios:
                return Ios_S;
            case Unix:
                return Unix_S;
            case Linux_Unix:
                return Linux_Unix_S;
            case OpenBSD:
                return OpenBSD_S;
            case Android:
                return Android_S;
            case Mobile:
                return Mobile_S;
            case Samsung:
                return Samsung_S;
            case Ps4:
                return Ps4_S;
            default:
                return Unknow_S;
        }
    }
}
