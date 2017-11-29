package fr.allycs.app.Controller.Misc;

public class Utils {
    public static int count(String text, String find) {
        int index = 0, count = 0, length = find.length();
        while( (index = text.indexOf(find, index)) != -1 ) {
            index += length;
            count++;
        }
        return count;
    }
}
