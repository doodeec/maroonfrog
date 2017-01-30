package com.doodeec.maroonfrog.ui.meal.list;

import android.app.Application;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.doodeec.maroonfrog.base.RxMvpBasePresenter;
import com.doodeec.maroonfrog.dagger.scope.ActivityScope;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.model.MealAdapterItem;
import com.doodeec.maroonfrog.data.model.MealCategory;
import com.doodeec.maroonfrog.data.provider.DataProvider;
import com.doodeec.maroonfrog.ui.meal.detail.MealDetailActivity;
import com.doodeec.maroonfrog.ui.widget.collection.ObservableAdapter;
import com.doodeec.maroonfrog.ui.widget.collection.ObservableAdapterFactory;
import com.doodeec.maroonfrog.util.rx.RxUtils;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
@ActivityScope
public class MealListPresenter extends RxMvpBasePresenter<MealListView> {

    private final Application application;
    private final DataProvider dataProvider;
    private final Scheduler mainScheduler;
    private final MealListFragment.StateHolder stateHolder;

    private final ObservableAdapter<MealAdapterItem> adapter;
    private final MealListSource source;

    @Inject MealListPresenter(Application application,
                              DataProvider dataProvider,
                              Scheduler mainScheduler,
                              ObservableAdapterFactory adapterFactory,
                              MealListFragment.StateHolder stateHolder) {
        this.application = application;
        this.dataProvider = dataProvider;
        this.mainScheduler = mainScheduler;
        this.stateHolder = stateHolder;

        this.source = new MealListSource(Collections.emptyList());
        this.adapter = adapterFactory.create(source);
    }

    @Override public void attachView(MealListView view) {
        super.attachView(view);

        add(dataProvider.observeErrors()
                .observeOn(mainScheduler)
                .subscribe(t -> showError(t.getMessage()), baseErrorHandler("mealList:observeErrors")));

        setupAdapterEvents();
        setupSourceUpdates();

        dataProvider.refresh();
    }

    private void setupAdapterEvents() {
        add(adapter.onItemEvent()
                .ofType(ObservableAdapter.ObjectEvent.class)
                .map(Observable::just)
                .switchMap(x -> Observable.merge(
                        x.map(ObservableAdapter.ObjectEvent::getData)
                                .ofType(Meal.class)
                                .map(meal -> (Action0) () -> onMealClicked(meal)),
                        x.map(ObservableAdapter.ObjectEvent::getData)
                                .ofType(MealCategory.class)
                                .map(cat -> (Action0) () -> onMealCategoryClicked(cat))
                ))
                .subscribe(Action0::call, baseErrorHandler("mealList:itemEvent")));
    }

    private void setupSourceUpdates() {
        Observable<List<MealAdapterItem>> groupedMealsObservable = dataProvider.observeMeals()
                .flatMap(meals -> Observable.from(meals)
                        .groupBy(Meal::getCategory)
                        .concatMap(x -> x.ofType(MealAdapterItem.class).startWith(x.getKey()))
                        .toList());

        add(Observable.combineLatest(
                groupedMealsObservable,
                stateHolder.observeExpandedIdentifier(),
                (items, categoryId) -> Observable.from(items)
                        .doOnNext(prepareExpandFlag(categoryId))
                        .filter(filterCollapsedItems(categoryId))
                        .toList())
                .flatMap(x -> x)
                .subscribeOn(Schedulers.computation())
                .doOnNext(x -> Timber.d("Meal items updated: %s", x))
                .compose(RxUtils.handleErrorNever("Error observing meal items"))
                .observeOn(mainScheduler)
                .subscribe(source::setData, baseErrorHandler("mealList:observeSource")));
    }

    void initMealList(RecyclerView mealList) {
        mealList.setAdapter(adapter);
        //we don't want to add multiple decorators, so this is the simplest way
        if (mealList.getLayoutManager() == null) {
            mealList.setLayoutManager(new LinearLayoutManager(mealList.getContext()));
            mealList.addItemDecoration(new DividerItemDecoration(mealList.getContext(), DividerItemDecoration.VERTICAL));
        }
    }

    private Action1<MealAdapterItem> prepareExpandFlag(String expandedCatId) {
        return i -> {
            if (i instanceof MealCategory) {
                ((MealCategory) i).setExpanded(i.getId().equals(expandedCatId));
            }
        };
    }

    private Func1<MealAdapterItem, Boolean> filterCollapsedItems(String expandedCatId) {
        return i -> (i instanceof MealCategory) || mealBelongsToCategory(i, expandedCatId);
    }

    private boolean mealBelongsToCategory(MealAdapterItem item, String id) {
        return (item instanceof Meal && ((Meal) item).getCategory().getId().equals(id));
    }

    private void onMealClicked(Meal meal) {
        if (isViewAttached()) {
            getView().openActivity(new Intent(application, MealDetailActivity.class)
                    .putExtra(MealDetailActivity.MEALID, meal.getId()));
        }
    }

    private void onMealCategoryClicked(MealCategory category) {
        stateHolder.toggleExpanded(category.getId());
    }
}
