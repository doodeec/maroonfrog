package com.doodeec.maroonfrog.ui.meal.detail;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.AddonAdapterItem;
import com.doodeec.maroonfrog.data.model.AddonCategory;
import com.doodeec.maroonfrog.ui.widget.collection.AdapterSource;

import java.util.List;

/**
 * @author Dusan Bartos
 */
public class AddonListSource extends AdapterSource<AddonAdapterItem> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_ADDON = 1;

    public AddonListSource(List<AddonAdapterItem> data) {
        super(data);
    }

    @Override protected int getViewType(int pos) {
        final AddonAdapterItem item = get(pos);
        if (item instanceof Addon) {
            return TYPE_ADDON;
        } else if (item instanceof AddonCategory) {
            return TYPE_CATEGORY;
        }
        return -1;
    }

    @Override public int getLayout(int viewType) {
        switch (viewType) {
            case TYPE_ADDON:
                return R.layout.view_addon;

            case TYPE_CATEGORY:
            default:
                return R.layout.view_addoncategory;
        }
    }
}
