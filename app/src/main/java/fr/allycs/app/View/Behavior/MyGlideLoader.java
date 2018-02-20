package fr.allycs.app.View.Behavior;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
;
import fr.allycs.app.Core.Configuration.GlideApp;
import fr.allycs.app.Core.Configuration.GlideRequest;
import fr.allycs.app.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class                MyGlideLoader {

    public static void      loadDrawableInCircularImageView(Context context, int ressource, ImageView imageView) {
        GlideApp.with(context)
                .load(ressource)
                .apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
    public static void      loadDrawableInCircularImageView(Context context, Drawable    ressource, ImageView imageView) {
        GlideApp.with(context)
                .load(ressource)
                .apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
    public static void      loadDrawableInImageView(Context context, int ressource, ImageView imageView, boolean override) {
        GlideRequest r = GlideApp.with(context)
                .load(ressource)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade());
        if (override)
                r.apply(new RequestOptions()
                        .fitCenter()
                        .override(100, 100));
        r.into(imageView);
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
