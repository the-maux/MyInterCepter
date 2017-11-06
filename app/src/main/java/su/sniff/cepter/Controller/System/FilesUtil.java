package su.sniff.cepter.Controller.System;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import su.sniff.cepter.Model.Target.DNSSpoofItem;
import su.sniff.cepter.Model.Target.Host;

public class                            FilesUtil {
    private static String               TAG = FilesUtil.class.getName();

    public static void                  dumpDnsOnFile(ArrayList<DNSSpoofItem> listObject, String nameFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(Singleton.getInstance().FilesPath + nameFile));
        for (DNSSpoofItem object : listObject) {
            Log.i(TAG, "[" + nameFile + "]Dumping: (" +  object + ")");
            writer.write(object.toString());
        }
        writer.close();
    }

    public static void                  dumpHostOnFile(ArrayList<Host> listObject, String nameFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(Singleton.getInstance().FilesPath + nameFile));
        for (Host object : listObject) {
            Log.i(TAG, "[" + nameFile + "]Dumping: (" +  object + ")");
            writer.write(object.toString());
        }
        writer.close();
    }

    public static ArrayList<DNSSpoofItem>     readDnsFromFile(String nameFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(Singleton.getInstance().FilesPath + nameFile));
        String line;
        ArrayList<DNSSpoofItem> rax = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            Log.i(TAG, "[" + nameFile + "]Reading: (" +  line + ")");
            if (line.contains(":")) {
                String[] tmp = line.split(":");
                rax.add(new DNSSpoofItem(tmp[0], tmp[1]));
            }
        }
        reader.close();
        return rax;
    }

}
