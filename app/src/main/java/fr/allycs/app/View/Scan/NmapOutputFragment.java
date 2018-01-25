package fr.allycs.app.View.Scan;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Misc.MyFragment;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;

public class                    NmapOutputFragment extends MyFragment  {
    private String              TAG = "NmapOutputFragment";
    private Host                mFocusedHost;
    private CoordinatorLayout   mCoordinatorLayout;
    private Singleton           mSingleton = Singleton.getInstance();
    private TextView            Output;
    private Map                 historicByDevice = new HashMap();
    private String              actualOutput = "";
    private String              PROMPT = "root $> ";

    public View                 onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nmap, container, false);
        initXml(rootView);
        return rootView;
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        Output = rootView.findViewById(R.id.Output);
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String output = "<font color='red'>" + PROMPT + "></font> " + txt + "<br>";
                Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);
                actualOutput = output;
            }
        });
    }

    public void                 flushOutput(final String stdout, final ProgressBar progressBar) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String output = (actualOutput + stdout).replace("\n", "<br>");
                Output.setText(Html.fromHtml(output), TextView.BufferType.SPANNABLE);
                actualOutput = output;
                historicByDevice.put(mFocusedHost.mac, actualOutput);
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }
}
