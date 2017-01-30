package com.doodeec.maroonfrog.data.model;

import com.doodeec.maroonfrog.data.persister.ServingPersister;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import static com.doodeec.maroonfrog.data.storage.DBHelper.TABLE_NAME_ADDON;

@DatabaseTable(tableName = TABLE_NAME_ADDON)
public class Addon implements AddonAdapterItem, DBEntity {

    @SerializedName("id") @DatabaseField(id = true) String id;
    @SerializedName("name") @DatabaseField String name;
    @SerializedName("description") @DatabaseField String description;
    @SerializedName("category") @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true) AddonCategory category;
    @SerializedName("servingSize") @DatabaseField(persisterClass = ServingPersister.class) Serving servingSize;
    @SerializedName("displaySeq") @DatabaseField Integer displaySeq;

    private boolean picked;

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public boolean isPicked() {
        return picked;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AddonCategory getCategory() {
        return category;
    }

    public Serving getServingSize() {
        return servingSize;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Addon addon = (Addon) o;

        return id != null ? id.equals(addon.id) : addon.id == null;

    }

    @Override public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override public String toString() {
        return "Addon[" +
                "id=" + id +
                ",name=" + name +
//                ",desc=" + description +
//                ",cat=" + category +
                ",cat=" + (category != null ? category.getId() : null) +
                "]";
    }
}
