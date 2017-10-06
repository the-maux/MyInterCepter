package su.sniff.cepter.Model.Wrap;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import su.sniff.cepter.R;

/**
 * Created by maxim on 02/10/2017.
 */

public class            ConsoleLog {
    public String       line;
    public int          color = 0x00;

    public              ConsoleLog(String line) {
        this.line = line;
    }
}
