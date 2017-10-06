package su.sniff.cepter.Controller.System;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import su.sniff.cepter.Controller.System.Wrapper.ArpSpoof;
import su.sniff.cepter.R;


public class                    MyActivity extends AppCompatActivity {
    protected String            TAG = this.getClass().getName();
    protected MyActivity        mInstance = this;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void              onDestroy() {
        ArpSpoof.stopArpSpoof();
        super.onDestroy();
    }
}
