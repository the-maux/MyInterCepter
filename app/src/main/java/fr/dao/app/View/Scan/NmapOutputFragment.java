package fr.dao.app.View.Scan;

import android.app.Activity;
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

import java.util.HashMap;
import java.util.Map;

import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                    NmapOutputFragment extends MyFragment  {
    private String              TAG = "NmapOutputFragment";
    private Host                mFocusedHost;
    private CoordinatorLayout   mCoordinatorLayout;
    private TextView            Output;
    private Map                 historicByDevice = new HashMap();
    private String              actualOutput = "";
    private String              PROMPT = "root ";
    private Activity            mActivity;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nmap, container, false);
        mActivity = getActivity();
        initXml(rootView);
        return rootView;
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        Output = rootView.findViewById(R.id.RV_Wireshark);
        Output.setMovementMethod(new ScrollingMovementMethod());
        String output = "<font color='red'>" + PROMPT + "</font> ";
        Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);
    }

    public void                 refresh(Host host) {
        mFocusedHost = host;
        if (Output != null) {
            if (historicByDevice.get(host.mac) != null) {
                Log.d(TAG, "Loading previous view for device:" + host.ip);
                Output.setText(Html.fromHtml((String)historicByDevice.get(host.mac)), TextView.BufferType.SPANNABLE);
                actualOutput = (String)historicByDevice.get(host.mac);
            } else {
                Log.d(TAG, "First init of view for device:" + host.ip);
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
                        "<font color='darkcyan'>" +  " $> " + "</font>" +
                        txt + "<br>";
                Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);
                actualOutput = output;
            }
        });
    }

    public void                 flushOutput(final String stdout, final ProgressBar progressBar) {
        final String output = (stdout == null) ? "Nmap error" : (actualOutput + stdout).replace("\n", "<br>");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
                Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);
                actualOutput = output;
                historicByDevice.put(mFocusedHost.mac, actualOutput);
            }
        });
    }

}
