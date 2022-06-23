package com.example.hris.ui.teams;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TeamsViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public TeamsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Teams fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}