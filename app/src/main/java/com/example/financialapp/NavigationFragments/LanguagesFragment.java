package com.example.financialapp.NavigationFragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;
import com.zeugmasolutions.localehelper.Locales;

import java.util.Locale;
import java.util.Objects;

public class LanguagesFragment extends Fragment {
    FragmentLanguagesBinding binding;
    public static String current_language = "English";
    public static String english = "English";
    public static String vietnamese = "Vietnamese";
    public static String japanese = "Japanese";
    public static String chinese = "Chinese";
    Locale locale;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLanguagesBinding.inflate(getLayoutInflater());

        binding.setLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguageApp();
            }
        });

        locale = getResources().getConfiguration().locale;

        if (locale.getLanguage().equals(new Locale("en").getLanguage())) {
            current_language = english;
        } else if (locale.getLanguage().equals(new Locale("vi").getLanguage())) {
            current_language = vietnamese;
        } else if (locale.getLanguage().equals(new Locale("jp").getLanguage())) {
            current_language = japanese;
        } else if (locale.getLanguage().equals(new Locale("zh").getLanguage())) {
            current_language = chinese;
        }


        if (current_language.equals(vietnamese)) {
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

    private void setLanguageApp() {
        locale = Locales.INSTANCE.getEnglish();
        if (binding.VNRadio.isChecked()) {
            current_language = vietnamese;
            locale = Locales.INSTANCE.getVietnamese();
        } else if (binding.ENRadio.isChecked()) {
            current_language = english;
            locale = Locales.INSTANCE.getEnglish();
        } else if (binding.JPRadio.isChecked()) {
            current_language = japanese;
            locale = Locales.INSTANCE.getJapanese();
        } else if (binding.CNRadio.isChecked()) {
            current_language = chinese;
            locale = Locale.CHINA;
        } else {
            current_language = english;
            locale = Locales.INSTANCE.getEnglish();
        }
        LocaleAwareCompatActivity activity = (LocaleAwareCompatActivity) getContext();
        assert activity != null;
        activity.updateLocale(locale);
    }
}