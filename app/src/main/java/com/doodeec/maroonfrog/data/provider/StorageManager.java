package com.doodeec.maroonfrog.data.provider;

import com.doodeec.maroonfrog.dagger.scope.ApplicationScope;
import com.doodeec.maroonfrog.data.model.DBEntity;
import com.doodeec.maroonfrog.data.storage.IDatabase;
import com.j256.ormlite.dao.Dao;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Dao manipulation wrapper
 *
 * @author Dusan Bartos
 */
@ApplicationScope
public class StorageManager {

    private final IDatabase helper;

    @Inject StorageManager(IDatabase dbHelper) {
        this.helper = dbHelper;
    }

    @SuppressWarnings("unchecked")
    public <T extends DBEntity> boolean add(T object) throws Exception {
        return helper.getCachedDao((Class<T>) object.getClass()).create(object) == 1;
    }

    /**
     * Adds (runs createOrUpdate on) every object from the given collection
     * This operation runs in a transaction
     *
     * @param objects data to save/update
     * @param <T>     input objects type
     *
     * @return true if operation ended successfully
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T extends DBEntity> boolean addAll(List<T> objects) throws Exception {
        if (objects == null || objects.isEmpty()) return false;

        final Dao<T, Object> dao = helper.getCachedDao((Class<T>) objects.get(0).getClass());
        return dao.callBatchTasks(() -> {
            for (T obj : objects) {
                Dao.CreateOrUpdateStatus status = dao.createOrUpdate(obj);
                if (status.isCreated()) {
                    Timber.v("created [%s]", obj.toString());
                } else if (status.getNumLinesChanged() > 0) {
                    Timber.v("updated [%s]", obj.toString());
                } else {
                    Timber.v("object up to date [%s]", obj.toString());
                }
            }
            return true;
        });
    }

    /**
     * Basically the same as {@link #addAll(List)} but this one runs one more query (queryForAll)
     * and checks if there are any objects left (which were not in the given collection) and
     * removes them from DB if necessary
     *
     * @param objects data to save/update
     * @param <T>     input objects type
     *
     * @return true if operation ended successfully
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T extends DBEntity> boolean replaceAll(List<T> objects) throws Exception {
        if (objects == null || objects.isEmpty()) return false;

        final Dao<T, Object> dao = helper.getCachedDao((Class<T>) objects.get(0).getClass());
        return dao.callBatchTasks(() -> {
            //query for all, to be able to determine which need to be deleted
            final List<T> beforeValueList = dao.queryForAll();
            for (T obj : objects) {
                beforeValueList.remove(obj);
                final Dao.CreateOrUpdateStatus status = dao.createOrUpdate(obj);
                if (status.isCreated()) {
                    Timber.v("created [%s]", obj.toString());
                } else if (status.getNumLinesChanged() > 0) {
                    Timber.v("updated [%s]", obj.toString());
                } else {
                    Timber.v("object up to date [%s]", obj.toString());
                }
            }
            //if any old object stays unmarked, it means it needs to be deleted
            if (!beforeValueList.isEmpty()) {
                deleteAll(beforeValueList);
            }
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends DBEntity> boolean deleteAll(List<T> objects) throws Exception {
        if (objects == null || objects.isEmpty()) return false;

        final Dao<T, Object> dao = helper.getCachedDao((Class<T>) objects.get(0).getClass());
        return dao.callBatchTasks(() -> {
            for (T obj : objects) {
                boolean result = dao.delete(obj) > 0;
                Timber.v("deleted: %s [%s]", result, obj.toString());
            }
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends DBEntity> Observable<List<T>> observe(Class<T> cls) {
        final Dao<T, Object> dao = helper.getCachedDao(cls);
        //lazy final variable is safe because unsubscribe will come only after subscribe is called
        final DaoHolder dh = new DaoHolder();
        final Action1<Subscriber<List<T>>> queryAction = (subscriber) -> {
            final String clsName = cls.getSimpleName();
            try {
                long _start = System.currentTimeMillis();
                Timber.v("DAO updated for class %s", clsName);
                subscriber.onNext(dao.queryForAll());
                Timber.i("DAO update [%s] took %d millis", clsName, System.currentTimeMillis() - _start);
            } catch (Exception e) {
                subscriber.onError(e);
            }
        };

        return Observable.<List<T>>create(
                subscriber -> {
                    dh.observer = () -> Observable.just(subscriber)
                            .observeOn(Schedulers.computation())
                            .subscribe(x -> queryAction.call((Subscriber<List<T>>) x));
                    dao.registerObserver(dh.observer);
                    queryAction.call((Subscriber<List<T>>) subscriber);
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .doOnUnsubscribe(() -> dao.unregisterObserver(dh.observer));
    }

    //helper to overcome final variable lazy initialization
    private class DaoHolder {
        Dao.DaoObserver observer;
    }
}
