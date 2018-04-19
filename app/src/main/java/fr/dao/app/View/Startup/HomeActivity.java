package fr.dao.app.View.Startup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.ImageView;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Dora;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.DoraAdapter;

public class HomeActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private HomeActivity        mInstance = this;
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

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_center);
        initXml();
    }

    protected void              onResume() {
        super.onResume();
    }

    private void                initXml() {
    }

}
