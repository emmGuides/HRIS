package com.example.hris.ui.sick;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.databinding.FragmentSickBinding;

public class SickFragment extends Fragment {

    private FragmentSickBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SickViewModel sickViewModel =
                new ViewModelProvider(this).get(SickViewModel.class);

        binding = FragmentSickBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSick;
        sickViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
