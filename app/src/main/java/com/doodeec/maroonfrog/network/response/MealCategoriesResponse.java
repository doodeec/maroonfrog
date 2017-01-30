package com.doodeec.maroonfrog.network.response;

import com.doodeec.maroonfrog.data.model.MealCategory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * @author Dusan Bartos
 */
public class MealCategoriesResponse extends BaseResponse {
    @Expose @SerializedName("mealCategories") List<MealCategory> categories;

    public List<MealCategory> getCategories() {
        return categories;
    }
}
