package su.sniff.cepter.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;

import su.sniff.cepter.R;

/**
 * Created by maxim on 03/08/2017.
 */

public class                    NmapActivity extends Activity {
    private String              TAG = this.getClass().getName();
    private NmapActivity        mInstance = this;
    private CoordinatorLayout   coordinatorLayout;

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initXml();
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);

    }
}
