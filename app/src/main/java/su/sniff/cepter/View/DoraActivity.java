package su.sniff.cepter.View;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import com.github.clans.fab.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.Controller.System.Wrapper.RootProcess;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Pcap.DoraProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.DoraAdapter;

public class                    DoraActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private DoraActivity        mInstance = this;
    private List<DoraProcess>   mListOfHostDored;
    private boolean             mIsRunning = false;

    private CoordinatorLayout   mCoordinatorLayout;
    private SearchView          searchView;
    private TabItem             radar, signalQuality;
    private ImageView           add, more;
    private RecyclerView        mRV_dora;
    private DoraAdapter         mRv_Adapter;
    private FloatingActionButton mFab;

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dora);
        initXml();
        initDoraList();
        initRV();
    }

    private void                initXml() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
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
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DoraProcess doraProcess : mListOfHostDored) {
                    if (mIsRunning) {
                        doraProcess.reset();
                    }
                }
            }
        });
    }

    private void                initDoraList() {
        mListOfHostDored = new ArrayList<>();
        if (Singleton.getInstance().hostsList == null) {
            Snackbar.make(mCoordinatorLayout, "No target selected", Snackbar.LENGTH_LONG);
        } else {
            for (Host host : Singleton.getInstance().hostsList) {
                mListOfHostDored.add(new DoraProcess(host));
            }
        }
    }

    private void                initRV() {
        mRv_Adapter = new DoraAdapter(mInstance, mListOfHostDored);
        mRV_dora.setAdapter(mRv_Adapter);
        mRV_dora.setHasFixedSize(true);
        mRV_dora.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
    }

    private void                launchDiagnose() {
        if (!mIsRunning) {
            mIsRunning = true;
            for (DoraProcess doraProcess : mListOfHostDored) {
                doraProcess.exec();
            }
            adapterRefreshDeamon();
            Log.d(TAG, "diagnose dora started");
        } else {
            mIsRunning = false;
            for (DoraProcess doraProcess : mListOfHostDored) {
                RootProcess.kill(doraProcess.mProcess.getPid());
            }
            Log.d(TAG, "diagnose dora stopped");
        }
        mRv_Adapter.setRunning(mIsRunning);
        mFab.setImageResource((!mIsRunning) ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
    }

    private int                 REFRESH_TIME = 1000;// == 1seconde
    private void                adapterRefreshDeamon() {
        if (mIsRunning) {
            final Handler handler = new Handler();
            handler.postDelayed( new Runnable() {

                @Override
                public void run() {
                    mInstance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsRunning) {
                                mRv_Adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    handler.postDelayed( this, REFRESH_TIME );
                }
            }, REFRESH_TIME );
        }
    }

    @Override
    public void                 onBackPressed() {
        mIsRunning = false;
        RootProcess.kill("ping");
        super.onBackPressed();
    }
}
