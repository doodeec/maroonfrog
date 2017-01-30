package com.doodeec.maroonfrog.ui.widget.collection;

import com.doodeec.maroonfrog.dagger.scope.ApplicationScope;

import javax.inject.Inject;

/**
 * @author Dusan Bartos
 */
@ApplicationScope
public class ObservableAdapterFactory {

    @Inject ObservableAdapterFactory() {}

    public <T> ObservableAdapter<T> create(AdapterSource<T> source) {
        return new ObservableAdapter<T>(source);
    }
}
