package fr.dao.app.Model.Unix;

public enum Os {
    Windows2000, WindowsXP, Windows, Windows7_8_10,
    Cisco, QUANTA, Gateway,
    Raspberry,
    Bluebird,
    Apple, Ios,
    Unix, Linux_Unix, OpenBSD,
    Android,
    Mobile, Samsung,
    Ps4, Unknow;

    public static Os fromString(String type) {
        if (type.contains("Ps4")) {
            return Ps4;
        } else if (type.contains("Samsung")) {
            return Samsung;
        } else if (type.contains("Mobile")) {
            return Mobile;
        } else if (type.contains("Android")) {
            return Android;
        } else if (type.contains("OpenBSD")) {
            return OpenBSD;
        } else if (type.contains("Unix")) {
            return Unix;
        } else if (type.contains("Apple")) {
            return Apple;
        } else if (type.contains("Bluebird")) {
            return Bluebird;
        } else if (type.contains("Raspberry")) {
            return Raspberry;
        } else if (type.contains("Gateway")) {
            return Gateway;
        } else if (type.contains("QUANTA")) {
            return QUANTA;
        } else if (type.contains("Cisco")) {
            return Cisco;
        } else if (type.contains("Windows")) {
            return Windows;
        }
        return Unknow;
    }
}
