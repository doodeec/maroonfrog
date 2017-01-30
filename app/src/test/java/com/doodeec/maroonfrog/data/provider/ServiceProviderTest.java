package com.doodeec.maroonfrog.data.provider;

import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.AddonCategory;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.model.MealCategory;
import com.doodeec.maroonfrog.network.RestService;
import com.doodeec.maroonfrog.network.response.AddonCategoriesResponse;
import com.doodeec.maroonfrog.network.response.AddonsResponse;
import com.doodeec.maroonfrog.network.response.MealCategoriesResponse;
import com.doodeec.maroonfrog.network.response.MealsResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.functions.Func0;
import test.RxTest;
import test.TestException;
import test.UnitTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.Observable.defer;
import static rx.Observable.error;

/**
 * @author Dusan Bartos
 */
@RunWith(UnitTestRunner.class)
public class ServiceProviderTest {

    private ServiceProvider serviceProvider;
    private RestService mockService;

    @Before public void setup() throws Exception {
//        mockService = mock(RestService.class);
        mockService = spy(RestService.class);

        this.serviceProvider = new ServiceProvider(mockService);
    }

    @Test public void serviceProvider_getMeals_success() {
        Meal m1 = mock(Meal.class);
        Meal m2 = mock(Meal.class);
        Meal m3 = mock(Meal.class);
        Meal m4 = mock(Meal.class);
        Meal m5 = mock(Meal.class);

        List<Meal> list1 = Arrays.asList(m1, m2, m3, m4, m5);
        MealsResponse response = mock(MealsResponse.class);
        when(response.getMeals()).thenReturn(list1);

        when(mockService.getMeals()).thenReturn(Observable.just(response));

        List<List<Meal>> results = Arrays.asList(list1);

        RxTest.checkObservableResult(serviceProvider.getMeals(), results, null);

        verify(mockService, times(1)).getMeals();
    }

    @Test public void serviceProvider_getMeals_socketTimeout() {
        Meal m1 = mock(Meal.class);
        Meal m2 = mock(Meal.class);
        Meal m3 = mock(Meal.class);
        Meal m4 = mock(Meal.class);
        Meal m5 = mock(Meal.class);

        List<Meal> list1 = Arrays.asList(m1, m2, m3, m4, m5);
        MealsResponse response = mock(MealsResponse.class);
        when(response.getMeals()).thenReturn(list1);

        when(mockService.getMeals())
                .thenReturn(defer(new Func0<Observable<MealsResponse>>() {
                    boolean hasBeenSubscribedTo = false;

                    @Override public Observable<MealsResponse> call() {
                        if (!hasBeenSubscribedTo) {
                            hasBeenSubscribedTo = true;
                            return error(new SocketTimeoutException("timeout"));
                        }
                        return Observable.just(response);
                    }
                }));

        List<List<Meal>> results = Arrays.asList(list1);

        RxTest.checkObservableResult(serviceProvider.getMeals(), results, null);

        verify(mockService, times(1)).getMeals();
    }

    @Test public void serviceProvider_getMeals_error() {
        when(mockService.getMeals())
                .thenReturn(Observable.error(new TestException("getMeals error")));

        RxTest.checkObservableError(serviceProvider.getMeals(), TestException.class, null);

        verify(mockService, times(1)).getMeals();
    }

    @Test public void serviceProvider_getMealCategories_success() {
        MealCategory mc1 = mock(MealCategory.class);
        MealCategory mc2 = mock(MealCategory.class);
        MealCategory mc3 = mock(MealCategory.class);
        MealCategory mc4 = mock(MealCategory.class);
        MealCategory mc5 = mock(MealCategory.class);

        List<MealCategory> list1 = Arrays.asList(mc1, mc2, mc3, mc4, mc5);
        MealCategoriesResponse response = mock(MealCategoriesResponse.class);
        when(response.getCategories()).thenReturn(list1);

        when(mockService.getMealCategories()).thenReturn(Observable.just(response));

        List<List<MealCategory>> results = Arrays.asList(list1);

        RxTest.checkObservableResult(serviceProvider.getMealCategories(), results, null);

        verify(mockService, times(1)).getMealCategories();
    }

    @Test public void serviceProvider_getMealCategories_error() {
        when(mockService.getMealCategories())
                .thenReturn(Observable.error(new TestException("getMealCategories error")));

        RxTest.checkObservableError(serviceProvider.getMealCategories(), TestException.class, null);

        verify(mockService, times(1)).getMealCategories();
    }

    @Test public void serviceProvider_getAddons_success() {
        Addon a1 = mock(Addon.class);
        Addon a2 = mock(Addon.class);
        Addon a3 = mock(Addon.class);
        Addon a4 = mock(Addon.class);
        Addon a5 = mock(Addon.class);

        List<Addon> list1 = Arrays.asList(a1, a2, a3, a4, a5);
        AddonsResponse response = mock(AddonsResponse.class);
        when(response.getAddons()).thenReturn(list1);

        when(mockService.getAddons()).thenReturn(Observable.just(response));

        List<List<Addon>> results = Arrays.asList(list1);

        RxTest.checkObservableResult(serviceProvider.getAddons(), results, null);

        verify(mockService, times(1)).getAddons();
    }

    @Test public void serviceProvider_getAddons_error() {
        when(mockService.getAddons())
                .thenReturn(Observable.error(new TestException("getMealCategories error")));

        RxTest.checkObservableError(serviceProvider.getAddons(), TestException.class, null);

        verify(mockService, times(1)).getAddons();
    }

    @Test public void serviceProvider_getAddonCategories_success() {
        AddonCategory ac1 = mock(AddonCategory.class);
        AddonCategory ac2 = mock(AddonCategory.class);
        AddonCategory ac3 = mock(AddonCategory.class);
        AddonCategory ac4 = mock(AddonCategory.class);
        AddonCategory ac5 = mock(AddonCategory.class);

        List<AddonCategory> list1 = Arrays.asList(ac1, ac2, ac3, ac4, ac5);
        AddonCategoriesResponse response = mock(AddonCategoriesResponse.class);
        when(response.getCategories()).thenReturn(list1);

        when(mockService.getAddonCategories()).thenReturn(Observable.just(response));

        List<List<AddonCategory>> results = Arrays.asList(list1);

        RxTest.checkObservableResult(serviceProvider.getAddonCategories(), results, null);

        verify(mockService, times(1)).getAddonCategories();
    }

    @Test public void serviceProvider_getAddonCategories_error() {
        when(mockService.getAddonCategories())
                .thenReturn(Observable.error(new TestException("getMealCategories error")));

        RxTest.checkObservableError(serviceProvider.getAddonCategories(), TestException.class, null);

        verify(mockService, times(1)).getAddonCategories();
    }
}
