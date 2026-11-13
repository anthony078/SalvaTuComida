package com.example.salvatucomida;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.example.salvatucomida.BuildConfig;

import java.io.InputStream;

import okhttp3.OkHttpClient;

public class MiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            // Ignorar validación SSL para Glide en modo debug
            builder.hostnameVerifier((hostname, session) -> true);
        }

        Glide.get(this).getRegistry().replace(
                GlideUrl.class,
                InputStream.class,
                new OkHttpUrlLoader.Factory(builder.build())
        );
    }
}