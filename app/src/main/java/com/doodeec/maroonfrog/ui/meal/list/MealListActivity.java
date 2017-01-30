package com.doodeec.maroonfrog.ui.meal.list;

import android.view.Menu;
import android.view.MenuItem;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.base.BaseActivity;
import com.doodeec.maroonfrog.base.Layout;

/**
 * @author Dusan Bartos
 */
@Layout(R.layout.activity_meal_list)
public class MealListActivity extends BaseActivity {

    private MealListFragment fragment;

    @Override protected void onResume() {
        super.onResume();
        fragment = (MealListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_product_list);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;
        if (fragment != null) {
            handled = fragment.onMenuItemClick(item.getItemId());
        }
        return handled || super.onOptionsItemSelected(item);
    }
}
