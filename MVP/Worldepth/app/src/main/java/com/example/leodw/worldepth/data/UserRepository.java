package com.example.leodw.worldepth.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import javax.security.auth.callback.Callback;

public class UserRepository {
    private Webservice webservice;

    // Simple in-memory cache. Details omitted for brevity.
    private UserCache userCache;

    public LiveData<User> getUser(int userId) {
        //This isn't optimal implementation.
        final MutableLiveData<User> data = new MutableLiveData<>();
        webservice.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                data.setValue(response.body());
            }
            // Error case left out for brevity.
        });
        return data;
    }
}
