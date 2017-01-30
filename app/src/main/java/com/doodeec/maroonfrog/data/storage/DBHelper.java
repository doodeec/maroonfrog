package com.doodeec.maroonfrog.data.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.doodeec.maroonfrog.data.model.Addon;
import com.doodeec.maroonfrog.data.model.AddonCategory;
import com.doodeec.maroonfrog.data.model.DBEntity;
import com.doodeec.maroonfrog.data.model.Meal;
import com.doodeec.maroonfrog.data.model.MealCategory;
import com.doodeec.maroonfrog.data.persister.AddonListPersister;
import com.doodeec.maroonfrog.data.persister.PackagingPersister;
import com.doodeec.maroonfrog.data.persister.SelectionOptionPersister;
import com.doodeec.maroonfrog.data.persister.ServingPersister;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public class DBHelper extends OrmLiteSqliteOpenHelper implements IDatabase {

    //DB table names
    public static final String TABLE_NAME_MEAL = "meals";
    public static final String TABLE_NAME_MEALCAT = "meal_categories";
    public static final String TABLE_NAME_ADDON = "addons";
    public static final String TABLE_NAME_ADDONCAT = "addon_categories";

    //DB config
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "com.doodeec.maroonfrog.db";

    //DAO cache
    private final Map<Class, Dao<?, Object>> daoMap = new HashMap<>();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            DataPersisterManager.registerDataPersisters(
                    ServingPersister.getSingleton(),
                    SelectionOptionPersister.getSingleton(),
                    PackagingPersister.getSingleton(),
                    AddonListPersister.getSingleton()
            );

            Timber.d("Creating Meals table");
            TableUtils.createTable(connectionSource, Meal.class);
            Timber.d("Creating Meal Categories table");
            TableUtils.createTable(connectionSource, MealCategory.class);
            Timber.d("Creating Addons table");
            TableUtils.createTable(connectionSource, Addon.class);
            Timber.d("Creating Addon Categories table");
            TableUtils.createTable(connectionSource, AddonCategory.class);
        } catch (java.sql.SQLException e) {
            Timber.e(e, "Error creating DB");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    //add migration when upgrading DB schema
                    break;

                default:
                    throw new IllegalStateException("onUpgrade() with unknown newVersion" + newVersion);
            }
            upgradeTo++;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DBEntity> Dao<T, Object> getCachedDao(Class<T> clazz) {
        if (!daoMap.containsKey(clazz)) {
            try {
                final Dao<T, Object> dao = getDao(clazz);
                daoMap.put(clazz, dao);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return (Dao<T, Object>) daoMap.get(clazz);
    }
}
