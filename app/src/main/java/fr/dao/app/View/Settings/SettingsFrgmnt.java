package fr.dao.app.View.Settings;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class SettingsFrgmnt extends MyFragment {
    private String              TAG = "SettingsFrgmnt";
    protected CoordinatorLayout mCoordinatorLayout;
    protected MyActivity        mActivity;
    protected LinearLayout      mCentral_layout;
    private LayoutInflater      inflater;
    private ViewGroup           container;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        this.inflater = inflater;
        this.container = container;

        mActivity = (MyActivity) this.getActivity();
        initXml(rootView);
        mActivity.setToolbarTitle("Settings", "Network discovery");
        init();
        return rootView;
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mCentral_layout = rootView.findViewById(R.id.central_layout);
    }


    public void                 addItemMenu(String title, String subtitle, final Runnable onClick,
                                            String switchEnabled, int colorThumb, int colorBack) {
        View settingsItemView = inflater.inflate(R.layout.item_settings_dark, container, false);
        TextView title_TV = settingsItemView.findViewById(R.id.title);
        TextView subtitle_TV = settingsItemView.findViewById(R.id.subtitle);
        ConstraintLayout rootView = settingsItemView.findViewById(R.id.rootView);
        SwitchCompat switch_sw = settingsItemView.findViewById(R.id.switch_sw);
        if (switchEnabled == null) {
            switch_sw.setVisibility(View.GONE);
        } else
            initSwitchBehavior(switch_sw, switchEnabled, onClick,  colorThumb, colorBack);

        title_TV.setText(title);
        if (subtitle == null) {
            subtitle_TV.setVisibility(View.GONE);
        } else {
            subtitle_TV.setText(subtitle);
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new Thread(onClick).start();
            }
        });
        settingsItemView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(onClick).start();
            }
        });
        addViewSettingsToListSettings();
        mCentral_layout.addView(settingsItemView);
    }

    private void                initSwitchBehavior(SwitchCompat switch_sw, String switchEnabled,
                                                   final Runnable onClick, int colorThumb, int colorBack) {
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.GRAY,
                colorThumb,
        };

        int[] trackColors = new int[] {
                ContextCompat.getColor(mActivity, R.color.switchOFF),
                colorBack,
        };

        DrawableCompat.setTintList(DrawableCompat.wrap(switch_sw.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(switch_sw.getTrackDrawable()), new ColorStateList(states, trackColors));
        switch_sw.setChecked(Boolean.valueOf(switchEnabled));
        switch_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new Thread(onClick).start();
            }
        });
    }

    private void                addViewSettingsToListSettings() {

    }
}
