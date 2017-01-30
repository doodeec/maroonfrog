package com.doodeec.maroonfrog.ui.meal.detail;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.ui.widget.collection.AdapterSource;

import java.util.List;

/**
 * @author Dusan Bartos
 */
public class PickedAddonsSource extends AdapterSource<Addon> {

    public PickedAddonsSource(List<Addon> data) {
        super(data);
    }

    @Override protected int getViewType(int pos) {
        return 0;
    }

    @Override public int getLayout(int viewType) {
        return R.layout.view_addon_picked;
    }
}
