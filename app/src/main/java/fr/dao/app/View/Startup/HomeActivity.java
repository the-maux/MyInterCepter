package fr.dao.app.View.Startup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Database.DBSessions;
import fr.dao.app.R;
import fr.dao.app.View.DashBoard.DashboardActivity;
import fr.dao.app.View.HostDiscovery.HostDiscoveryActivity;
import fr.dao.app.View.Settings.SettingsActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class                    HomeActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private HomeActivity        mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private CardView            blue_card, dashboard_card, settings_card, red_card;
    private int                 MAXIMUM_TRY_PERMISSION = 5, try_permission = 0;
    private View                monitorRoot, monitorPermission, monitorUpdated;
    private TextView            TV_Root, TV_Permission, TV_Updated;
    private ProgressBar         PB_Root, PB_Permission, PB_Updated;
    private CardView            cardRoot, cardPermission, cardUpdated;
    private CircleImageView     statusRoot, statusPermission, statusUpdated;
    private static final int    PERMISSIONS_MULTIPLE_REQUEST = 123;
    private Singleton           mSingleton = Singleton.getInstance();

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSingleton.init(this);
        initXml();
        init();
    }

    protected void              onPostResume() {
        super.onPostResume();
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
        TV_Updated.setText("Internet");
        PB_Root = monitorRoot.findViewById(R.id.progressBar_monitor);
        PB_Permission = monitorPermission.findViewById(R.id.progressBar_monitor);
        PB_Updated = monitorUpdated.findViewById(R.id.progressBar_monitor);
        statusRoot.setImageResource(R.color.material_deep_orange_400);
        statusPermission.setImageResource(R.color.material_deep_orange_400);
        statusPermission.setImageResource(R.color.material_deep_orange_400);
        setStatusBarColor(R.color.generic_background);
        GlideApp.with(mInstance)
                .load(R.drawable.bg3)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(withCrossFade())
                .placeholder(R.drawable.ico)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        findViewById(R.id.rootView).setBackground(resource);
                    }
                });
        ViewAnimate.FabAnimateReveal(mInstance, blue_card, new Runnable() {
            public void run() {
                ViewAnimate.FadeAnimateReveal(mInstance, red_card, new Runnable() {
                    public void run() {
                        ViewAnimate.FadeAnimateReveal(mInstance, dashboard_card, new Runnable() {
                            public void run() {
                                ViewAnimate.FadeAnimateReveal(mInstance, settings_card, null);
                            }
                        });
                    }
                });
            }
        });
        ViewAnimate.FabAnimateReveal(mInstance, monitorRoot, new Runnable() {
            public void run() {
                ViewAnimate.FadeAnimateReveal(mInstance, monitorPermission, new Runnable() {
                    public void run() {
                        ViewAnimate.FadeAnimateReveal(mInstance, monitorUpdated, null);
                    }
                });
            }
        });
    }

    private void                init() {
        red_card.setOnClickListener(onAttackclicked());
        blue_card.setOnClickListener(onDefenseClicked());
        settings_card.setOnClickListener(onSettingsClick());
        dashboard_card.setOnClickListener(onDashboardClick());
        defensifCheck();
        mSingleton.Session = DBSessions.createOrUpdateSession();
    }

    private void                defensifCheck() {
        if (mSingleton.NetworkInformation == null || mSingleton.NetworkInformation.myIp == null) {
            showSnackbar("You need to be connected to a NetworkInformation");
            statusUpdated.setImageResource(R.color.offline_color);
        } else {
            statusUpdated.setImageResource(R.color.online_color);
        }
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

    private View.OnClickListener onDefenseClicked() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance, 100);
                Intent intent = new Intent(mInstance, DefenseHomeActivity.class);
                Pair<View, String> p1 = Pair.create(findViewById(R.id.logo_defense), "logo_defense2");
                Pair<View, String> p2 = Pair.create(findViewById(R.id.blue_card), "rootViewTransition");
                Pair<View, String> p3 = Pair.create(findViewById(R.id.title_defense), "title");
                startActivity(intent,  ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1, p2, p3).toBundle());
            }
        };
    }
    private View.OnClickListener onDashboardClick() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance, 100);
                Intent intent = new Intent(mInstance, DashboardActivity.class);
                Pair<View, String> p1 = Pair.create(findViewById(R.id.logo_dashboard), "logo_activity");
                Pair<View, String> p2 = Pair.create(findViewById(R.id.dashboard_card), "rootViewTransition");
                Pair<View, String> p3 = Pair.create(findViewById(R.id.title_dashboard), "title");
                startActivity(intent,  ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p2).toBundle());
            }
        };
    }
    private View.OnClickListener onSettingsClick() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance, 100);
                Intent intent = new Intent(mInstance, SettingsActivity.class);
                Pair<View, String> p1 = Pair.create(findViewById(R.id.logo_settings), "logo_activity");
                Pair<View, String> p2 = Pair.create(findViewById(R.id.dashboard_card), "rootViewTransition");
                Pair<View, String> p3 = Pair.create(findViewById(R.id.title_settings), "title");
                startActivity(intent,  ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1, p2, p3).toBundle());
            }
        };
    }
    private View.OnClickListener onAttackclicked() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance);
                runOnThreadDelay(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(mInstance, HostDiscoveryActivity.class);
                        Pair<View, String> p1 = Pair.create(findViewById(R.id.logo_attack), "logo_activity");
                        Pair<View, String> p3 = Pair.create(findViewById(R.id.title_attack), "title");
                        Pair<View, String> p2 = Pair.create(findViewById(R.id.red_card), "rootViewTransition");
                        startActivity(intent,  ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1, p2, p3).toBundle());
                    }
                });
            }
        };
    }

    private void                runOnThreadDelay(Runnable runnable) {
        mInstance.runOnUiThread(runnable);
    }

    private void                getRootPermission() {
        PB_Root.setVisibility(View.VISIBLE);
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
                    Utils.vibrateDevice(mInstance, 100);
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
