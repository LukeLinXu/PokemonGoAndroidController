package com.example.llin.pokemongocontrollernomap;
/**
 * Created by Luke Lin on 8/13/2015.
 */
import android.support.annotation.NonNull;

public interface KeyValuePersistence {
    public void save(@NonNull String key, @NonNull String value);

    @NonNull
    public String read(@NonNull String key);

    public void removeKey(@NonNull String key);
}
