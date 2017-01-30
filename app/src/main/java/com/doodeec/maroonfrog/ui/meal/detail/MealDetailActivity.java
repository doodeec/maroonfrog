package com.doodeec.maroonfrog.ui.meal.detail;

import android.os.Bundle;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.base.BaseActivity;
import com.doodeec.maroonfrog.base.Layout;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.activity_meal_detail)
public class MealDetailActivity extends BaseActivity {
    public static final String MEALID = "meal_id";

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        enableToolbarBack();
    }
}
