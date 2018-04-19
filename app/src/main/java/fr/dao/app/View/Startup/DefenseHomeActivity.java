package fr.dao.app.View.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RadioButton;

import fr.dao.app.R;
import fr.dao.app.View.Settings.SettingsActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;

public class DefenseHomeActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private DefenseHomeActivity mInstance = this;
    private ConstraintLayout    mCoordinatorLayout;
    private CardView            blue_card, dashboard_card, settings_card, red_card;
    private RadioButton         radioButton, radioButton2, radioButton3;

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_center);
        initXml();
    }

    protected void              onResume() {
        super.onResume();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.rootView);
        blue_card = findViewById(R.id.blue_card);
        red_card = findViewById(R.id.red_card);
        dashboard_card = findViewById(R.id.dashboard_card);
        settings_card = findViewById(R.id.settings_card);
        radioButton = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
    }

    private void                init() {
        red_card.setOnClickListener(onAttackclicked());
        blue_card.setOnClickListener(onDefenseClicked());
        settings_card.setOnClickListener(onSettingsClick());
    }

    private View.OnClickListener onSettingsClick() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(mInstance, SettingsActivity.class));
            }
        };
    }

    private View.OnClickListener onDefenseClicked() {
        return new View.OnClickListener() {
            public void onClick(View view) {

            }
        };
    }

    private View.OnClickListener onAttackclicked() {
        return new View.OnClickListener() {
            public void onClick(View view) {

            }
        };
    }

}
