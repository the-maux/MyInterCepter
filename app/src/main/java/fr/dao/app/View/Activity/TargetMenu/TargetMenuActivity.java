package fr.dao.app.View.Activity.TargetMenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.MyGlideLoader;

/**
 * Menu
 */
public class                    TargetMenuActivity extends MyActivity {
    private String              TAG = "TargetMenuActivity";
    private TargetMenuActivity  mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private Singleton           mSingleton = Singleton.getInstance();

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
    }

    protected void              onResume() {
        super.onResume();
        RecyclerView RV_menu = findViewById(R.id.RV_menu);
        RV_menu.setAdapter(new MenuAdapter(this));
        RV_menu.setLayoutManager(new GridLayoutManager(this, 2));
    }

    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

}
