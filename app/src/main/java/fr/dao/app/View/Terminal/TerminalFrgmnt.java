package fr.dao.app.View.Terminal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Shell;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Dialog.QuestionDialog;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                    TerminalFrgmnt extends MyFragment  {
    private String              TAG = "NmapOutputView";
    private TerminalFrgmnt      mInstance;
    private CoordinatorLayout   mCoordinatorLayout;
    private TextView            Output;
    private Map                 ttyByTabs = new HashMap();
    private String              actualOutput = "";
    private String              PROMPT = "shell";
    private TerminalActivity    mActivity;
    private ArrayList<Shell>    mShell;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nmap, container, false);
        mActivity = (TerminalActivity)getActivity();
        initXml(rootView);
        init();
        return rootView;
    }

    public void                 init() {
        if (mShell == null) {
            mShell = new ArrayList<>();
            mShell.add(new Shell(mActivity, mInstance));
        }
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        Output = rootView.findViewById(R.id.RV_Wireshark);
        Output.setOnClickListener(onNewInput());
        Output.setMovementMethod(new ScrollingMovementMethod());
        String output = "<font color='red'>" + PROMPT + "</font> " + "<font color='cyan'> $> </font>";
        Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);

    }

    private View.OnClickListener onNewInput() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                new QuestionDialog(mActivity)
                        .setTitle("Cmd")
                        .setText("")
                        .onPositiveButton("Execute", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            Utils.vibrateDevice(mActivity, 100);
                            }
                        })
                        .show();
            }
        };
    }

    public void                 refresh(int nbrTTY) {
        if (Output != null) {//Besoin de faire évolué le get historic from host.mac to host.scan.type
            if (ttyByTabs.get(nbrTTY) != null) {
                Log.d(TAG, "Loading previous tty:" + nbrTTY);
                Output.setText(Html.fromHtml((String) ttyByTabs.get(nbrTTY)), TextView.BufferType.SPANNABLE);
                actualOutput = (String) ttyByTabs.get(nbrTTY);
            } else {
                Log.d(TAG, "First init of tty:" + nbrTTY);
                String output = "<font color='red'>" + PROMPT + "</font>   ";
                Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);
                actualOutput = output;
            }
        } else {
            Log.d(TAG, "output is null");
        }
    }

    public void                 printCmdInTerminal(final String txt) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String output = "<font color='red'>" + PROMPT + "</font> " +
                                    "<font color='cyan'>" +  " $> " + "</font>" +
                        txt + "<br>";
                Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);
                actualOutput = output;
            }
        });
    }

    public void                 flushOutput(final String stdout, final ProgressBar progressBar) {
        final String output = (stdout == null) ? "Shell error" : (actualOutput + stdout).replace("\n", "<br>");
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
                Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);
                actualOutput = output;
                ttyByTabs.put(mActivity.mTabs.getSelectedTabPosition(), actualOutput);
            }
        });
    }

}
