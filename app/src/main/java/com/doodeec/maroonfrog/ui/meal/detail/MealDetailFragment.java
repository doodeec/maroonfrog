package com.doodeec.maroonfrog.ui.meal.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.doodeec.maroonfrog.App;
import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.base.InjectableMvpFragment;
import com.doodeec.maroonfrog.base.Layout;
import com.doodeec.maroonfrog.dagger.AbstractStateHolder;
import com.doodeec.maroonfrog.dagger.component.DaggerMealDetailComponent;
import com.doodeec.maroonfrog.dagger.component.MealDetailComponent;
import com.doodeec.maroonfrog.dagger.module.ContextModule;
import com.doodeec.maroonfrog.dagger.scope.ActivityScope;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

import static android.text.TextUtils.join;
import static android.text.TextUtils.split;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.fragment_meal_detail)
public class MealDetailFragment extends InjectableMvpFragment<MealDetailView, MealDetailPresenter>
        implements MealDetailView {

    @BindView(R.id.meal_name) TextView name;
    @BindView(R.id.meal_desc) TextView description;

    @BindView(R.id.addon_list) RecyclerView list;
    @BindView(R.id.picked_addon_list) RecyclerView pickedList;

    private MealDetailComponent mealDetailComponent;

    @Override protected void inject() {
        mealDetailComponent = DaggerMealDetailComponent.builder()
                .appComponent(App.getAppComponent())
                .contextModule(new ContextModule(this))
                .build();

        stateHolder = mealDetailComponent.stateHolder();
    }

    @NonNull @Override public MealDetailPresenter createPresenter() {
        return preparePresenter(mealDetailComponent.presenter());
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.initAddonList(list);
        presenter.initPickedAddonList(pickedList);
    }

    @Override public void onStop() {
        super.onStop();
        list.setAdapter(null);
        pickedList.setAdapter(null);
    }

    @Override public void onStart() {
        super.onStart();
        if (list != null && list.getAdapter() == null) {
            presenter.initAddonList(list);
        }
        if (pickedList != null && pickedList.getAdapter() == null) {
            presenter.initPickedAddonList(pickedList);
        }
    }

    @Override public void setName(String name) {
        this.name.setText(name);
        setTitle(name);
    }

    @Override public void setDescription(String description) {
        this.description.setText(description);
    }

    /**
     * Scoped State holder for maintaining instance state during orientation change
     */
    @ActivityScope
    public static class StateHolder extends AbstractStateHolder {

        private static final String DIVIDER = ",";

        private static final String STATE_ID = "expand_id";
        private static final String ADDONS = "addons";

        private static final String ID_COLLAPSED_ALL = "-1";
        private final BehaviorSubject<String> expandedIdentifier = BehaviorSubject.create(ID_COLLAPSED_ALL);
        private final BehaviorSubject<Set<String>> addons = BehaviorSubject.create(new HashSet<String>());

        @Inject StateHolder() {
            Timber.w("DETAIL:StateHolder");
        }

        @Override public void onSaveState(Bundle instanceState) {
            instanceState.putString(STATE_ID, expandedIdentifier.getValue());
            instanceState.putString(ADDONS, join(DIVIDER, addons.getValue()));
        }

        @Override public void onRestoreState(Bundle instanceState) {
            expandedIdentifier.onNext(instanceState.getString(STATE_ID, ID_COLLAPSED_ALL));
            String[] ids = split(instanceState.getString(ADDONS, ""), DIVIDER);
            addons.onNext(new HashSet<>(Arrays.asList(ids)));
        }

        Observable<String> observeExpandedIdentifier() {
            return expandedIdentifier.asObservable();
        }

        Observable<Set<String>> observePickedAddons() {
            return addons.asObservable();
        }

        void toggleExpanded(String id) {
            if (expandedIdentifier.getValue().equals(id)) {
                expandedIdentifier.onNext(ID_COLLAPSED_ALL);
            } else {
                expandedIdentifier.onNext(id);
            }
        }

        void togglePickedAddon(String id) {
            final Set<String> set = addons.getValue();
            if (set.contains(id)) {
                set.remove(id);
            } else {
                set.add(id);
            }
            addons.onNext(set);
        }
    }
}
