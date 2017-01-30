package com.doodeec.maroonfrog.data.storage;

import com.doodeec.maroonfrog.data.model.DBEntity;
import com.j256.ormlite.dao.Dao;

/**
 * Abstraction of DBHelper to get rid of Android framework specific classes for testing
 *
 * @author Dusan Bartos
 */
public interface IDatabase {
    <T extends DBEntity> Dao<T, Object> getCachedDao(Class<T> clazz);
}
