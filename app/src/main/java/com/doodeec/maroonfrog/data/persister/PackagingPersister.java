package com.doodeec.maroonfrog.data.persister;

import com.doodeec.maroonfrog.App;
import com.doodeec.maroonfrog.data.model.Packaging;
import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

/**
 * @author Dusan Bartos
 */
public class PackagingPersister extends StringType {

    private static final PackagingPersister INSTANCE = new PackagingPersister();

    public static PackagingPersister getSingleton() {
        return INSTANCE;
    }

    private Gson gson;

    public PackagingPersister() {
        super(SqlType.STRING, new Class<?>[]{Packaging.class});
        gson = App.getAppComponent().gson();
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        Packaging myFieldClass = (Packaging) javaObject;
        return myFieldClass != null ? getJsonFromMyFieldClass(myFieldClass) : null;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? getMyFieldClassFromJson((String) sqlArg) : null;
    }

    private String getJsonFromMyFieldClass(Packaging myFieldClass) {
        return gson.toJson(myFieldClass);
    }

    private Packaging getMyFieldClassFromJson(String json) {
        return gson.fromJson(json, Packaging.class);
    }
}
