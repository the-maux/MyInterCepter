package fr.allycs.app.Controller.Misc;

import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;

public class                Utils {
    public static int       nbrSubstringOccurence(String text, String find) {
        int index = 0, count = 0, length = find.length();
        while( (index = text.indexOf(find, index)) != -1 ) {
            index += length;
            count++;
        }
        return count;
    }
    public static int       ReadOnlyFileSystemOFF() {
        return new RootProcess("initialisation ").exec("mount -o rw,remount /system").closeProcess();
    }
}
