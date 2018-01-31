package fr.allycs.app.Model.Net;


import android.util.Log;
import android.util.SparseIntArray;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class                    listPorts {
    private String              TAG = "listPorts";
    private ArrayList<Port>     mPorts = new ArrayList<>();
    private SparseIntArray      primitivePortsLits = new SparseIntArray();
    private String              dump;

    public                      listPorts(ArrayList<String> lines) {
        dump = StringUtils.join(lines, "\n");
        for (String line : lines) {
            line = line.replaceAll("  ", " ");
            Port port = add(line);
            primitivePortsLits.append(port.getPort(), port.state.getValue());
            mPorts.add(port);
        }
        dump();
    }

    public Port                 add(String dumpPortLine) {
        dumpPortLine = dumpPortLine.replaceAll("  ", " ");
        String[] dumpssplited = dumpPortLine.split(" ");
        return new Port(dumpssplited[0], dumpssplited[1], dumpssplited[2]);
    }

    public listPorts            add(Port port) {
        mPorts.add(port);
        return this;
    }


    public boolean               isPortOpen(int portNumber) {
        return Port.State.valueOf(primitivePortsLits.get(portNumber)) == Port.State.OPEN;
    }

    public void                 dump() {
        Log.d(TAG, "Ports dumps:");
        for (Port port : mPorts) {
            Log.i(TAG, port.toString());
        }
    }
}
