package com.doodeec.maroonfrog.ui.meal.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.AddonAdapterItem;
import com.doodeec.maroonfrog.ui.widget.collection.BindableView;
import com.doodeec.maroonfrog.util.Util;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * @author Dusan Bartos
 */
public class AddonView extends RelativeLayout implements BindableView<AddonAdapterItem> {

    private Addon addon;

    @BindView(R.id.addon_name) TextView nameView;
    @BindView(R.id.addon_size) TextView sizeView;
    @BindView(R.id.addon_price) TextView priceView;

    public AddonView(Context context) {
        this(context, null);
    }

    public AddonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    private void setup() {
//        setFocusable(true);
//        setFocusableInTouchMode(true);
        setClickable(true);
    }

    @Override public void bindTo(AddonAdapterItem item) {
        if (item instanceof Addon) {
            addon = (Addon) item;

            nameView.setText(addon.getName());
            if (addon.getServingSize() != null) {
                sizeView.setText(addon.getServingSize().getSize());
                priceView.setText(Util.formatPrice(addon.getServingSize().getPrice()));
            } else {
                sizeView.setText(null);
                priceView.setText(null);
            }
            setEnabled(!addon.isPicked());
        }
    }

    @Override public View getView() {
        return this;
    }

    @Override public Observable<Object> getObjectObservable() {
        return RxView.clicks(this).map(x -> addon).ofType(Object.class);
    }

    @Override public void setEnabled(boolean enabled) {
//        super.setEnabled(enabled);
        nameView.setEnabled(enabled);
        sizeView.setEnabled(enabled);
        priceView.setEnabled(enabled);
    }
}
