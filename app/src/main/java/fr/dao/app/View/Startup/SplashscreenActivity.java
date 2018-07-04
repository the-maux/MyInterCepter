package fr.dao.app.View.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;

import fr.dao.app.Core.Configuration.SettingsControler;
import fr.dao.app.R;

public class                    SplashscreenActivity extends AppCompatActivity {
    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.generic_background));
        Intent i = new Intent(this,
                new File(/* Ok Ok maybe not the best way to discover the code of the app xD */
                        this.getFilesDir().getPath() + '/',
                        SettingsControler.NAME_FILE_PREFERENCE).exists() ?
                        HomeActivity.class : SetupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }
}
