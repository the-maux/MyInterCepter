package su.sniff.cepter.Controller.System;

import android.util.Log;
import android.widget.TextView;

import su.sniff.cepter.Controller.CepterControl.IntercepterReader;
import su.sniff.cepter.R;
import su.sniff.cepter.Controller.MiscUtils.OpenFileDialog;
import su.sniff.cepter.View.MainActivity;
import su.sniff.cepter.globalVariable;

import java.io.*;

public class                    InterceptorFileSelected implements OpenFileDialog.OnFileSelectedListener {
    private String              TAG = "InterceptorFileSelected";
    private MainActivity        activity;
    private TextView            tvHello;

    public                      InterceptorFileSelected(MainActivity activity, TextView tvHello) {
        Log.d(TAG, "Constructor");
        this.activity = activity;
        this.tvHello = tvHello;
    }

    public void                 onFileSelected(File f) {
        tvHello.setTextSize(2, (float) globalVariable.raw_textsize);
        ((TextView) activity.findViewById(R.id.monitor)).setTextSize(2, (float) globalVariable.raw_textsize);
        File fDroidSheep = new File(globalVariable.path + "/exits.id");
        if (fDroidSheep.exists()) {
            fDroidSheep.delete();
        }
        activity.sniff_process = new RootProcess("InterceptorFileSelected", globalVariable.path + "");
        activity.sniff_process.exec(globalVariable.path + "/cepter " + f.getAbsolutePath() + " " + Integer.toString(globalVariable.resurrection));
        activity.sniff_process.exec("exit").closeDontWait();
        new IntercepterReader(activity, tvHello, activity.sniff_process);
    }
}