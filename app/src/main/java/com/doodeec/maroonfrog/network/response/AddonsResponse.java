package com.doodeec.maroonfrog.network.response;

import com.doodeec.maroonfrog.data.model.Addon;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Dusan Bartos
 */
public class AddonsResponse extends BaseResponse {
    @Expose @SerializedName("addOns") List<Addon> addons;

    public List<Addon> getAddons() {
        return addons;
    }

    @Override public String toString() {
        return "AddonsResponse[addons=" + addons + "]";
    }
}
