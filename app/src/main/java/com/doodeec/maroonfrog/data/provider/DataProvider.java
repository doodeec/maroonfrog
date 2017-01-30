package com.doodeec.maroonfrog.data.provider;

import com.doodeec.maroonfrog.dagger.scope.ApplicationScope;
import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.DBEntity;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.util.rx.RxUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Abstraction over Storage and Network Service access
 * Basically this is an implementation of the Repository pattern
 *
 * @author Dusan Bartos
 */
@ApplicationScope
public class DataProvider {

    private final StorageManager storageManager;
    private final ServiceProvider serviceProvider;

    private final BehaviorSubject<List<Meal>> mealsSubject = BehaviorSubject.create(new ArrayList<Meal>());
    private final BehaviorSubject<List<Addon>> addOnsSubject = BehaviorSubject.create(new ArrayList<Addon>());

    private final PublishSubject<Throwable> errorSubject = PublishSubject.create();

    private final CompositeSubscription refreshSubscription = new CompositeSubscription();

    @Inject DataProvider(StorageManager storageManager,
                         ServiceProvider serviceProvider) {
        this.storageManager = storageManager;
        this.serviceProvider = serviceProvider;

        // observe DB entries
        storageManager.observe(Meal.class)
                .observeOn(Schedulers.computation())
                .doOnError(errorSubject::onNext)
                .compose(RxUtils.logLifecycle("dataProvider:observe:Meal"))
                .compose(RxUtils.handleErrorNever("Error observing Meals"))
                .subscribe(mealsSubject::onNext);

        storageManager.observe(Addon.class)
                .observeOn(Schedulers.computation())
                .doOnError(errorSubject::onNext)
                .compose(RxUtils.logLifecycle("dataProvider:observe:Addon"))
                .compose(RxUtils.handleErrorNever("Error observing Addons"))
                .subscribe(addOnsSubject::onNext);
    }

    public Observable<List<Meal>> observeMeals() {
        return mealsSubject.asObservable();
    }

    public Observable<List<Addon>> observeAddons() {
        return addOnsSubject.asObservable();
    }

    public void refresh() {
        refreshSubscription.clear();
        refreshSubscription.add(serviceProvider.getMealCategories()
                .subscribe(this::replaceData, t -> Timber.e(t, "Error loading meal categories")));
        refreshSubscription.add(serviceProvider.getMeals()
                .subscribe(this::replaceData, t -> Timber.e(t, "Error loading meals")));
        refreshSubscription.add(serviceProvider.getAddonCategories()
                .subscribe(this::replaceData, t -> Timber.e(t, "Error loading addon categories")));
        refreshSubscription.add(serviceProvider.getAddons()
                .subscribe(this::replaceData, t -> Timber.e(t, "Error loading addons")));
    }

    public Observable<Throwable> observeErrors() {
        return errorSubject.asObservable();
    }

    public Observable<Meal> observeMeal(String id) {
        return mealsSubject
                .flatMap(ml -> Observable.from(ml)
                        .filter(m -> m.getId().equals(id))
                        .firstOrDefault(null));
    }

    private void replaceData(List<? extends DBEntity> entities) {
        try {
//            storageManager.addAll(entities);
            storageManager.replaceAll(entities);
        } catch (Exception e) {
            Timber.e(e, "Error adding entities %s", entities);
            errorSubject.onNext(e);
        }
    }
}