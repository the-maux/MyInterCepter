package fr.dao.app.View.Startup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import fr.dao.app.Core.Configuration.Setup;

public class                    SplashscreenActivity extends AppCompatActivity {
    private SplashscreenActivity mInstance = this;


    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Setup.buildPath(this);

    }



}
