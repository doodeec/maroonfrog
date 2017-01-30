package com.doodeec.maroonfrog.data.provider;

import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.storage.IDatabase;
import com.j256.ormlite.dao.Dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import test.RxTest;
import test.UnitTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dusan Bartos
 */
@RunWith(UnitTestRunner.class)
public class StorageManagerTest {

    private StorageManager storageManager;
    private IDatabase mockDB;

    @Before public void setup() throws Exception {
        mockDB = spy(IDatabase.class);

        this.storageManager = new StorageManager(mockDB);
    }

    @Test public void storageManager_add() throws Exception {
        Dao<Meal, Object> dao = mock(Dao.class);
        Meal m1 = new Meal();
        when(dao.create(m1)).thenReturn(1);

        when(mockDB.getCachedDao(Meal.class)).thenReturn(dao);

        assertThat("error adding", storageManager.add(m1), is(true));
    }

    @Test public void storageManager_addAll() throws Exception {
        Dao<Meal, Object> dao = mock(Dao.class);
        Meal m1 = mockMeal("1");
        Dao.CreateOrUpdateStatus status1 = new Dao.CreateOrUpdateStatus(true, false, 5);
        Meal m2 = mockMeal("2");
        Dao.CreateOrUpdateStatus status2 = new Dao.CreateOrUpdateStatus(false, true, 1);
        Meal m3 = mockMeal("3");
        Dao.CreateOrUpdateStatus status3 = new Dao.CreateOrUpdateStatus(false, true, 0);

        when(dao.createOrUpdate(m1)).thenReturn(status1);
        when(dao.createOrUpdate(m2)).thenReturn(status2);
        when(dao.createOrUpdate(m3)).thenReturn(status3);
        when(dao.callBatchTasks(any()))
                .thenAnswer(invocation -> ((Callable) invocation.getArguments()[0]).call());
        when(mockDB.getCachedDao(Meal.class)).thenReturn(dao);

        assertThat("error adding list", storageManager.addAll(Arrays.asList(m1, m2, m3)), is(true));
        assertThat("error adding null", storageManager.addAll(null), is(false));
        assertThat("error adding empty list", storageManager.addAll(Collections.emptyList()), is(false));
    }

    @Test public void storageManager_replaceAll() throws Exception {
        Dao<Meal, Object> dao = mock(Dao.class);
        Meal m1 = mockMeal("1");
        Dao.CreateOrUpdateStatus status1 = new Dao.CreateOrUpdateStatus(true, false, 5);
        Meal m2 = mockMeal("2");
        Dao.CreateOrUpdateStatus status2 = new Dao.CreateOrUpdateStatus(false, true, 1);
        Meal m3 = mockMeal("3");
        Dao.CreateOrUpdateStatus status3 = new Dao.CreateOrUpdateStatus(false, true, 0);
        Meal m4 = mockMeal("4");

        when(dao.createOrUpdate(m1)).thenReturn(status1);
        when(dao.createOrUpdate(m2)).thenReturn(status2);
        when(dao.createOrUpdate(m3)).thenReturn(status3);
        when(dao.queryForAll()).thenReturn(new ArrayList<>(Arrays.asList(m1, m2, m3, m4)));
        when(dao.callBatchTasks(any()))
                .thenAnswer(invocation -> ((Callable) invocation.getArguments()[0]).call());

        when(mockDB.getCachedDao(Meal.class)).thenReturn(dao);

        assertThat("error replacing list", storageManager.replaceAll(Arrays.asList(m1, m2, m3)), is(true));
        assertThat("error replacing null", storageManager.replaceAll(null), is(false));
        assertThat("error replacing empty list", storageManager.replaceAll(Collections.emptyList()), is(false));

        verify(dao, times(1)).delete(m4);
    }

    @Test public void storageManager_deleteAll() throws Exception {
        Dao<Meal, Object> dao = mock(Dao.class);
        Meal m1 = mockMeal("1");
        Meal m2 = mockMeal("2");
        Meal m3 = mockMeal("3");

        when(dao.delete(m1)).thenReturn(0);
        when(dao.delete(m2)).thenReturn(0);
        when(dao.delete(m3)).thenReturn(1);
        when(dao.callBatchTasks(any()))
                .thenAnswer(invocation -> ((Callable) invocation.getArguments()[0]).call());

        when(mockDB.getCachedDao(Meal.class)).thenReturn(dao);

        assertThat("error deleting list", storageManager.deleteAll(Arrays.asList(m1, m2, m3)), is(true));
        assertThat("error deleting null", storageManager.deleteAll(null), is(false));
        assertThat("error deleting empty list", storageManager.deleteAll(Collections.emptyList()), is(false));
    }

    @Test public void storageManager_observe() throws Exception {
        Dao<Meal, Object> dao = mock(Dao.class);

        Meal m1 = mockMeal("1");
        Meal m2 = mockMeal("2");
        Meal m3 = mockMeal("3");

        when(dao.queryForAll())
                .thenReturn(Collections.emptyList())
                .thenReturn(Arrays.asList(m1, m2, m3));
        when(mockDB.getCachedDao(Meal.class)).thenReturn(dao);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Dao.DaoObserver observer = (Dao.DaoObserver) invocation.getArguments()[0];
                observer.onChange();
                return null;
            }
        }).when(dao).registerObserver(any());

        List<List<Meal>> result = Arrays.asList(
                Collections.emptyList(),
                Arrays.asList(m1, m2, m3));

        RxTest.checkObservableResult(storageManager.observe(Meal.class), result, null);
    }

    @Test public void storageManager_observe_error() throws Exception {
        Dao<Meal, Object> dao = mock(Dao.class);

        when(dao.queryForAll()).thenThrow(new SQLException("DB error"));
        when(mockDB.getCachedDao(Meal.class)).thenReturn(dao);

        RxTest.checkObservableError(storageManager.observe(Meal.class), SQLException.class, null);
    }

    //TODO test not compatible data class

    private Meal mockMeal(String id) {
        Meal m = new Meal();
        try {
            Field idField = m.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(m, id);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return m;
    }
}
