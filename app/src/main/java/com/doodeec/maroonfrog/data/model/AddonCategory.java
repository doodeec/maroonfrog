package com.doodeec.maroonfrog.data.model;

import com.doodeec.maroonfrog.data.persister.SelectionOptionPersister;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import static com.doodeec.maroonfrog.data.storage.DBHelper.TABLE_NAME_ADDONCAT;

@DatabaseTable(tableName = TABLE_NAME_ADDONCAT)
public class AddonCategory implements AddonAdapterItem, DBEntity {

    @SerializedName("id") @DatabaseField(id = true) String id;
    @SerializedName("name") @DatabaseField String name;
    @SerializedName("description") @DatabaseField String description;
    @SerializedName("selectionOption") @DatabaseField(persisterClass = SelectionOptionPersister.class) SelectionOption selOption;
    @SerializedName("displaySeq") @DatabaseField Integer displaySeq;

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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddonCategory that = (AddonCategory) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override public String toString() {
        return "AddonCategory[" +
                "id=" + id +
                ",name=" + name +
                ",exp=" + expanded +
                "]";
    }
}
