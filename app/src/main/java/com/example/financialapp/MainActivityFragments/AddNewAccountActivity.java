package com.example.financialapp.MainActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.financialapp.MainActivity;
import com.example.financialapp.Model.AccountModel;
import com.example.financialapp.databinding.ActivityAddNewAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNewAccountActivity extends AppCompatActivity {

    ActivityAddNewAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String id = FirebaseFirestore.getInstance().collection("Account").document().getId();
        String name = binding.accountNameET.getText().toString().trim();
        if (name.length() == 0) {
            binding.accountNameET.setError("Empty");
            return;
        }
        boolean cashChecked = binding.cashRadio.isChecked();
        String type;
        if (binding.initValueET.getText().toString().trim().length() == 0) {
            binding.initValueET.setError("Empty");
            return;
        }
        long initialValue = Integer.parseInt(binding.initValueET.getText().toString().trim());
        if (cashChecked) type = "Cash";
        else type = "Bank";


        AccountModel accountModel = new AccountModel(id, name, type, initialValue);

        FirebaseFirestore
                .getInstance()
                .collection("Account")
                .document(id)
                .set(accountModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddNewAccountActivity.this, "Account added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        startActivity(new Intent(AddNewAccountActivity.this, MainActivity.class));
        finish();
    }
}