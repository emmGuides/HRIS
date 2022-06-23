package com.example.hris.ui.vacation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VacationViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public VacationViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("This is Vacation Fragment");
    }

    public LiveData<String> getText() { return mText; }
}
