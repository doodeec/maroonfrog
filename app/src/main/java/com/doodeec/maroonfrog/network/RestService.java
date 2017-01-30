package com.doodeec.maroonfrog.network;

import com.doodeec.maroonfrog.network.response.AddonCategoriesResponse;
import com.doodeec.maroonfrog.network.response.AddonsResponse;
import com.doodeec.maroonfrog.network.response.MealCategoriesResponse;
import com.doodeec.maroonfrog.network.response.MealsResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Retrofit interface which defines RESTful API
 * @author Dusan Bartos
 */
public interface RestService {
    @GET("meal")
    Observable<MealsResponse> getMeals();

    @GET("meal/category")
    Observable<MealCategoriesResponse> getMealCategories();

    @GET("addon")
    Observable<AddonsResponse> getAddons();

    @GET("addon/category")
    Observable<AddonCategoriesResponse> getAddonCategories();
}
