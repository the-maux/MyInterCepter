package fr.allycs.app.Controller.Misc;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import fr.allycs.app.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class                MyGlideLoader {

    public static void      loadDrawableInImageView(Context context, int ressource, ImageView imageView) {
        GlideApp.with(context)
                .load(ressource)
                .apply(new RequestOptions()
                        .fitCenter()
                        .override(100, 100))
                .placeholder(R.drawable.ico)
                .into(imageView);
    }


    public static void      loadDrawableInImageViewNoOverride(Context context, int ressource, ImageView imageView) {
        GlideApp.with(context)
                .load(ressource)
                .placeholder(R.mipmap.ic_add_button)
                .into(imageView);
    }

    public static void      coordoBackground(Activity context, final CoordinatorLayout layout) {
        GlideApp.with(context)
                .load(R.drawable.splashscreen)
                .transition(withCrossFade())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        layout.setBackground(resource);
                    }
                });
    }
    public static void      coordoBackground(Activity context, final RelativeLayout layout) {
        GlideApp.with(context)
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
