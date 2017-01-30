package com.doodeec.maroonfrog.ui.meal.list;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.model.MealAdapterItem;
import com.doodeec.maroonfrog.data.model.MealCategory;
import com.doodeec.maroonfrog.ui.widget.collection.AdapterSource;

import java.util.List;

/**
 * @author Dusan Bartos
 */
public class MealListSource extends AdapterSource<MealAdapterItem> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_MEAL = 1;

    public MealListSource(List<MealAdapterItem> data) {
        super(data);
    }

    @Override protected int getViewType(int pos) {
        final MealAdapterItem item = get(pos);
        if (item instanceof Meal) {
            return TYPE_MEAL;
        } else if (item instanceof MealCategory) {
            return TYPE_CATEGORY;
        }
        return -1;
    }

    @Override public int getLayout(int viewType) {
        switch (viewType) {
            case TYPE_MEAL:
                return R.layout.view_meal;

            case TYPE_CATEGORY:
            default:
                return R.layout.view_mealcategory;
        }
    }
}
