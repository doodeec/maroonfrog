package com.doodeec.maroonfrog.ui.meal.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.model.MealAdapterItem;
import com.doodeec.maroonfrog.ui.widget.collection.BindableView;
import com.doodeec.maroonfrog.util.Util;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * @author Dusan Bartos
 */
public class MealView extends RelativeLayout implements BindableView<MealAdapterItem> {

    private Meal meal;

    @BindView(R.id.meal_name) TextView nameView;
    @BindView(R.id.meal_photo) ImageView photo;
    @BindView(R.id.meal_price) TextView priceView;
    @BindView(R.id.meal_size) TextView sizeView;

    public MealView(Context context) {
        this(context, null);
    }

    public MealView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MealView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        Util.releaseImage(photo);

        if (item instanceof Meal) {
            meal = (Meal) item;

            nameView.setText(meal.getName());
            Util.setImage(photo, meal.getPhoto(), R.drawable.food);
            if (meal.getServing() != null) {
                priceView.setText(Util.formatPrice(meal.getServing().getPrice()));
                sizeView.setText(meal.getServing().getSize());
            } else {
                priceView.setText(null);
                sizeView.setText(null);
            }
        }
    }

    @Override public View getView() {
        return this;
    }

    @Override public Observable<Object> getObjectObservable() {
        return RxView.clicks(this).map(x -> meal).ofType(Object.class);
    }
}
