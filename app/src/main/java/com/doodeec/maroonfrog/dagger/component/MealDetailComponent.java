package com.doodeec.maroonfrog.dagger.component;

import android.content.Context;

import com.doodeec.maroonfrog.dagger.module.ContextModule;
import com.doodeec.maroonfrog.dagger.scope.ActivityScope;
import com.doodeec.maroonfrog.ui.meal.detail.MealDetailFragment;
import com.doodeec.maroonfrog.ui.meal.detail.MealDetailPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Dusan Bartos
 */
@ActivityScope
@Component(modules = ContextModule.class, dependencies = {AppComponent.class})
public interface MealDetailComponent {
    MealDetailPresenter presenter();

    Context context();

    @Singleton MealDetailFragment.StateHolder stateHolder();
}
