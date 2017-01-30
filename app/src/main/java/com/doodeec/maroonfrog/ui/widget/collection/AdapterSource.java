package com.doodeec.maroonfrog.ui.widget.collection;

import android.support.annotation.LayoutRes;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Data source for ObservableAdapter
 * Automatically notifies adapter when data is changed
 * It can implement different layouts in single adapter - similar to basic
 * recyclerView's adapter
 *
 * @author Dusan Bartos
 * @see #getViewType(int)
 * @see #getLayout(int)
 */
public abstract class AdapterSource<T> {

    private final PublishSubject<Object> dataChangeSubject = PublishSubject.create();
    private List<T> data;

    public AdapterSource(List<T> data) {
        this.data = data;
    }

    public void setData(List<T> data) {
        this.data = data;
        dataChangeSubject.onNext(new Object());
    }

    public T get(int position) {
        return data.get(position);
    }

    /**
     * Implement viewType determination from the data item at specific position
     */
    protected abstract int getViewType(int pos);

    /**
     * Implement resource layout type determination regarding its viewType
     */
    @LayoutRes public abstract int getLayout(int viewType);

    int getCount() {
        return data.size();
    }

    public Observable<Object> onDataChange() {
        return dataChangeSubject.asObservable();
    }
}
