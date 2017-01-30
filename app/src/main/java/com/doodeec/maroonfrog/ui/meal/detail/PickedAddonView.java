package com.doodeec.maroonfrog.ui.meal.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.AddonAdapterItem;
import com.doodeec.maroonfrog.ui.widget.collection.BindableView;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * @author Dusan Bartos
 */
public class PickedAddonView extends FrameLayout implements BindableView<AddonAdapterItem> {

    private Addon addon;

    @BindView(R.id.picked_addon_name) TextView nameView;
    @BindView(R.id.picked_addon_remove) View removeBtn;

    public PickedAddonView(Context context) {
        this(context, null);
    }

    public PickedAddonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickedAddonView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        if (item instanceof Addon) {
            addon = (Addon) item;
            nameView.setText(addon.getName());
        }
    }

    @Override public View getView() {
        return this;
    }

    @Override public Observable<Object> getObjectObservable() {
        return RxView.clicks(this).map(x -> addon).ofType(Object.class);
    }
}
