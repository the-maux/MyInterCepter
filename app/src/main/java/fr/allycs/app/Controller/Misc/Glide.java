package fr.allycs.app.Controller.Misc;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import fr.allycs.app.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class                Glide {
    public static void      putGenericBackground(Activity context, final CoordinatorLayout layout) {
        com.bumptech.glide.Glide.with(context)
                .load(R.drawable.splashscreen)
                .transition(withCrossFade())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        layout.setBackground(resource);
                    }
                });
    }
    public static void      putGenericBackground(Activity context, final RelativeLayout layout) {
        com.bumptech.glide.Glide.with(context)
                .load(R.drawable.splashscreen)
                .transition(withCrossFade())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        layout.setBackground(resource);
                    }
                });
    }
}
