package su.sniff.cepter.Utils;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import su.sniff.cepter.BuildConfig;
import su.sniff.cepter.R;
import su.sniff.cepter.View.*;
import su.sniff.cepter.globalVariable;

public class TabActivitys extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.tabtest_layout);
        getWindow().setFeatureDrawableResource(3, R.drawable.ico);
        globalVariable.parent = this;
        TabHost tabHost = getTabHost();
        String str = getIntent().getExtras().getString("Key_String");
        String orig_str = getIntent().getExtras().getString("Key_String_origin");
        Intent i1 = new Intent(this, MainActivity.class);
        i1.putExtra("Key_String", str);
        i1.putExtra("Key_String_origin", orig_str);
        Intent i2 = new Intent(this, RawActivity.class);
        i2.putExtra("Key_String", str);
        i2.putExtra("Key_String_origin", orig_str);
        Intent i3 = new Intent(this, SettingsActivity.class);
        i3.putExtra("Key_String", str);
        i3.putExtra("Key_String_origin", orig_str);
        Intent i4 = new Intent(this, WebActivity.class);
        Intent i5 = new Intent(this, GalleryActivity.class);
        Intent i6 = new Intent(this, GalleryActivity.class);
        Resources res = getResources();
        TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(BuildConfig.FLAVOR, res.getDrawable(R.drawable.poiz));
        tabSpec.setContent(i1);
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(BuildConfig.FLAVOR, res.getDrawable(R.drawable.shark));
        tabSpec.setContent(i2);
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator(BuildConfig.FLAVOR, res.getDrawable(R.drawable.cookie));
        tabSpec.setContent(i4);
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag5");
        tabSpec.setIndicator(BuildConfig.FLAVOR, res.getDrawable(R.drawable.gallery));
        tabSpec.setContent(i5);
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag4");
        tabSpec.setIndicator(BuildConfig.FLAVOR, res.getDrawable(R.drawable.settings));
        tabSpec.setContent(i3);
        tabHost.addTab(tabSpec);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
