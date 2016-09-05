package com.eweware.phabrik.rest;

/**
 * Created by Dave on 9/4/2016.
 */

import com.eweware.phabrik.api.gsonUTCDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;

public class RestUtils {
    private static Gson _gson = null;

    public static Gson get_gson() {
        if (_gson == null) {
            _gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new gsonUTCDateAdapter()).create();
        }

        return _gson;
    }
}
