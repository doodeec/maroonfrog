package com.doodeec.maroonfrog.network.response;

import com.doodeec.maroonfrog.data.model.Meal;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Dusan Bartos
 */
public class MealsResponse extends BaseResponse {
    @Expose @SerializedName("meals") List<Meal> meals;

    public List<Meal> getMeals() {
        return meals;
    }
}
