package com.doodeec.maroonfrog.ui.meal.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.data.model.MealAdapterItem;
import com.doodeec.maroonfrog.data.model.MealCategory;
import com.doodeec.maroonfrog.ui.widget.collection.BindableView;
import com.doodeec.maroonfrog.util.Util;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * @author Dusan Bartos
 */
public class MealCategoryView extends RelativeLayout implements BindableView<MealAdapterItem> {

    private MealCategory category;

    @BindView(R.id.category_name) TextView nameView;
    @BindView(R.id.category_chevron) View chevron;

    public MealCategoryView(Context context) {
        this(context, null);
    }

    public MealCategoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MealCategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    private void setup() {
        setClickable(true);
    }

    @Override public void bindTo(MealAdapterItem item) {
        if (item instanceof MealCategory) {
            category = (MealCategory) item;

            nameView.setText(category.getName());
            chevron.setRotation(category.isExpanded() ? -90 : 90);

            Util.adaptTextSize(nameView, 20, R.dimen.abc_text_size_display_2_material, R.dimen.abc_text_size_display_1_material);
        }
    }

    @Override public View getView() {
        return this;
    }

    @Override public Observable<Object> getObjectObservable() {
        return RxView.clicks(this).map(x -> category).ofType(Object.class);
    }
}
