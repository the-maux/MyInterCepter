package fr.allycs.app.View.Widget;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.AndroidUtils.MyFragment;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;

public class                    SettingsFragment extends MyFragment {
    private String              TAG = "SettingsFragment";
    private CoordinatorLayout   mCoordinatorLayout;
    private Singleton           mSingleton = Singleton.getInstance();
    private MyActivity          mActivity;
    private LayoutInflater      inflater;
    private ViewGroup           container;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_historic, container, false);
        this.inflater = inflater;
        this.container = container;
        initXml(rootView);
        mActivity = (MyActivity) this.getActivity();
        init();
        return rootView;
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
    }

    public void                  setTitle(String txt) {
        mActivity.setToolbarTitle(txt, null);
    }

    public void                 addItemMenu(String title, String subtitle, final Thread onClick, String switchEnabled) {
        View itemView = inflater.inflate(R.layout.item_settings, container, false);
        TextView title_TV = itemView.findViewById(R.id.title);
        TextView subtitle_TV = itemView.findViewById(R.id.subtitle);
        RelativeLayout rootView = itemView.findViewById(R.id.rootView);
        Switch switch_sw = itemView.findViewById(R.id.switch_sw);
        title_TV.setText(title);
        if (subtitle == null) {
            subtitle_TV.setVisibility(View.GONE);
        } else {
            subtitle_TV.setText(subtitle);
        }
        if (switchEnabled == null) {
            switch_sw.setVisibility(View.GONE);
        } else {
            switch_sw.setChecked(switchEnabled.contains("true"));
        }
        switch_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onClick.start();
            }
        });
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.start();
            }
        });
        itemView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.start();
            }
        });
    }
}
