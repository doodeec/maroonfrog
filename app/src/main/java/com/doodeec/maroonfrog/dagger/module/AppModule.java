package com.doodeec.maroonfrog.dagger.module;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.res.Resources;

import com.doodeec.maroonfrog.dagger.scope.ApplicationScope;
import com.doodeec.maroonfrog.data.storage.DBHelper;
import com.doodeec.maroonfrog.data.storage.IDatabase;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Base DI Application module
 *
 * @author Dusan Bartos
 */
@Module(includes = {ApiModule.class})
public class AppModule {
    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides @ApplicationScope Application providesApplication() {
        return application;
    }

    @Provides @ApplicationScope Resources providesResources() {
        return application.getResources();
    }

    @Provides @ApplicationScope IDatabase providesDatabase() {
        return new DBHelper(application);
    }

    @Provides @ApplicationScope AccountManager providesAccountManager() {
        return AccountManager.get(application);
    }

    @Provides @ApplicationScope Scheduler providesMainScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides @ApplicationScope Picasso providesPicasso() {
        return new Picasso.Builder(application)
                .listener((picasso, uri, exception) -> Timber.e("Error loading image %s : %s", uri, exception.getMessage()))
                .build();
    }
}
