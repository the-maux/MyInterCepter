package fr.dao.app.View.Startup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Setup;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.R;
import fr.dao.app.View.DashBoard.DashboardActivity;
import fr.dao.app.View.HostDiscovery.HostDiscoveryActivity;
import fr.dao.app.View.Settings.SettingsActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;

public class                    HomeActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private HomeActivity        mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private CardView            blue_card, dashboard_card, settings_card, red_card;
    private RadioButton         radioButton, radioButton2, radioButton3;
    private int                 MAXIMUM_TRY_PERMISSION = 5, try_permission = 0;
    private View                monitorRoot, monitorPermission, monitorUpdated;
    private TextView            TV_Root, TV_Permission, TV_Updated;
    private ProgressBar         PB_Root, PB_Permission, PB_Updated;
    private CircleImageView     statusRoot, statusPermission, statusUpdated;
    private static final int    PERMISSIONS_MULTIPLE_REQUEST = 123;

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initXml();
        init();
    }

    protected void              onPostResume() {
        super.onPostResume();
        Setup.buildPath(this);
        getRootPermission();
        getAndroidPermission();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        blue_card = findViewById(R.id.blue_card);
        red_card = findViewById(R.id.red_card);
        dashboard_card = findViewById(R.id.dashboard_card);
        settings_card = findViewById(R.id.settings_card);
        monitorRoot = findViewById(R.id.monitorRoot);
        monitorPermission = findViewById(R.id.monitorPermission);
        monitorUpdated = findViewById(R.id.monitorUpdated);
        TV_Root = monitorRoot.findViewById(R.id.titleCard);
        TV_Permission = monitorPermission.findViewById(R.id.titleCard);
        TV_Updated = monitorUpdated.findViewById(R.id.titleCard);
        statusRoot = monitorRoot.findViewById(R.id.statusIconCardView);
        statusPermission = monitorPermission.findViewById(R.id.statusIconCardView);
        statusUpdated = monitorUpdated.findViewById(R.id.statusIconCardView);
        TV_Root.setText("Root");
        TV_Permission.setText("Permission");
        TV_Updated.setText("New release");
        PB_Root = monitorRoot.findViewById(R.id.progressBar_monitor);
        PB_Permission = monitorPermission.findViewById(R.id.progressBar_monitor);
        PB_Updated = monitorUpdated.findViewById(R.id.progressBar_monitor);
    }

    private void                init() {
        red_card.setOnClickListener(onAttackclicked());
        blue_card.setOnClickListener(onDefenseClicked());
        settings_card.setOnClickListener(onSettingsClick());
        dashboard_card.setOnClickListener(onDashboardClick());
        NetDiscovering.initNetworkInfo(mInstance);
        initBottomMonitor();
    }

    private void                initBottomMonitor() {
        statusRoot.setImageResource(R.color.material_deep_orange_400);
        statusPermission.setImageResource(R.color.material_deep_orange_400);
    }

    private void                getAndroidPermission() {
        String[] PERMISSION_STORAGE = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        PB_Permission.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, PERMISSIONS_MULTIPLE_REQUEST);
        } else {

            statusPermission.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.online_color)));
            PB_Permission.setVisibility(View.GONE);
        }
    }

    public void                 onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Vous ne pouvez pas utiliser l'application sans ces permissions");
        }
        getAndroidPermission();
    }
    private View.OnClickListener onDashboardClick() {
        return new View.OnClickListener() {
            public void onClick(View view) {
               startActivity(new Intent(mInstance, DashboardActivity.class));
            }
        };
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
                startActivity(new Intent(mInstance, DefenseHomeActivity.class));
            }
        };
    }

    private View.OnClickListener onAttackclicked() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(mInstance, HostDiscoveryActivity.class));
            }
        };
    }

    private void                getRootPermission() {
        PB_Root.setVisibility(View.VISIBLE);
        Log.d(TAG, "getRootPermission");
        if (rootCheck()) {
            statusRoot.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.online_color)));
            PB_Root.setVisibility(View.GONE);
            return;
        } else {
            Log.d(TAG, "getRootPermission::Error::Retry");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (try_permission++ > MAXIMUM_TRY_PERMISSION) {
                        showSnackbar("Root: permission denied");
                        statusRoot.setImageResource(R.color.offline_color);
                    } else
                        getRootPermission();
                }
            }, 5000);
        }
    }

    private boolean             rootCheck() {
        try {
            String RootOk = new RootProcess("RootCheck").exec("id").getReader().readLine();
            return (RootOk != null && RootOk.contains("uid=0(root)"));
        } catch (IOException e) {
            Log.d("Splashscreen", "RootOK[IOException]");
            e.printStackTrace();
            return false;
        }
    }

    public void                 onBackPressed() {
        finish();
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }
}
