package su.sniff.cepter.Controller.Core.Conf;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.Core.Singleton;
import su.sniff.cepter.Model.Net.Network;

public class                                ConfigurationBehavior {
    private String                          PATH_NETWORK_CONF = Singleton.getInstance().FilesPath;
    private static ConfigurationBehavior    mInstance = null;
    private List<Network>                   mListNetworkHistoric;

    private ConfigurationBehavior() {
        mListNetworkHistoric = new ArrayList<>();
        try {
            String tmp, finalDump = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(PATH_NETWORK_CONF));
            while ((tmp = bufferedReader.readLine()) != null) {
                finalDump += tmp;
            }
            mListNetworkHistoric = new Gson().fromJson(finalDump, new TypeToken<ArrayList<Network>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ConfigurationBehavior getInstance() {
        if(mInstance == null)
            mInstance = new ConfigurationBehavior();
        return mInstance;
    }

}
