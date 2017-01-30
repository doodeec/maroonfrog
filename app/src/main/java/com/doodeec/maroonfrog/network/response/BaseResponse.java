package com.doodeec.maroonfrog.network.response;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Dusan Bartos
 */
public abstract class BaseResponse {

    @Expose @SerializedName("version") String version;

    @Nullable public String getVersion() {
        return version;
    }
}
