package fr.allycs.app.Controller.AndroidUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import fr.allycs.app.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class                MyGlideLoader {

    public static void      loadDrawableInImageView(Context context, int ressource, ImageView imageView) {
        GlideApp.with(context)
                .load(ressource)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(new RequestOptions()
                        .fitCenter()
                        .override(100, 100))
                .placeholder(R.drawable.ico)
                .into(imageView);
    }


    public static void      loadDrawableInImageViewNoOverride(Context context, int ressource, ImageView imageView) {
        GlideApp.with(context)
                .load(ressource)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ico)
                .into(imageView);
    }

    public static void      coordoBackgroundXMM(Activity context, final CoordinatorLayout layout) {
        GlideApp.with(context)
                .load(R.drawable.splashscreen)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(withCrossFade())
                .placeholder(R.drawable.ico)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        layout.setBackground(resource);
                    }
                });
    }
    public static void      coordoBackgroundXMM(Activity context, final View layout) {
        GlideApp.with(context)
                .load(R.drawable.splashscreen)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(withCrossFade())
                .placeholder(R.drawable.ico)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        layout.setBackground(resource);
                    }
                });
    }
}
