package fr.dao.app.View.ZViewController.Behavior;

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

import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.State;
import fr.dao.app.Model.Unix.Os;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;

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
    public static void      loadDrawableInCircularImageView(Context context, Drawable ressource, ImageView imageView) {
        GlideApp.with(context)
                .load(ressource)
                .apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
    public static void      loadDrawableInImageView(Context context, int ressource, ImageView imageView, boolean override) {
        GlideRequest builder = GlideApp.with(context)
                .load(ressource)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade());
        if (override)
                builder.apply(new RequestOptions()
                        .fitCenter()
                        .override(100, 100));
        builder.into(imageView);
    }
    public static void      loadDrawableInImageView(MyActivity context, int ressource, final ImageView imageView, boolean override, boolean anim) {
        final GlideRequest builder = GlideApp.with(context)
                .load(ressource)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        if (!anim)
            builder.dontAnimate();
        else
            builder.transition(DrawableTransitionOptions.withCrossFade());
        if (override)
            builder.apply(new RequestOptions()
                    .fitCenter()
                    .override(100, 100));
        context.runOnUiThread(new Runnable() {
            public void run() {
                builder.into(imageView);
            }
        });
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

    public static void      setOsIcon(Host host, ImageView osImageView) {
        if (host != null && host.osType != null) {
            if (host.state == State.FILTERED && host.vendor.contains("Unknown")) {
                osImageView.setImageResource(R.mipmap.ic_unknow);
                return ;
            }
            setOsIcon(host.osType, osImageView);
            return;
        }
        osImageView.setImageResource(R.drawable.monitor);
    }

    public static void      setOsIcon(Os os, ImageView osImageView) {
        int ImageRessource;
        switch (os) {
            case Windows:
                ImageRessource = R.drawable.windows;
                break;
            case Cisco:
                ImageRessource = R.drawable.cisco;
                break;
            case Raspberry:
                ImageRessource = R.drawable.rasp;
                break;
            case QUANTA:
                ImageRessource = R.drawable.quanta;
                break;
            case Bluebird:
                ImageRessource = R.drawable.bluebird;
                break;
            case Apple://Need MacBOOK, MacAIR, Iphone, AppleTV
                ImageRessource = R.drawable.ios;
                break;
            case Ios:
                ImageRessource = R.drawable.ios;
                break;
            case Unix:
                ImageRessource = R.drawable.linuxicon;
                break;
            case Linux_Unix:
                ImageRessource = R.drawable.linuxicon;
                break;
            case OpenBSD:
                ImageRessource = R.drawable.linuxicon;
                break;
            case Android:
                ImageRessource = R.drawable.android8;
                break;
            case Mobile:
                ImageRessource = R.mipmap.ic_logo_android_trans_round;
                break;
            case Samsung:
                ImageRessource = R.mipmap.ic_logo_android_trans_round;
                break;
            case Ps4:
                ImageRessource = R.drawable.ps4;
                break;
            case Gateway:
                ImageRessource = R.drawable.router1;
                break;
            case Unknow:
                ImageRessource = R.drawable.secure_computer1;
                break;
            default:
                ImageRessource = R.drawable.router3;
                break;
        }
        osImageView.setImageResource(ImageRessource);
    }
}
