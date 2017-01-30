package com.doodeec.maroonfrog.util.rx;

import com.doodeec.maroonfrog.BuildConfig;

import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public final class RxUtils {

    private static final boolean isDebug = BuildConfig.DEBUG;

    /**
     * Handles onError events in a stream
     * Does not emit onComplete after an error
     *
     * @param message message
     *
     * @return empty stream
     */
    public static <T> Observable.Transformer<T, T> handleErrorNever(String message) {
        return t -> t.onErrorResumeNext(e -> {
            if (isDebug) {
                Timber.e(e, message);
            } else {
                Timber.e("%s: %s", message, e.getMessage());
            }
            return Observable.never();
        });
    }

    /**
     * Handles onError events in a stream
     * Does emit onComplete right after an error
     *
     * @param message message
     *
     * @return empty stream
     */
    public static <T> Observable.Transformer<T, T> handleErrorEmpty(String message) {
        return t -> t.onErrorResumeNext(e -> {
            if (isDebug) {
                Timber.e(e, message);
            } else {
                Timber.e("%s: %s", message, e.getMessage());
            }
            return Observable.empty();
        });
    }

    /**
     * Creates transform operator, which logs important events in observable's lifecycle
     *
     * @param msg message
     * @param <T> type
     *
     * @return transformer
     */
    public static <T> Observable.Transformer<T, T> log(String msg) {
        return logInternal(msg, x -> Timber.d("[onNext] %s (%s) [Thread:%s]", msg, x, Thread.currentThread().getName()));
    }

    /**
     * Creates transform operator, which logs important events in observable's lifecycle
     * This one is different from {@link #log(String)} in a way it logs only lifecycle events, and
     * not actual data passed in these events (which can be slow when serialized data are huge)
     *
     * @param msg message
     * @param <T> type
     *
     * @return transformer
     */
    public static <T> Observable.Transformer<T, T> logLifecycle(String msg) {
        return logInternal(msg, x -> Timber.d("[onNext] %s [Thread:%s]", msg, Thread.currentThread().getName()));
    }

    private static <T> Observable.Transformer<T, T> logInternal(String msg, Action1<T> onNext) {
        return x -> x
                .doOnNext(onNext)
                .doOnError(e -> {
                    if (isDebug) {
                        Timber.e(e, "[onError] %s", msg);
                    } else {
                        Timber.e("%s: %s", msg, e.getMessage());
                    }
                })
                .doOnSubscribe(() -> Timber.v("[subscribe] %s [Thread:%s]", msg, Thread.currentThread().getName()))
                .doOnUnsubscribe(() -> Timber.v("[unsubscribe] %s", msg))
                .doOnCompleted(() -> Timber.i("[onComplete] %s", msg));
    }
}
