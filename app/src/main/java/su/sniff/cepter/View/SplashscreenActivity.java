package su.sniff.cepter.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class SplashscreenActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashscreenActivity.this, SetupActivity.class));
        Log.d("SplashscreenActivity ", "finish()");
        finish();
    }
}
