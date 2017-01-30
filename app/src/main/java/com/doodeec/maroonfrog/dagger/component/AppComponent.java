package com.doodeec.maroonfrog.dagger.component;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.res.Resources;

import com.doodeec.maroonfrog.dagger.module.ApiModule;
import com.doodeec.maroonfrog.dagger.module.AppModule;
import com.doodeec.maroonfrog.dagger.scope.ApplicationScope;
import com.doodeec.maroonfrog.data.provider.DataProvider;
import com.doodeec.maroonfrog.data.provider.StorageManager;
import com.doodeec.maroonfrog.data.storage.IDatabase;
import com.doodeec.maroonfrog.network.RestService;
import com.doodeec.maroonfrog.ui.widget.collection.ObservableAdapterFactory;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import dagger.Component;
import rx.Scheduler;

/**
 * DI component
 *
 * @author Dusan Bartos
 */
@ApplicationScope
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {
    DataProvider dataProvider();

    StorageManager storageManager();

    AccountManager accountManager();

    Resources resources();

    Application application();

    IDatabase dbHelper();

    RestService restService();

    Scheduler mainScheduler();

    Picasso picasso();

    Gson gson();

    ObservableAdapterFactory observableAdapterFactory();
}
