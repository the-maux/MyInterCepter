package su.sniff.cepter.View;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import com.github.clans.fab.FloatingActionButton;

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
    private List<DoraProcess>   listOfHostDored;
    private boolean             running = false;

    private CoordinatorLayout   coordinatorLayout;
    private SearchView          searchView;
    private TabItem             radar, signalQuality;
    private ImageView           add, more;
    private RecyclerView        RV_dora;
    private DoraAdapter         Rv_Adapter;
    private FloatingActionButton fab;

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dora);
        initXml();
        initDoraList();
        initRV();
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        searchView = (SearchView) findViewById(R.id.searchView);
        radar = (TabItem) findViewById(R.id.radar);
        signalQuality = (TabItem) findViewById(R.id.signalQuality);
        add = (ImageView) findViewById(R.id.add);
        more = (ImageView) findViewById(R.id.more);
        RV_dora = (RecyclerView) findViewById(R.id.RV_dora);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDiagnose();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DoraProcess doraProcess : listOfHostDored) {
                    if (running) {
                        doraProcess.reset();
                    }
                }
            }
        });
    }

    private void                initDoraList() {
        listOfHostDored = new ArrayList<>();
        for (Host host : Singleton.getInstance().hostsList) {
            listOfHostDored.add(new DoraProcess(host));
        }
    }

    private void                initRV() {
        Rv_Adapter = new DoraAdapter(mInstance, listOfHostDored);
        RV_dora.setAdapter(Rv_Adapter);
        RV_dora.setHasFixedSize(true);
        RV_dora.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
    }

    private void                launchDiagnose() {
        if (!running) {
            running = true;
            for (DoraProcess doraProcess : listOfHostDored) {
                doraProcess.exec();
            }
            adapterRefreshDeamon();
            Log.d(TAG, "diagnose dora started");
        } else {
            running = false;
            Log.d(TAG, "diagnose dora stopped");
            RootProcess.kill("ping");
        }
        fab.setImageResource((!running) ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
    }

    private int                 REFRESH_TIME = 1000;// == 1seconde
    private void                adapterRefreshDeamon() {
        if (running) {
            final Handler handler = new Handler();
            handler.postDelayed( new Runnable() {

                @Override
                public void run() {
                    mInstance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (running) {
                                Rv_Adapter.notifyDataSetChanged();
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
        running = false;
        RootProcess.kill("ping");
        super.onBackPressed();
    }
}
