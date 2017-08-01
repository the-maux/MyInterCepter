package su.sniff.cepter;

import java.io.BufferedReader;

import su.sniff.cepter.Controller.NetUtils;
import su.sniff.cepter.Controller.RootProcess;

/**
 * Created by root on 01/08/17.
 */

public class                    Cepter {
    private static RootProcess  actualProcess = null;

    public static void          startCepter(String MAC) {
        new RootProcess("/cepter getv", globalVariable.path) //Start Cepter Binary
                .exec(globalVariable.path + "/cepter getv " + MAC)
                .exec("exit")
                .closeProcess();
    }

    public static BufferedReader searchDevices(String AdaptNumb) {
        final RootProcess process = new RootProcess("Cepter Scan1", globalVariable.path + "");
        final BufferedReader bufferedReader2 = new BufferedReader(process.getInputStreamReader());
        process.exec(globalVariable.path + "/cepter scan " + AdaptNumb);
        process.exec("exit");
        actualProcess = process;
        return bufferedReader2;
    }

    public static void          waitForCepter() {
        actualProcess.waitFor();
    }
}
