package com.doodeec.maroonfrog.ui.meal.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.doodeec.maroonfrog.App;
import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.base.InjectableMvpFragment;
import com.doodeec.maroonfrog.base.Layout;
import com.doodeec.maroonfrog.dagger.AbstractStateHolder;
import com.doodeec.maroonfrog.dagger.component.DaggerMealListComponent;
import com.doodeec.maroonfrog.dagger.component.MealListComponent;
import com.doodeec.maroonfrog.dagger.module.ContextModule;
import com.doodeec.maroonfrog.dagger.scope.ActivityScope;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.fragment_meal_list)
public class MealListFragment extends InjectableMvpFragment<MealListView, MealListPresenter>
        implements MealListView {

    @BindView(R.id.meal_list) RecyclerView list;

    private MealListComponent mealListComponent;

    @Override protected void inject() {
        Timber.v("INJECT");
        mealListComponent = DaggerMealListComponent.builder()
                .appComponent(App.getAppComponent())
                .contextModule(new ContextModule(this))
                .build();

        stateHolder = mealListComponent.stateHolder();
    }

    @NonNull @Override public MealListPresenter createPresenter() {
        return preparePresenter(mealListComponent.presenter());
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.initMealList(list);
    }

    @Override public void onStop() {
        super.onStop();
        list.setAdapter(null);
    }

    @Override public void onStart() {
        super.onStart();
        if (list != null && list.getAdapter() == null) {
            presenter.initMealList(list);
        }
    }

    /**
     * Scoped State holder for maintaining instance state during orientation change
     */
    @ActivityScope
    public static class StateHolder extends AbstractStateHolder {

        private static final String STATE_ID = "expand_id";

        private static final String ID_COLLAPSED_ALL = "-1";
        private final BehaviorSubject<String> expandedIdentifier = BehaviorSubject.create(ID_COLLAPSED_ALL);

        @Inject StateHolder() {
            Timber.w("LIST:StateHolder");
        }

        @Override public void onSaveState(Bundle instanceState) {
            instanceState.putString(STATE_ID, expandedIdentifier.getValue());
        }

        @Override public void onRestoreState(Bundle instanceState) {
            expandedIdentifier.onNext(instanceState.getString(STATE_ID, ID_COLLAPSED_ALL));
        }

        Observable<String> observeExpandedIdentifier() {
            return expandedIdentifier.asObservable();
        }

        void toggleExpanded(String id) {
            if (expandedIdentifier.getValue().equals(id)) {
                expandedIdentifier.onNext(ID_COLLAPSED_ALL);
            } else {
                expandedIdentifier.onNext(id);
            }
        }
    }
}
