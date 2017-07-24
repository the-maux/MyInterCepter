package su.sniff.cepter.Misc;

import su.sniff.cepter.globalVariable;

/**
 * Created by the-maux on 12/07/17.
 */

public class                ThreadUtils {
    public static void      lock() {
        if (globalVariable.lock == 0) {
            globalVariable.lock = 1;
        } else {
            while (globalVariable.lock == 1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            globalVariable.lock = 1;
        }
    }
}
