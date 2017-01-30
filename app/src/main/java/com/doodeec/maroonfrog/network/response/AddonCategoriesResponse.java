package com.doodeec.maroonfrog.network.response;

import com.doodeec.maroonfrog.data.model.AddonCategory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Dusan Bartos
 */
public class AddonCategoriesResponse extends BaseResponse {
    @Expose @SerializedName("addOnCategories") List<AddonCategory> categories;

    public List<AddonCategory> getCategories() {
        return categories;
    }
}
