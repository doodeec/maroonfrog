package com.doodeec.maroonfrog.dagger.component;

import android.content.Context;

import com.doodeec.maroonfrog.dagger.module.ContextModule;
import com.doodeec.maroonfrog.dagger.scope.ActivityScope;
import com.doodeec.maroonfrog.ui.meal.list.MealListFragment;
import com.doodeec.maroonfrog.ui.meal.list.MealListPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Dusan Bartos
 */
@ActivityScope
@Component(modules = ContextModule.class, dependencies = AppComponent.class)
public interface MealListComponent {
    MealListPresenter presenter();

    Context context();

    @Singleton MealListFragment.StateHolder stateHolder();
}
