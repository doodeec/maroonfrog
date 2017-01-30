package com.doodeec.maroonfrog.data.model;

import com.doodeec.maroonfrog.data.persister.PackagingPersister;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import static com.doodeec.maroonfrog.data.storage.DBHelper.TABLE_NAME_MEALCAT;

@DatabaseTable(tableName = TABLE_NAME_MEALCAT)
public class MealCategory implements MealAdapterItem, DBEntity {

    @SerializedName("id") @DatabaseField(id = true) String id;
    @SerializedName("name") @DatabaseField String name;
    @SerializedName("packaging") @DatabaseField(persisterClass = PackagingPersister.class) Packaging packaging;
    @SerializedName("description") @DatabaseField String description;
    @SerializedName("displaySeq") @DatabaseField Integer displaySeq;

    @SerializedName("meals") List<Meal> meals;

    private boolean expanded = false;

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MealCategory category = (MealCategory) o;

        return id != null ? id.equals(category.id) : category.id == null;

    }

    @Override public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override public String toString() {
        return "MealCategory[" +
                "id=" + id +
                ",name=" + name +
//                ",meals=" + meals +
                ",pack=" + packaging +
                ",desc=" + description +
//                ",seq=" + displaySeq +
//                ",exp=" + expanded +
                "]";
    }
}
