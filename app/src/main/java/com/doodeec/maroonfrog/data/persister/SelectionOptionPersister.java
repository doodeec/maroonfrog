package com.doodeec.maroonfrog.data.persister;

import com.doodeec.maroonfrog.App;
import com.doodeec.maroonfrog.data.model.SelectionOption;
import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

/**
 * @author Dusan Bartos
 */
public class SelectionOptionPersister extends StringType {

    private static final SelectionOptionPersister INSTANCE = new SelectionOptionPersister();

    public static SelectionOptionPersister getSingleton() {
        return INSTANCE;
    }

    private Gson gson;

    public SelectionOptionPersister() {
        super(SqlType.STRING, new Class<?>[]{SelectionOption.class});
        gson = App.getAppComponent().gson();
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        SelectionOption myFieldClass = (SelectionOption) javaObject;
        return myFieldClass != null ? getJsonFromMyFieldClass(myFieldClass) : null;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? getMyFieldClassFromJson((String) sqlArg) : null;
    }

    private String getJsonFromMyFieldClass(SelectionOption myFieldClass) {
        return gson.toJson(myFieldClass);
    }

    private SelectionOption getMyFieldClassFromJson(String json) {
        return gson.fromJson(json, SelectionOption.class);
    }
}
