package com.doodeec.maroonfrog.ui.meal.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.data.model.AddonAdapterItem;
import com.doodeec.maroonfrog.data.model.AddonCategory;
import com.doodeec.maroonfrog.ui.widget.collection.BindableView;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * @author Dusan Bartos
 */
public class AddonCategoryView extends RelativeLayout implements BindableView<AddonAdapterItem> {

    private AddonCategory category;

    @BindView(R.id.addon_category_name) TextView nameView;
    @BindView(R.id.addon_category_chevron) View chevron;

    public AddonCategoryView(Context context) {
        this(context, null);
    }

    public AddonCategoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddonCategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    @Override public void bindTo(AddonAdapterItem item) {
        if (item instanceof AddonCategory) {
            category = (AddonCategory) item;

            nameView.setText(category.getName());
            chevron.setRotation(category.isExpanded() ? -90 : 90);
        }
    }

    @Override public View getView() {
        return this;
    }

    @Override public Observable<Object> getObjectObservable() {
        return RxView.clicks(this).map(x -> category).ofType(Object.class);
    }
}
