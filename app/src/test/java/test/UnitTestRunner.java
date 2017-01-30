package test;

import org.mockito.junit.MockitoJUnitRunner;

import rx.plugins.RxJavaTestPlugins;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import timber.log.Timber;

import static rx.plugins.RxJavaHooks.setOnComputationScheduler;
import static rx.plugins.RxJavaHooks.setOnIOScheduler;
import static rx.plugins.RxJavaHooks.setOnNewThreadScheduler;

/**
 * Custom test runner, which is necessary for using mock Rx Schedulers
 *
 * @author Dusan Bartos
 */
public final class UnitTestRunner extends MockitoJUnitRunner.Silent {
    public UnitTestRunner(Class<?> cls) throws Exception {
        super(cls);

        RxJavaTestPlugins.resetPlugins();
        setOnIOScheduler(scheduler -> Schedulers.immediate());
        setOnComputationScheduler(scheduler -> Schedulers.immediate());
        setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        Timber.plant(new TestDebugTree());
    }
}
