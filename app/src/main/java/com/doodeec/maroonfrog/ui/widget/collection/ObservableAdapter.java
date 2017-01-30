package com.doodeec.maroonfrog.ui.widget.collection;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Observable Adapter is a generic class for use with the recyclerView
 *
 * This class separates UI related code from Data source logic.
 *
 * usage:
 * - create an implementation of abstract {@link AdapterSource} class
 * - pass this source into the constructor of this class
 * - when modifying data source, list is notified appropriately
 *
 * @author Dusan Bartos
 */
public class ObservableAdapter<T> extends RecyclerView.Adapter<ObservableAdapter.ViewHolder<T>> {

    private final AdapterSource<T> source;
    private final PublishSubject<Object> itemEvent = PublishSubject.create();
    private final CompositeSubscription changeWatcher = new CompositeSubscription();

    ObservableAdapter(AdapterSource<T> source) {
        this.source = source;
    }

    @Override
    public ViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = source.getLayout(viewType);
        try {
            @SuppressWarnings("unchecked")
            BindableView<T> view = (BindableView<T>) LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new ViewHolder<T>(view);
        } catch (InflateException e) {
            Timber.e(e, "Error inflating view %s %s", layout, parent.getContext().getResources().getResourceEntryName(layout));
            throw e;
        }
    }

    @Override public void onBindViewHolder(ViewHolder<T> holder, int position) {
        holder.bindTo(source.get(position), position);
    }

    @Override public int getItemViewType(int position) {
        return source.getViewType(position);
    }

    @Override public int getItemCount() {
        return source.getCount();
    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        notifyDataSetChanged();
        changeWatcher.add(source.onDataChange().subscribe(
                o -> notifyDataSetChanged(),
                e -> Timber.e(e, "Error watching source data")));
    }

    @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        changeWatcher.clear();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override public void onViewAttachedToWindow(ViewHolder<T> holder) {
        super.onViewAttachedToWindow(holder);
        Observable<Object> objectObservable = holder.holderView.getObjectObservable();
        if (objectObservable != null) {
            holder.subscription.add(objectObservable
                    .filter(obj -> obj != null)
                    .map(data -> new ObjectEvent(holder, data))
                    .doOnError(e -> Timber.e("ObservableAdapter Error: %s", e))
                    .subscribe(itemEvent::onNext));
        }
    }

    @Override public void onViewDetachedFromWindow(ViewHolder<T> holder) {
        super.onViewDetachedFromWindow(holder);
        holder.subscription.clear();
    }

    /**
     * Generic method which dispatches viewHolder events (click, longtap, whatever) to
     * the listeners (typically presenters).
     * Data object is of type Object, but the real type depends on an implementation inside
     * specific view (makes use of {@link BindableView#getObjectObservable()} method)
     *
     * @return observable event dispatcher
     */
    public Observable<Object> onItemEvent() {
        return itemEvent.asObservable();
    }

    /**
     * Generic ViewHolder for the purposes of ObservableAdapter use
     */
    static class ViewHolder<T> extends RecyclerView.ViewHolder {

        private BindableView<T> holderView;
        private T item;
        private int position;
        private final CompositeSubscription subscription = new CompositeSubscription();

        ViewHolder(BindableView<T> itemView) {
            super(itemView.getView());
            this.holderView = itemView;
        }

        void bindTo(T item, int pos) {
            this.item = item;
            this.position = pos;
            holderView.bindTo(item);
        }
    }

    /**
     * Handler class for adapter item events
     */
    public static class ObjectEvent {

        private WeakReference<ViewHolder> holder;
        private Object data;

        ObjectEvent(ViewHolder holder, Object data) {
            this.holder = new WeakReference<ViewHolder>(holder);
            this.data = data;
        }

        @Nullable
        public ViewHolder getView() {
            return holder.get();
        }

        public Object getData() {
            return data;
        }
    }
}
