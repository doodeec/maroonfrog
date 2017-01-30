package com.doodeec.maroonfrog.data.provider;

import com.doodeec.maroonfrog.dagger.scope.ApplicationScope;
import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.AddonCategory;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.model.MealCategory;
import com.doodeec.maroonfrog.network.RestService;
import com.doodeec.maroonfrog.network.response.AddonCategoriesResponse;
import com.doodeec.maroonfrog.network.response.AddonsResponse;
import com.doodeec.maroonfrog.network.response.MealCategoriesResponse;
import com.doodeec.maroonfrog.network.response.MealsResponse;
import com.doodeec.maroonfrog.util.rx.RxUtils;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Service Provider encapsulates server REST API communication
 *
 * @author Dusan Bartos
 */
@ApplicationScope
public class ServiceProvider {
    private final RestService restService;

    @Inject ServiceProvider(RestService restService) {
        this.restService = restService;
    }

    /**
     * Load list of meals
     * Runs on IO scheduler
     */
    Observable<List<Meal>> getMeals() {
        return restService.getMeals()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(handleCommonErrors())
                .compose(RxUtils.logLifecycle("getMeals"))
                .map(MealsResponse::getMeals);
    }

    /**
     * Load list of meal categories
     * Runs on IO scheduler
     */
    Observable<List<MealCategory>> getMealCategories() {
        return restService.getMealCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(handleCommonErrors())
                .compose(RxUtils.logLifecycle("getMealCategories"))
                .map(MealCategoriesResponse::getCategories);
    }

    /**
     * Load list of addons
     * Runs on IO scheduler
     */
    Observable<List<Addon>> getAddons() {
        return restService.getAddons()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(handleCommonErrors())
                .compose(RxUtils.logLifecycle("getAddons"))
                .map(AddonsResponse::getAddons);
    }

    /**
     * Load list of addon categories
     * Runs on IO scheduler
     */
    Observable<List<AddonCategory>> getAddonCategories() {
        return restService.getAddonCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(handleCommonErrors())
                .compose(RxUtils.logLifecycle("getAddonCategories"))
                .map(AddonCategoriesResponse::getCategories);
    }

    /**
     * Common error handler
     * This can contain timeout retry logic, transformation of global response errors, and other logic
     *
     * @param <T> type - this should not be modified during transformation
     *
     * @return observable transformer
     */
    private <T> Observable.Transformer<T, T> handleCommonErrors() {
        return br -> br.retryWhen(errors -> errors
                //retry max 2 times
                .zipWith(Observable.range(1, 2), (t, i) -> t)
                .flatMap(error -> {
                    //retry when socket timeout occurs
                    if (error instanceof SocketTimeoutException) {
                        return Observable.just(null);
                    }

                    // For anything else, don't retry
                    return Observable.error(error);
                }));
    }
}
