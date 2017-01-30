package com.doodeec.maroonfrog.ui.meal.detail;

import com.doodeec.maroonfrog.base.BaseMvpView;

/**
 * @author Dusan Bartos
 */
public interface MealDetailView extends BaseMvpView {
    void setName(String name);

    void setDescription(String description);
}
