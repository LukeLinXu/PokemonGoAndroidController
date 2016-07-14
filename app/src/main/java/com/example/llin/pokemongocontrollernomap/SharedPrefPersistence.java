package com.example.llin.pokemongocontrollernomap;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by Luke Lin on 8/13/2015.
 */
public class SharedPrefPersistence implements KeyValuePersistence {
    private final SharedPreferences prefs;


    public SharedPrefPersistence(Context context) {
        prefs = context.getSharedPreferences(SharedPrefPersistence.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    @Override
    public void save(@NonNull String key, @NonNull String value) {
        prefs.edit().putString(key, value).apply();
    }


    @Override
    public String read(@NonNull String key) {
        return prefs.getString(key, null);
    }

    @Override
    public void removeKey(String key) {
        prefs.edit().remove(key).apply();
        ;
    }
}
