package su.sniff.cepter.Controller.Core.Conf;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Model.Net.Network;

public class PersistanteConfiguration {
    private String                          PATH_NETWORK_CONF = Singleton.getInstance().FilesPath;
    private static PersistanteConfiguration mInstance = null;
    private List<Network>                   mListNetworkHistoric;

    private PersistanteConfiguration() {
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

    public static synchronized PersistanteConfiguration getInstance() {
        if(mInstance == null)
            mInstance = new PersistanteConfiguration();
        return mInstance;
    }

}
