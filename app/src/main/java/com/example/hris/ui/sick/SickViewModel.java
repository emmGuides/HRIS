package com.example.hris.ui.sick;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SickViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public SickViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("number of days placeholder");
    }

    public LiveData<String> getText() { return mText; }
}
