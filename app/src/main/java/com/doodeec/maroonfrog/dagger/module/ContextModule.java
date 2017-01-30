package com.doodeec.maroonfrog.dagger.module;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.doodeec.maroonfrog.dagger.scope.ActivityScope;

import dagger.Provides;

/**
 * @author Dusan Bartos
 */
@dagger.Module
public class ContextModule {
    private Fragment fragment;

    public ContextModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides @ActivityScope Context provideContext() {
        return fragment.getContext();
    }
}