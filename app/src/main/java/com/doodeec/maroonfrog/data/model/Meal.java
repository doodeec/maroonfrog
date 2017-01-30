package com.doodeec.maroonfrog.data.model;

import com.doodeec.maroonfrog.data.persister.AddonListPersister;
import com.doodeec.maroonfrog.data.persister.ServingPersister;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import static com.doodeec.maroonfrog.data.storage.DBHelper.TABLE_NAME_MEAL;

@DatabaseTable(tableName = TABLE_NAME_MEAL)
public class Meal implements MealAdapterItem, DBEntity {

    @SerializedName("id") @DatabaseField(id = true) String id;
    @SerializedName("name") @DatabaseField String name;
    @SerializedName("category") @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true) MealCategory category;
    @SerializedName("servingSize") @DatabaseField(persisterClass = ServingPersister.class) Serving servingSize;
    @SerializedName("description") @DatabaseField String description;
    @SerializedName("addOnIds") @DatabaseField(persisterClass = AddonListPersister.class) List<String> addons;
    @SerializedName("photo") @DatabaseField String photo;
    @SerializedName("displaySeq") @DatabaseField Integer displaySeq;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getDescription() {
        return description;
    }

    public Serving getServing() {
        return servingSize;
    }

    public List<String> getAddons() {
        return addons;
    }

    public MealCategory getCategory() {
        return category;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meal meal = (Meal) o;

        return id != null ? id.equals(meal.id) : meal.id == null;

    }

    @Override public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override public String toString() {
        return "Meal[" +
                "id=" + id +
                ",name=" + name +
                ",cat=" + category +
                ",desc=" + description +
                ",photo=" + photo +
//                ",size=" + servingSize +
                ",addons=" + addons +
//                ",seq=" + displaySeq +
                "]";
    }
}
