package com.example.financialapp.NavigationFragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.financialapp.MainActivity;
import com.example.financialapp.databinding.FragmentLanguagesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class LanguagesFragment extends Fragment {
    FragmentLanguagesBinding binding;
    public static String current_language = "English";
    public static String english = "English";
    public static String vietnamese = "Vietnamese";
    public static String japanese = "Japanese";
    public static String chinese = "Chinese";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLanguagesBinding.inflate(getLayoutInflater());

        binding.setLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguage();
            }
        });

        if(current_language.equals(vietnamese)) {
            binding.VNRadio.setChecked(true);
        } else if (current_language.equals(english)) {
            binding.ENRadio.setChecked(true);
        } else if (current_language.equals(japanese)) {
            binding.JPRadio.setChecked(true);
        } else if (current_language.equals(chinese)) {
            binding.CNRadio.setChecked(true);
        }
        return binding.getRoot();
    }

    private void setLanguage() {
        if(binding.VNRadio.isChecked()) {
            current_language = vietnamese;
        } else if (binding.ENRadio.isChecked()) {
            current_language = english;
        } else if (binding.JPRadio.isChecked()) {
            current_language = japanese;
        } else if (binding.CNRadio.isChecked()) {
            current_language = chinese;
        } else {
            current_language = english;
        }

        MainActivity.currentUser.setLanguage(current_language);
        FirebaseFirestore.getInstance().collection("User").document(MainActivity.currentUser.getId()).set(MainActivity.currentUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Set language", "Set user language successfully");
                        Activity activity = (Activity) getContext();
                        assert activity != null;
                        activity.recreate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Set language", "Set user language failed");
                        Activity activity = (Activity) getContext();
                        assert activity != null;
                        activity.recreate();
                    }
                });
    }
}