package fr.allycs.app.View.Scan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public View                 onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nmap, container, false);
        initXml(rootView);
        return rootView;
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        Output = rootView.findViewById(R.id.Output);
        Output.setMovementMethod(new ScrollingMovementMethod());
        Output.setText("\nroot$> ");
    }

    public void                 refresh(Host host) {
        mFocusedHost = host;
        if (Output != null)
            Output.setText("\nroot$> ");
    }

    public void                 flushOutput(final String stdout) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Output.setText(Output.getText() + "\nroot$> " + stdout);
            }
        });
    }
}
