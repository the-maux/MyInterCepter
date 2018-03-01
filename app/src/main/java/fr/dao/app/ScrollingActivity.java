package fr.dao.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Core.Configuration.MyAppGlideModule;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView collapsBackground = findViewById(R.id.collapsBackground);
        GlideRequest r = GlideApp.with(this)
                .load(R.drawable.bg1)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        r.into(collapsBackground);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        final ImageView osImg = findViewById(R.id.osImg);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int alpha = appBarLayout.getTotalScrollRange() - Math.abs(verticalOffset);
                if (alpha < 40) {
                    if (osImg.getVisibility() == View.VISIBLE)
                        osImg.animate()
                                .alpha(0.0f)
                                .setDuration(250)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        osImg.setVisibility(View.GONE);
                                    }
                                });
                } else {
                    if (osImg.getVisibility() == View.GONE)
                        osImg.animate()
                                .alpha(1.0f)
                                .setDuration(250)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        osImg.setVisibility(View.VISIBLE);
                                    }
                                });
                }
            }
        });
    }
}
