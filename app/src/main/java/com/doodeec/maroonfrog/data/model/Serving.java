package com.doodeec.maroonfrog.data.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

/**
 * @author Dusan Bartos
 */
public class Serving {
    @SerializedName("size") @DatabaseField String size;
    @SerializedName("price") @DatabaseField Double price;

    public String getSize() {
        return size;
    }

    public Double getPrice() {
        return price;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override public String toString() {
        return "Serving[" +
                "size=" + size +
                ",price=" + price +
                "]";
    }
}
