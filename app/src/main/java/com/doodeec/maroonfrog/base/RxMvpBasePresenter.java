package com.doodeec.maroonfrog.base;

import android.os.Bundle;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public abstract class RxMvpBasePresenter<P extends BaseMvpView> extends MvpBasePresenter<P> {

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    protected Bundle intentExtra;
    protected Bundle fragmentArgs;

    @Override public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        subscriptions.clear();
    }

    void setIntentExtra(Bundle intentExtra) {
        this.intentExtra = intentExtra;
    }

    void setFragmentArgs(Bundle fragmentArgs) {
        this.fragmentArgs = fragmentArgs;
    }

    /**
     * Helper method to bind subscription to presenter's lifecycle automatically
     * When you add a subscription with this method, it will be automatically unsubscribed
     * during {@link #detachView(boolean)} event
     */
    protected void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    protected void showError(String message) {
        if (isViewAttached()) {
            getView().toast(message);
        }
    }

    protected Action1<Throwable> baseErrorHandler(String msg) {
        return t -> Timber.e(t, "Error: %s", msg);
    }

    protected void showLoading(int msg) {
        if (isViewAttached()) {
            getView().showLoading(msg);
        }
    }

    protected void hideLoading() {
        if (isViewAttached()) {
            getView().hideLoading();
        }
    }
}
