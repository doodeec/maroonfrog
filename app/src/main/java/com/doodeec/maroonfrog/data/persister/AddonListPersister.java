package com.doodeec.maroonfrog.data.persister;

import android.text.TextUtils;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dusan Bartos
 */
public class AddonListPersister extends StringType {

    private static final AddonListPersister INSTANCE = new AddonListPersister();
    private static final String DELIMITER = ",";

    public static AddonListPersister getSingleton() {
        return INSTANCE;
    }

    public AddonListPersister() {
        super(SqlType.STRING, new Class<?>[]{List.class});
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        List<String> myFieldClass = (List<String>) javaObject;
        return myFieldClass != null ? getJsonFromMyFieldClass(myFieldClass) : null;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? getMyFieldClassFromJson((String) sqlArg) : null;
    }

    private String getJsonFromMyFieldClass(List<String> myFieldClass) {
        return TextUtils.join(DELIMITER, myFieldClass);
    }

    private List<String> getMyFieldClassFromJson(String json) {
        return Arrays.asList(TextUtils.split(json, DELIMITER));
    }
}
