package com.doodeec.maroonfrog.data.persister;

import com.doodeec.maroonfrog.App;
import com.doodeec.maroonfrog.data.model.Serving;
import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

/**
 * @author Dusan Bartos
 */
public class ServingPersister extends StringType {

    private static final ServingPersister INSTANCE = new ServingPersister();

    public static ServingPersister getSingleton() {
        return INSTANCE;
    }

    private Gson gson;

    public ServingPersister() {
        super(SqlType.STRING, new Class<?>[]{Serving.class});
        gson = App.getAppComponent().gson();
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        Serving myFieldClass = (Serving) javaObject;
        return myFieldClass != null ? getJsonFromMyFieldClass(myFieldClass) : null;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? getMyFieldClassFromJson((String) sqlArg) : null;
    }

    private String getJsonFromMyFieldClass(Serving myFieldClass) {
        return gson.toJson(myFieldClass);
    }

    private Serving getMyFieldClassFromJson(String json) {
        return gson.fromJson(json, Serving.class);
    }
}
