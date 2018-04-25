package fr.dao.app.View.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import fr.dao.app.Core.Configuration.SettingsControler;

public class                    SplashscreenActivity extends AppCompatActivity {
    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(
                new Intent(this,
                new File(/* Ok Ok maybe not the best way to discover the code of the app xD */
                        this.getFilesDir().getPath() + '/',
                        SettingsControler.NAME_FILE_PREFERENCE).exists() ?
                        HomeActivity.class : SetupActivity.class));
    }
}
