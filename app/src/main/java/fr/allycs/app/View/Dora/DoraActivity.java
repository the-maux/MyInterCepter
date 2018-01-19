package fr.allycs.app.View.Dora;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Tools.Dora;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Misc.MyGlideLoader;
import fr.allycs.app.Model.Unix.DoraProcess;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Adapter.DoraAdapter;

public class                    DoraActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private DoraActivity        mInstance = this;
    private Dora mDoraWrapper;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private SearchView          searchView;
    private TabItem             radar, signalQuality;
    private ImageView           add, more;
    private RecyclerView        mRV_dora;
    private DoraAdapter         mRv_Adapter;
    private FloatingActionButton mFab;
    private int                 REFRESH_TIME = 1000;// == 1seconde

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dora);
        initXml();
    }

    @Override
    protected void              onResume() {
        super.onResume();
        getDoraWrapper();
        initRV();
    }

    private void                initXml() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackground(this, mCoordinatorLayout);

        searchView = (SearchView) findViewById(R.id.searchView);
        radar = (TabItem) findViewById(R.id.radar);
        signalQuality = (TabItem) findViewById(R.id.signalQuality);
        add = (ImageView) findViewById(R.id.add);
        more = (ImageView) findViewById(R.id.action_add_host);
        mRV_dora = (RecyclerView) findViewById(R.id.RV_dora);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDiagnose();
            }
        });

    }

    private void                getDoraWrapper() {
        mDoraWrapper = Dora.getDora(this);
        mFab.setImageResource((!mDoraWrapper.isRunning()) ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DoraProcess doraProcess : mDoraWrapper.getmListOfHostDored()) {
                    if (mDoraWrapper.isRunning()) {
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
        boolean isStarting = mDoraWrapper.onAction();
        mRv_Adapter.setRunning(isStarting);
        mFab.setImageResource((!isStarting) ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
    }

    public void                 adapterRefreshDeamon() {
        if (mDoraWrapper.isRunning()) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mInstance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDoraWrapper.isRunning()) {
                                mRv_Adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    handler.postDelayed(this, REFRESH_TIME);
                }
            }, REFRESH_TIME);
        }
    }
}
