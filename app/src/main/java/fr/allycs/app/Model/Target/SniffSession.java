package fr.allycs.app.Model.Target;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import fr.allycs.app.Model.Unix.Os;

public class                            SniffSession extends File {
    @SerializedName("listDevices")
    private ArrayList<Host>             mListDevices;
    @SerializedName("accessPoint")
    private Host                        mAccessPoint;
    @SerializedName("date")
    private Date                        mDate;

    public SniffSession(@NonNull String pathname, ArrayList<Host> listDevices, Date date, Host accessPoint) {
        super(pathname);
        mDate = date;
        mListDevices = listDevices;
        mAccessPoint = accessPoint;
    }

    public int                          getNbrDevices() {
        return mListDevices.size();
    }

    public ArrayList<Os>                getListOsPresent() {
        ArrayList<Os> listOs = new ArrayList<>();
        for (Host device : mListDevices) {
            if (!listOs.contains(device.osType))
                listOs.add(device.osType);
        }
        return listOs;
    }

    public ArrayList<Host>              getListDevices() {
        return mListDevices;
    }

}
