package com.doodeec.maroonfrog.ui.widget.collection;

import android.view.View;

import rx.Observable;

/**
 * Base View class used in Observable Adapter
 * This delegates bind mechanism directly to custom view instead of adapter implementation
 *
 * @author Dusan Bartos
 */
public interface BindableView<T> {
    /**
     * Event invoked when viewHolder is attached a data object
     *
     * @param item adapter item
     */
    void bindTo(T item);

    /**
     * This needs to return View instance (probably will be "this" for most implementations)
     * Just a helper method, since we cannot pass interface to viewHolder's constructor
     *
     * @return viewHolder's view instance
     */
    View getView();

    /**
     * Return observable events (cast to Object) here if you want to handle clicks and
     * other events from viewHolder
     * Output of this stream can be observed through {@link ObservableAdapter#onItemEvent()}
     *
     * @return observable events
     */
    Observable<Object> getObjectObservable();
}
