package su.sniff.cepter.Controller.System;

import android.app.Activity;

import su.sniff.cepter.Controller.System.Wrapper.ArpSpoof;


public class                    MyActivity extends Activity {
    protected String            TAG = this.getClass().getName();
    protected MyActivity        mInstance = this;


    @Override
    protected void              onDestroy() {
        ArpSpoof.stopArpSpoof();
        super.onDestroy();
    }
}
