package com.doodeec.maroonfrog.data.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

/**
 * @author Dusan Bartos
 */
public class Packaging {
    @SerializedName("description") @DatabaseField String description;
    @SerializedName("price") @DatabaseField Double price;

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price != null ? price : 0;
    }

    @Override public String toString() {
        return "Packaging[" +
                "desc=" + description +
                ",price=" + price +
                "]";
    }
}
