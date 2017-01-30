package com.doodeec.maroonfrog;

import android.app.Application;

import com.doodeec.maroonfrog.dagger.component.AppComponent;
import com.doodeec.maroonfrog.dagger.component.DaggerAppComponent;
import com.doodeec.maroonfrog.dagger.module.AppModule;
import com.doodeec.maroonfrog.dagger.module.NetModule;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public class App extends Application {

    private static AppComponent appComponent;

    private Thread.UncaughtExceptionHandler defaultUEH;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(AppConfig.BASE_SERVER_URL))
                .build();

        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            ex.printStackTrace();
            Timber.e(ex, "Unhandled exception");
            defaultUEH.uncaughtException(thread, ex);
        });

        Picasso.setSingletonInstance(appComponent.picasso());

        //init logs
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        //init fabric - crashlytics
        //TODO set crashlytics key and uncomment its definition in manifest
        /*if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }*/
    }

    @Override
    public void onLowMemory() {
        Timber.d("onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Timber.d("onTrimMemory - %s", level);
        super.onTrimMemory(level);
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
