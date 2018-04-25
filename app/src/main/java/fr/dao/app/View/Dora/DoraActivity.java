package fr.dao.app.View.Dora;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Dora;
import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Unix.DoraProcess;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.DoraAdapter;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Dialog.QuestionDialog;

public class                    DoraActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private DoraActivity        mInstance = this;
    private Dora                mDoraWrapper;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private AppBarLayout        appBarLayout;
    private SearchView          searchView;
    private TabItem             radar, signalQuality;
    private ImageView           add, more;
    private RecyclerView        mRV_dora;
    private DoraAdapter         mRv_Adapter;
    private FloatingActionButton mFab;
    private int                 REFRESH_TIME = 1000;// == 1seconde
    ProgressDialog              dialog;

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dora);
        initXml();
    }

    protected void              onResume() {
        super.onResume();
        getDoraWrapper();
        initRV();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        searchView =  findViewById(R.id.searchView);
        radar = findViewById(R.id.radar);
        signalQuality = findViewById(R.id.signalQuality);
        add = findViewById(R.id.add);
        more = findViewById(R.id.action_add_host);
        mRV_dora = findViewById(R.id.RV_dora);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchDiagnose();
            }
        });
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
    }

    private void                getDoraWrapper() {
        mDoraWrapper = Dora.getDora(this);
        if (!NetworkDiscoveryControler.isHostListLoaded()) {
            new QuestionDialog(mInstance)

                    .setTitle("Vous devez scannez le reseau pour trouver des cibles")
                    .setText("Voulez vous scannez le reseau pour découvrir des hosts ?")
                    .onPositiveButton("Search device", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            NetworkDiscoveryControler.getInstance(mInstance).run(true);
                            mInstance.dialog = ProgressDialog.show(mInstance, "Discovering Network", "Loading. Please wait...", true);
                        }
                    })
                    .onNegativeButton("Dont", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showSnackbar("Dora is not fonctinal without target");
                        }
                    })
                    .show();
        } else
            ViewAnimate.FabAnimateReveal(mInstance, mFab);
        mFab.setImageResource((!Dora.isRunning()) ? R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DoraProcess doraProcess : mDoraWrapper.getmListOfHostDored()) {
                    if (Dora.isRunning()) {
                        doraProcess.reset();
                    }
                }
            }
        });
    }

    private void                initRV() {
        mRv_Adapter = new DoraAdapter(mInstance, mDoraWrapper.getmListOfHostDored());
        mRV_dora.setAdapter(mRv_Adapter);
        mRV_dora.setHasFixedSize(true);
        mRV_dora.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
    }

    private void                launchDiagnose() {
        //TODO: checkez qu'on a internet !!
        //TODO: make a loading visible
        if (mSingleton.hostList == null || mSingleton.hostList.isEmpty()) {
            Log.d(TAG, "onHostActualized::mSingleton.hostList is null or empty");
            if (!NetworkDiscoveryControler.getInstance(this).inLoading)
                NetworkDiscoveryControler.getInstance(this).run(true);
            else
                Log.d(TAG, "onHostActualized::mSingleton.hostList is null or empty but scan is already running");
        } else {
            Log.d(TAG, "onHostActualized::starting RV initialisation");
            boolean isStarting = mDoraWrapper.onAction();//TODO: make a loading visible
            mRv_Adapter.setIsRunning(isStarting);
            mFab.setImageResource((!isStarting) ? R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
        }
    }

    public void                 adapterRefreshDeamon() {
        if (Dora.isRunning()) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mInstance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Dora.isRunning()) {
                                mRv_Adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    handler.postDelayed(this, REFRESH_TIME);
                }
            }, REFRESH_TIME);
        }
    }

    public void                 onHostActualized(ArrayList<Host> hosts) {
        super.onHostActualized(hosts);
        Log.d(TAG, "onHostActualized::"+hosts.size());
        mRv_Adapter.updateDoraListHost(mDoraWrapper.getmListOfHostDored());
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public void                 showSnackbar(String txt) {
        super.showSnackbar(txt);
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }
}
