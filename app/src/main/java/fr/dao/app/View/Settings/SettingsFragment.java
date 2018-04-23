package fr.dao.app.View.Settings;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
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

public class                    SettingsFragment extends MyFragment {
    private String              TAG = "SettingsFragment";
    protected CoordinatorLayout mCoordinatorLayout;
    protected MyActivity        mActivity;
    protected LinearLayout      mCentral_layout;
    private LayoutInflater      inflater;
    private ViewGroup           container;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        this.inflater = inflater;
        this.container = container;
        initXml(rootView);
        mActivity = (MyActivity) this.getActivity();
        init();
        return rootView;
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mCentral_layout = rootView.findViewById(R.id.central_layout);
    }

    public void                 setTitle(String txt) {
        mActivity.setToolbarTitle(txt, "");
    }

    public void                 addItemMenu(String title, String subtitle, final Runnable onClick,
                                            String switchEnabled) {
        View settingsItemView = inflater.inflate(R.layout.item_settings_dark, container, false);
        TextView title_TV = settingsItemView.findViewById(R.id.title);
        TextView subtitle_TV = settingsItemView.findViewById(R.id.subtitle);
        ConstraintLayout rootView = settingsItemView.findViewById(R.id.rootView);
        SwitchCompat switch_sw = settingsItemView.findViewById(R.id.switch_sw);
        initSwitchBehavior(switch_sw);

        title_TV.setText(title);
        if (subtitle == null) {
            subtitle_TV.setVisibility(View.GONE);
        } else {
            subtitle_TV.setText(subtitle);
        }
        if (switchEnabled == null) {
            switch_sw.setVisibility(View.GONE);
        }
        switch_sw.setChecked(Boolean.valueOf(switchEnabled));
        switch_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new Thread(onClick).start();
            }
        });
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

    private void                initSwitchBehavior(SwitchCompat switch_sw) {
        int[][] states = new int[][] {
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_checked},
                new int[]{}
        };

        int[] colors = new int[] {
                R.color.settingsPrimary,
                R.color.material_grey_500,
                R.color.snifferPrimary
        };

        ColorStateList myList = new ColorStateList(states, colors);
        switch_sw.setThumbTintList(myList);
    }

    private void                addViewSettingsToListSettings() {

    }
}
