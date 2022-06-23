package com.example.hris.ui.teams;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TeamsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mText2;

    public TeamsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Teams fragment");
        mText2 = new MutableLiveData<>();
        mText2.setValue("checking: This is mtext2");
    }

    public LiveData<String> getText() {
        return mText;
    }
}