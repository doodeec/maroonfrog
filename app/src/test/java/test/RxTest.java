package test;

import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public final class RxTest {

    /**
     * Checks observable source for expected results
     *
     * @param source   source observable
     * @param expected expected results
     * @param action   action to perform between subscription and check
     * @param <T>      type of the result
     */
    public static <T> void checkObservableResult(Observable<T> source, List<T> expected, Action0 action) {
        checkObservableResult(source, expected, action, null);
    }

    /**
     * Checks observable source for expected results
     *
     * @param source   source observable
     * @param expected expected results
     * @param action   action to perform between subscription and check
     * @param debug    result debug name (pass null or empty string to disable log)
     * @param <T>      type of the result
     */
    public static <T> void checkObservableResult(Observable<T> source, List<T> expected,
                                                 Action0 action, String debug) {
        TestSubscriber<T> testSubscriber = new TestSubscriber<>();
        source.observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.requestMore(Long.MAX_VALUE);

        if (action != null) {
            action.call();
        }

        if (debug != null && !debug.equals("")) {
            List<T> values = testSubscriber.getOnNextEvents();
            Timber.d("%s|values: %s", debug, values);
        }

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(expected);
    }

    /**
     * Checks observable source for expected errors
     *
     * @param source   source observable
     * @param expected expected error type
     * @param action   action to perform between subscription and check
     * @param <T>      type of the result
     */
    public static <T> void checkObservableError(Observable<T> source, Class<? extends Throwable> expected, Action0 action) {
        checkObservableError(source, expected, action, null);
    }

    /**
     * Checks observable source for expected errors
     *
     * @param source   source observable
     * @param expected expected error type
     * @param action   action to perform between subscription and check
     * @param debug    result debug name (pass null or empty string to disable log)
     * @param <T>      type of the result
     */
    public static <T> void checkObservableError(Observable<T> source,
                                                Class<? extends Throwable> expected,
                                                Action0 action, String debug) {
        TestSubscriber<T> testSubscriber = new TestSubscriber<>();
        source.observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.requestMore(Long.MAX_VALUE);

        if (action != null) {
            action.call();
        }

        if (debug != null && !debug.equals("")) {
            List<Throwable> values = testSubscriber.getOnErrorEvents();
            Timber.d("%s|values: %s", debug, values);
        }

        testSubscriber.assertError(expected);
    }
}
