package com.doodeec.maroonfrog.data.provider;

import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.AddonCategory;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.model.MealCategory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import test.RxTest;
import test.TestException;
import test.UnitTestRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dusan Bartos
 */
@RunWith(UnitTestRunner.class)
public class DataProviderTest {

    private DataProvider dataProvider;
    private ServiceProvider serviceProvider;
    private StorageManager storageManager;

    private BehaviorSubject<List<Meal>> mockMeals = BehaviorSubject.create(Collections.emptyList());
    private BehaviorSubject<List<Addon>> mockAddons = BehaviorSubject.create(Collections.emptyList());

    @Before public void setup() throws Exception {
        serviceProvider = mock(ServiceProvider.class);
        when(serviceProvider.getMeals()).thenReturn(Observable.empty());
        when(serviceProvider.getMealCategories()).thenReturn(Observable.empty());
        when(serviceProvider.getAddons()).thenReturn(Observable.empty());
        when(serviceProvider.getAddonCategories()).thenReturn(Observable.empty());

        storageManager = mock(StorageManager.class);
        when(storageManager.observe(Meal.class)).thenReturn(mockMeals.asObservable());
        when(storageManager.observe(Addon.class)).thenReturn(mockAddons.asObservable());

        this.dataProvider = new DataProvider(storageManager, serviceProvider);
    }

    @Test public void dataProvider_observeMeals() {
        Meal m1 = mock(Meal.class);
        Meal m2 = mock(Meal.class);
        Meal m3 = mock(Meal.class);
        Meal m4 = mock(Meal.class);
        Meal m5 = mock(Meal.class);

        List<Meal> list1 = Arrays.asList(m1, m3);
        List<Meal> list2 = Arrays.asList(m1, m2, m3);
        List<Meal> list3 = Arrays.asList(m2, m4, m5);

        List<List<Meal>> results = Arrays.asList(
                Collections.emptyList(), list1, Collections.emptyList(), list2, list3);

        RxTest.checkObservableResult(dataProvider.observeMeals(), results, () -> {
            mockMeals.onNext(list1);
            mockMeals.onNext(Collections.emptyList());
            mockMeals.onNext(list2);
            mockMeals.onNext(list3);
        });
    }

    @Test public void dataProvider_observeMeals_error() {
        TestException exception = new TestException("observe meal error");
        List<Throwable> results = Arrays.asList(exception);

        RxTest.checkObservableResult(dataProvider.observeErrors(), results,
                () -> mockMeals.onError(exception));
    }

    @Test public void dataProvider_observeAddon_error() {
        TestException exception = new TestException("observe meal error");
        List<Throwable> results = Arrays.asList(exception);

        RxTest.checkObservableResult(dataProvider.observeErrors(), results,
                () -> mockAddons.onError(exception));
    }

    @Test public void dataProvider_observeAddons() {
        Addon a1 = mock(Addon.class);
        Addon a2 = mock(Addon.class);
        Addon a3 = mock(Addon.class);
        Addon a4 = mock(Addon.class);
        Addon a5 = mock(Addon.class);

        List<Addon> list1 = Arrays.asList(a1, a3);
        List<Addon> list2 = Arrays.asList(a1, a2, a3);
        List<Addon> list3 = Arrays.asList(a2, a4, a5);

        List<List<Addon>> results = Arrays.asList(
                Collections.emptyList(), list1, Collections.emptyList(), list2, list3);

        RxTest.checkObservableResult(dataProvider.observeAddons(), results, () -> {
            mockAddons.onNext(list1);
            mockAddons.onNext(Collections.emptyList());
            mockAddons.onNext(list2);
            mockAddons.onNext(list3);
        });
    }

    @Test public void dataProvider_observeMeal() {
        Meal m1 = mockMeal("1");
        Meal m2 = mockMeal("mymeal");
        Meal m3 = mockMeal("3");
        Meal m4 = mockMeal("4");
        Meal m5 = mockMeal("5");

        List<Meal> list1 = Arrays.asList(m1, m2, m3, m4, m5);
        List<Meal> list2 = Arrays.asList(m5, m3);
        List<Meal> list3 = Arrays.asList(m2, m4);

        List<Meal> results = Arrays.asList(null, m2, null, m2, null);

        RxTest.checkObservableResult(dataProvider.observeMeal("mymeal"), results, () -> {
            mockMeals.onNext(list1);
            mockMeals.onNext(list2);
            mockMeals.onNext(list3);
            mockMeals.onNext(Collections.emptyList());
        });
    }

    @Test public void dataProvider_triggerStorageSave() {
        Meal m1 = mock(Meal.class);
        Meal m2 = mock(Meal.class);

        MealCategory mc1 = mock(MealCategory.class);
        MealCategory mc2 = mock(MealCategory.class);

        Addon a1 = mock(Addon.class);
        Addon a2 = mock(Addon.class);

        AddonCategory ac1 = mock(AddonCategory.class);
        AddonCategory ac2 = mock(AddonCategory.class);

        when(serviceProvider.getMeals()).thenReturn(Observable.just(Arrays.asList(m1, m2)));
        when(serviceProvider.getMealCategories()).thenReturn(Observable.just(Arrays.asList(mc1, mc2)));
        when(serviceProvider.getAddons()).thenReturn(Observable.just(Arrays.asList(a1, a2)));
        when(serviceProvider.getAddonCategories()).thenReturn(Observable.just(Arrays.asList(ac1, ac2)));

        dataProvider.observeMeals().subscribe();
        dataProvider.observeAddons().subscribe();
    }

    @Test public void dataProvider_refresh() throws Exception {
        when(storageManager.replaceAll(any())).thenReturn(true);

        dataProvider.refresh();

        verify(serviceProvider, times(1)).getMeals();
        verify(serviceProvider, times(1)).getMealCategories();
        verify(serviceProvider, times(1)).getAddons();
        verify(serviceProvider, times(1)).getAddonCategories();
    }

    @Test public void dataProvider_refresh_error() throws Exception {
        MealCategory mc1 = mock(MealCategory.class);
        MealCategory mc2 = mock(MealCategory.class);

        Exception exception = new Exception("storage save error");

        when(serviceProvider.getMealCategories()).thenReturn(Observable.just(Arrays.asList(mc1, mc2)));
        when(storageManager.replaceAll(any())).thenThrow(exception);

        List<Throwable> results = Arrays.asList(exception);

        RxTest.checkObservableResult(dataProvider.observeErrors(), results, () -> dataProvider.refresh());
    }

    private Meal mockMeal(String id) {
        Meal m = mock(Meal.class);
        when(m.getId()).thenReturn(id);
        return m;
    }
}
