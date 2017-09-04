package su.sniff.cepter.Controller.System;

import android.support.v7.app.AppCompatActivity;

import su.sniff.cepter.Controller.System.Wrapper.ArpSpoof;


public class                    MyActivity extends AppCompatActivity {
    protected String            TAG = this.getClass().getName();
    protected MyActivity        mInstance = this;


    @Override
    protected void              onDestroy() {
        ArpSpoof.stopArpSpoof();
        super.onDestroy();
    }
}
