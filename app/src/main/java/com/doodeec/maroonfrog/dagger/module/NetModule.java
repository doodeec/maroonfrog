package com.doodeec.maroonfrog.dagger.module;

import com.doodeec.maroonfrog.BuildConfig;
import com.doodeec.maroonfrog.dagger.scope.ApplicationScope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DI module for network communication
 * provides network specific constructors for partial components
 * - GSON
 * - OkHttp client
 * - logging and header interceptors
 * - Retrofit instance
 *
 * @author Dusan Bartos
 */
@Module
public class NetModule {

    // base URL for server communication
    private String baseUrl;

    public NetModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides @ApplicationScope Gson provideGson() {
        return new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    @Provides @ApplicationScope HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        return interceptor;
    }

    @Provides @ApplicationScope Interceptor provideHeaderInterceptor() {
        return chain -> {
            Request original = chain.request();
//            Log.v("Request", original.url().toString());

            Request request = original.newBuilder()
//                    .addHeader("access_token", AppConfig.TOKEN)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };
    }

    @Provides @ApplicationScope OkHttpClient provideClient(HttpLoggingInterceptor interceptor,
                                                           Interceptor headersInterceptor) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(headersInterceptor);
        clientBuilder.addInterceptor(interceptor);
        return clientBuilder.build();
    }

    @Provides @ApplicationScope Retrofit provideRetro(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }
}
