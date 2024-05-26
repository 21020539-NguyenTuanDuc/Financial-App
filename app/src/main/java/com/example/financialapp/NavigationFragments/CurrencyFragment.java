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
import com.example.financialapp.databinding.FragmentCurrencyBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class CurrencyFragment extends Fragment {
    FragmentCurrencyBinding binding;
    public static String current_symbol = "đ";
    public static String vnd_symbol = "đ";
    public static String usd_symbol = "$";
    public static String pound_symbol = "£";
    public static String euro_symbol = "€";
    public static String yen_symbol = "¥";
    public static String won_symbol = "₩";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCurrencyBinding.inflate(getLayoutInflater());

        if(current_symbol.equals(vnd_symbol)) {
            binding.vndRadio.setChecked(true);
        } else if (current_symbol.equals(usd_symbol)) {
            binding.usdRadio.setChecked(true);
        } else if (current_symbol.equals(pound_symbol)) {
            binding.poundRadio.setChecked(true);
        } else if (current_symbol.equals(euro_symbol)) {
            binding.euroRadio.setChecked(true);
        } else if (current_symbol.equals(yen_symbol)) {
            binding.yenRadio.setChecked(true);
        } else if (current_symbol.equals(won_symbol)) {
            binding.wonRadio.setChecked(true);
        }

        binding.setCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrency();
            }
        });

        return binding.getRoot();
    }

    private void setCurrency() {
        if(binding.vndRadio.isChecked()) {
            current_symbol = vnd_symbol;
        } else if (binding.usdRadio.isChecked()) {
            current_symbol = usd_symbol;
        } else if (binding.poundRadio.isChecked()) {
            current_symbol = pound_symbol;
        } else if (binding.euroRadio.isChecked()) {
            current_symbol = euro_symbol;
        } else if (binding.yenRadio.isChecked()) {
            current_symbol = yen_symbol;
        } else if (binding.wonRadio.isChecked()) {
            current_symbol = won_symbol;
        } else {
            current_symbol = vnd_symbol;
        }

        MainActivity.currentUser.setCurrency_symbol(current_symbol);
        FirebaseFirestore.getInstance().collection("User").document(MainActivity.currentUser.getId()).set(MainActivity.currentUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Set currency", "Set user currency successfully");
                        Activity activity = (Activity) getContext();
                        assert activity != null;
                        activity.recreate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Set currency", "Set user currency failed");
                        Activity activity = (Activity) getContext();
                        assert activity != null;
                        activity.recreate();
                    }
                });
    }
}