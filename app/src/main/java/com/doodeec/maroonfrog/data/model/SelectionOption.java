package com.doodeec.maroonfrog.data.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

/**
 * @author Dusan Bartos
 */
public class SelectionOption {
    @SerializedName("min") @DatabaseField Double min;
    @SerializedName("max") @DatabaseField Double max;

    @Override public String toString() {
        return "SelectionOption[" +
                "min=" + min +
                ",max=" + max +
                "]";
    }
}
