package com.example.myapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class MyViewModel extends ViewModel {
    public final MutableLiveData<HashMap<String,Object>> user = new MutableLiveData<>();

    public void setUser(HashMap<String, Object> user) {
        this.user.setValue(user);
    }
}
