package fr.allycs.app.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public class                    SplashscreenActivity extends AppCompatActivity {
    @Override public void       onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override protected void    onPostResume() {
        super.onPostResume();
        startActivity(new Intent(SplashscreenActivity.this, SetupActivity.class));
        finish();
    }
}
